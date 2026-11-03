package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Animator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public class JdbcAnimatorRepository implements AnimatorDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAnimatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Animator> findAll() {
        String sql = """
                SELECT id, name, role, experience, notes
                FROM animators
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Animator animator = new Animator(
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getInt("experience"),
                    rs.getString("notes")
            );

            animator.setId(rs.getLong("id"));
            return animator;
        });
    }

    public Optional<Animator> findById(long id) {
        String sql = """
            SELECT id, name, role, experience, notes
            FROM animators
            WHERE id = ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Animator animator = new Animator(
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getInt("experience"),
                    rs.getString("notes")
            );

            animator.setId(rs.getLong("id"));
            return animator;
        }, id).stream().findFirst();
    }

    public long create(String name, String role,
                       int experience, String notes) {

        String sql = """
            INSERT INTO animators (name, role, experience, notes)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;

        Long id = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                name, role, experience, notes
        );

        if (id == null) {
            throw new IllegalStateException(
                    "Не вдалося отримати ID нового аніматора"
            );
        }

        return id;
    }

    public int update(long id, String name, String role,
                      int experience, String notes) {

        String sql = """
            UPDATE animators
            SET name = ?,
                role = ?,
                experience = ?,
                notes = ?
            WHERE id = ?
            """;

        return jdbcTemplate.update(
                sql,
                name,
                role,
                experience,
                notes,
                id
        );
    }

    public int deleteById(long id) {

        String sql = """
            DELETE FROM animators
            WHERE id = ?
            """;

        return jdbcTemplate.update(sql, id);
    }

    public List<Animator> findByRoleAndMinExperience(
            String role, int minExperience) {

        String sql = """
            SELECT id, name, role, experience, notes
            FROM animators
            WHERE role = ? AND experience >= ?
            ORDER BY experience DESC
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Animator animator = new Animator(
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getInt("experience"),
                            rs.getString("notes")
                    );

                    animator.setId(rs.getLong("id"));
                    return animator;
                },
                role,
                minExperience
        );
    }
}
