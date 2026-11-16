package org.example.krutishe_za_vsih.repository.jpa;

import org.example.krutishe_za_vsih.entity.OrderEntity;
import org.example.krutishe_za_vsih.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderJpaRepository extends CrudRepository<OrderEntity, Long> {

    // ---- 5.1.1: JPQL через @Query. JOIN FETCH підтягує аніматора й програму одним SQL ----

    @Query("""
            SELECT o FROM OrderEntity o
            JOIN FETCH o.animator
            JOIN FETCH o.program
            WHERE o.animator.id = :animatorId
            ORDER BY o.eventDate
            """)
    List<OrderEntity> findByAnimatorWithDetails(@Param("animatorId") Long animatorId);

    // ---- 5.1.2: JPQL через @NamedQuery ----
    // Сам запит оголошено над класом OrderEntity з іменем "OrderEntity.findByStatusAndPeriod".

    List<OrderEntity> findByStatusAndPeriod(@Param("status") OrderStatus status,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);

    // ---- 5.2: методи за назвою ----

    boolean existsByAnimatorIdAndEventDateAndStatusIn(Long animatorId, LocalDate eventDate,
                                                      List<OrderStatus> statuses);

    long countByStatus(OrderStatus status);

    long countByAnimatorId(Long animatorId);

    List<OrderEntity> findByAnimatorIdOrderByEventDateAscIdAsc(Long animatorId);

    // ---- @EntityGraph: той самий ефект, що й JOIN FETCH, але без написання JPQL ----

    @EntityGraph(attributePaths = {"animator", "program"})
    List<OrderEntity> findAllByOrderByEventDateAscIdAsc();
}