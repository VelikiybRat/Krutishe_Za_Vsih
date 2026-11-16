package org.example.krutishe_za_vsih;

import org.example.krutishe_za_vsih.dto.ReassignResult;
import org.example.krutishe_za_vsih.entity.AnimatorEntity;
import org.example.krutishe_za_vsih.entity.OrderEntity;
import org.example.krutishe_za_vsih.entity.OrderStatus;
import org.example.krutishe_za_vsih.entity.ProgramEntity;
import org.example.krutishe_za_vsih.exception.BookingConflictException;
import org.example.krutishe_za_vsih.repository.jpa.AnimatorJpaRepository;
import org.example.krutishe_za_vsih.repository.jpa.OrderJpaRepository;
import org.example.krutishe_za_vsih.repository.jpa.ProgramJpaRepository;
import org.example.krutishe_za_vsih.service.jpa.JpaOrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Інтеграційні тести ЛР6 на реальній PostgreSQL. Тест-класс НЕ позначено @Transactional,
 * щоб COMMIT і ROLLBACK сервісного методу були справжніми, а не відкотом самого тесту.
 */
@SpringBootTest
class JpaOrderServiceIntegrationTest {

    private static final LocalDate D1 = LocalDate.of(2031, 3, 10);
    private static final LocalDate D2 = LocalDate.of(2031, 3, 17);

    @Autowired JpaOrderService orderService;
    @Autowired AnimatorJpaRepository animatorRepository;
    @Autowired OrderJpaRepository orderRepository;
    @Autowired ProgramJpaRepository programRepository;

    private String tag;
    private AnimatorEntity from;
    private AnimatorEntity to;
    private ProgramEntity program;

    @BeforeEach
    void setUp() {
        tag = UUID.randomUUID().toString().substring(0, 8);
        from = animatorRepository.save(new AnimatorEntity("JpaFrom_" + tag, "TestRole_" + tag, 20, "test"));
        to = animatorRepository.save(new AnimatorEntity("JpaTo_" + tag, "TestRole_" + tag, 15, "test"));
        program = programRepository.findAllByOrderByIdAsc().get(0);
    }

    @AfterEach
    void cleanUp() {
        orderRepository.findAllByOrderByEventDateAscIdAsc().stream()
                .filter(o -> o.getClientName().endsWith(tag))
                .forEach(orderRepository::delete);
        animatorRepository.deleteById(from.getId());
        animatorRepository.deleteById(to.getId());
    }

    private void order(AnimatorEntity animator, LocalDate date) {
        orderRepository.save(new OrderEntity("JpaClient_" + date + "_" + tag, date, animator, program,
                new BigDecimal("1000.00")));
    }

    @Test
    void reassignCommitsAllOrders() {
        order(from, D1);
        order(from, D2);

        ReassignResult result = orderService.reassignOrders(from.getId(), to.getId());

        assertEquals(2, result.movedOrders());
        assertEquals(0, orderRepository.countByAnimatorId(from.getId()));
        assertEquals(2, orderRepository.countByAnimatorId(to.getId()));
    }

    @Test
    void reassignRollsBackEverythingOnConflict() {
        order(from, D1);
        order(from, D2);
        order(to, D2); // конфлікт на другій даті: перше замовлення вже перенесене в межах транзакції

        assertThrows(BookingConflictException.class,
                () -> orderService.reassignOrders(from.getId(), to.getId()));

        // ROLLBACK: у "from" залишилися обидва замовлення, у "to" - лише власне
        assertEquals(2, orderRepository.countByAnimatorId(from.getId()));
        assertEquals(1, orderRepository.countByAnimatorId(to.getId()));
    }

    @Test
    void queryMethodsAndJpqlWork() {
        order(from, D1);

        // методи за назвою
        List<AnimatorEntity> found = animatorRepository
                .findByRoleAndExperienceGreaterThanEqualOrderByExperienceDesc("TestRole_" + tag, 10);
        assertEquals(List.of(from.getId(), to.getId()), found.stream().map(AnimatorEntity::getId).toList());
        assertTrue(animatorRepository.findTop3ByOrderByExperienceDesc().size() <= 3);

        // @Query
        assertEquals(1, animatorRepository.searchByName("jpafrom_" + tag).size());
        assertEquals(1, orderRepository.findByAnimatorWithDetails(from.getId()).size());

        // @NamedQuery
        List<OrderEntity> named = orderRepository.findByStatusAndPeriod(OrderStatus.NEW, D1, D1);
        assertTrue(named.stream().anyMatch(o -> o.getClientName().endsWith(tag)));
    }
}