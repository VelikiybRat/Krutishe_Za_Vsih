package org.example.krutishe_za_vsih.service.jpa;

import org.example.krutishe_za_vsih.dto.CreateOrderRequest;
import org.example.krutishe_za_vsih.dto.OrderDto;
import org.example.krutishe_za_vsih.dto.ReassignResult;
import org.example.krutishe_za_vsih.entity.AnimatorEntity;
import org.example.krutishe_za_vsih.entity.OrderEntity;
import org.example.krutishe_za_vsih.entity.OrderStatus;
import org.example.krutishe_za_vsih.entity.ProgramEntity;
import org.example.krutishe_za_vsih.exception.BookingConflictException;
import org.example.krutishe_za_vsih.repository.jpa.AnimatorJpaRepository;
import org.example.krutishe_za_vsih.repository.jpa.OrderJpaRepository;
import org.example.krutishe_za_vsih.repository.jpa.ProgramJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JpaOrderServiceImpl implements JpaOrderService {

    private static final List<OrderStatus> ACTIVE =
            List.of(OrderStatus.NEW, OrderStatus.CONFIRMED);

    private final OrderJpaRepository orderRepository;
    private final AnimatorJpaRepository animatorRepository;
    private final ProgramJpaRepository programRepository;

    public JpaOrderServiceImpl(OrderJpaRepository orderRepository,
                               AnimatorJpaRepository animatorRepository,
                               ProgramJpaRepository programRepository) {
        this.orderRepository = orderRepository;
        this.animatorRepository = animatorRepository;
        this.programRepository = programRepository;
    }

    @Override
    public List<OrderDto> findAll(Long animatorId) {
        List<OrderEntity> orders = (animatorId == null)
                ? orderRepository.findAllByOrderByEventDateAscIdAsc()
                : orderRepository.findByAnimatorWithDetails(animatorId);
        return orders.stream().map(OrderDto::from).toList();
    }

    @Override
    public OrderDto findById(long id) {
        return OrderDto.from(getEntity(id));
    }

    @Override
    public List<OrderDto> findByStatusAndPeriod(OrderStatus status, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата 'to' раніша за 'from'");
        }
        return orderRepository.findByStatusAndPeriod(status, from, to).stream().map(OrderDto::from).toList();
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional
    public OrderDto create(CreateOrderRequest r) {
        AnimatorEntity animator = getAnimator(r.animatorId());
        ProgramEntity program = programRepository.findById(r.programId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Програму з id=" + r.programId() + " не знайдено"));

        if (orderRepository.existsByAnimatorIdAndEventDateAndStatusIn(animator.getId(), r.eventDate(), ACTIVE)) {
            throw new BookingConflictException(
                    "Аніматор " + animator.getName() + " уже зайнятий " + r.eventDate());
        }

        OrderEntity saved = orderRepository.save(
                new OrderEntity(r.clientName().trim(), r.eventDate(), animator, program, r.price()));
        return OrderDto.from(saved);
    }

    @Override
    @Transactional
    public OrderDto updateStatus(long id, OrderStatus status) {
        OrderEntity order = getEntity(id);
        if (status.isActive() && !order.getStatus().isActive()
                && orderRepository.existsByAnimatorIdAndEventDateAndStatusIn(
                order.getAnimator().getId(), order.getEventDate(), ACTIVE)) {
            throw new BookingConflictException("Аніматор уже зайнятий на " + order.getEventDate());
        }
        order.setStatus(status);
        return OrderDto.from(order);
    }

    @Override
    @Transactional
    public void delete(long id) {
        orderRepository.delete(getEntity(id));
    }

    /**
     * Передає всі активні замовлення (NEW, CONFIRMED) від одного аніматора іншому.
     *
     * Замовлення переносяться по одному в порядку дат. Якщо новий аніматор уже зайнятий
     * на одну з дат, кидається BookingConflictException (RuntimeException), і Spring відкочує
     * ВСІ зміни цієї транзакції, навіть ті, які Hibernate вже надіслав в БД.
     */
    @Override
    @Transactional
    public ReassignResult reassignOrders(long fromId, long toId) {
        if (fromId == toId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "fromAnimatorId і toAnimatorId мають відрізнятися");
        }
        AnimatorEntity to = getAnimator(toId);
        getAnimator(fromId); // перевірка існування, інакше 404

        List<OrderEntity> toMove = orderRepository.findByAnimatorIdOrderByEventDateAscIdAsc(fromId).stream()
                .filter(o -> o.getStatus().isActive())
                .toList();

        for (OrderEntity order : toMove) {
            if (orderRepository.existsByAnimatorIdAndEventDateAndStatusIn(toId, order.getEventDate(), ACTIVE)) {
                throw new BookingConflictException(
                        "Аніматор " + to.getName() + " уже зайнятий " + order.getEventDate()
                                + ". Передачу скасовано, жодне замовлення не змінено.");
            }
            order.setAnimator(to); // dirty checking: UPDATE піде при flush або COMMIT
        }

        return new ReassignResult(toMove.size(), fromId, toId,
                toMove.stream().map(OrderDto::from).toList());
    }

    private OrderEntity getEntity(long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Замовлення з id=" + id + " не знайдено"));
    }

    private AnimatorEntity getAnimator(long id) {
        return animatorRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Аніматора з id=" + id + " не знайдено"));
    }
}