package org.example.krutishe_za_vsih.repository.jpa;

import org.example.krutishe_za_vsih.dto.AnimatorOrderCount;
import org.example.krutishe_za_vsih.entity.AnimatorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnimatorJpaRepository extends CrudRepository<AnimatorEntity, Long> {

    // ---- 5.2: методи пошуку, які Spring Data генерує з назви ----

    /** WHERE role = ? AND experience >= ? ORDER BY experience DESC */
    List<AnimatorEntity> findByRoleAndExperienceGreaterThanEqualOrderByExperienceDesc(
            String role, int minExperience);

    /** Обмеження кількості результатів ключовим словом Top3: LIMIT 3. */
    List<AnimatorEntity> findTop3ByOrderByExperienceDesc();

    List<AnimatorEntity> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCase(String name);

    // ---- 5.1.1: JPQL через @Query ----

    @Query("""
            SELECT a FROM AnimatorEntity a
            WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :namePart, '%'))
            ORDER BY a.name
            """)
    List<AnimatorEntity> searchByName(@Param("namePart") String namePart);

    /** Інтерфейсна проекція: тільки name та кількість замовлень, без завантаження сутностей. */
    @Query("""
            SELECT a.name AS name, COUNT(o) AS ordersCount
            FROM AnimatorEntity a LEFT JOIN a.orders o
            GROUP BY a.id, a.name
            ORDER BY COUNT(o) DESC, a.name
            """)
    List<AnimatorOrderCount> findOrderCounts();

    // ---- Динамічна проекція: один метод, різні формати результату ----

    <T> List<T> findByRoleOrderByNameAsc(String role, Class<T> type);
}