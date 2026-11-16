package org.example.krutishe_za_vsih.service.jpa;

import org.example.krutishe_za_vsih.dto.AnimatorDto;
import org.example.krutishe_za_vsih.dto.AnimatorNameOnly;
import org.example.krutishe_za_vsih.dto.AnimatorOrderCount;
import org.example.krutishe_za_vsih.dto.AnimatorRequest;
import org.example.krutishe_za_vsih.entity.AnimatorEntity;
import org.example.krutishe_za_vsih.exception.BookingConflictException;
import org.example.krutishe_za_vsih.repository.jpa.AnimatorJpaRepository;
import org.example.krutishe_za_vsih.repository.jpa.OrderJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class JpaAnimatorServiceImpl implements JpaAnimatorService {

    private final AnimatorJpaRepository animatorRepository;
    private final OrderJpaRepository orderRepository;

    public JpaAnimatorServiceImpl(AnimatorJpaRepository animatorRepository,
                                  OrderJpaRepository orderRepository) {
        this.animatorRepository = animatorRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<AnimatorDto> findAll() {
        return animatorRepository.findAllByOrderByIdAsc().stream().map(AnimatorDto::from).toList();
    }

    @Override
    public AnimatorDto findById(long id) {
        return AnimatorDto.from(getEntity(id));
    }

    @Override
    public List<AnimatorDto> filter(String role, int minExperience) {
        return animatorRepository
                .findByRoleAndExperienceGreaterThanEqualOrderByExperienceDesc(role, minExperience)
                .stream().map(AnimatorDto::from).toList();
    }

    @Override
    public List<AnimatorDto> top3ByExperience() {
        return animatorRepository.findTop3ByOrderByExperienceDesc().stream().map(AnimatorDto::from).toList();
    }

    @Override
    public List<AnimatorDto> searchByName(String namePart) {
        return animatorRepository.searchByName(namePart).stream().map(AnimatorDto::from).toList();
    }

    @Override
    public List<AnimatorOrderCount> orderCounts() {
        return animatorRepository.findOrderCounts();
    }

    @Override
    public List<AnimatorNameOnly> namesByRole(String role) {
        return animatorRepository.findByRoleOrderByNameAsc(role, AnimatorNameOnly.class);
    }

    @Override
    @Transactional
    public AnimatorDto create(AnimatorRequest r) {
        AnimatorEntity saved = animatorRepository.save(
                new AnimatorEntity(r.name().trim(), r.role().trim(), r.experience(), r.notes()));
        return AnimatorDto.from(saved);
    }

    @Override
    @Transactional
    public AnimatorDto update(long id, AnimatorRequest r) {
        AnimatorEntity a = getEntity(id);
        a.setName(r.name().trim());
        a.setRole(r.role().trim());
        a.setExperience(r.experience());
        a.setNotes(r.notes());
        // save() не обов'язковий: сутність у транзакції "managed", зміни підуть в БД при COMMIT
        return AnimatorDto.from(animatorRepository.save(a));
    }

    @Override
    @Transactional
    public void delete(long id) {
        AnimatorEntity a = getEntity(id);
        long orders = orderRepository.countByAnimatorId(id);
        if (orders > 0) {
            throw new BookingConflictException(
                    "Не можна видалити аніматора: у нього є замовлення (" + orders + ")");
        }
        animatorRepository.delete(a);
    }

    private AnimatorEntity getEntity(long id) {
        return animatorRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Аніматора з id=" + id + " не знайдено"));
    }
}