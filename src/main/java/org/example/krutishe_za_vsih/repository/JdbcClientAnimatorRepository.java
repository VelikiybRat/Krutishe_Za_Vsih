package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Animator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public class JdbcClientAnimatorRepository {

    private final JdbcClient jdbcClient;

    public JdbcClientAnimatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcClient = JdbcClient.create(jdbcTemplate);
    }

    public List<Animator> findAll() {
        String sql = """
                SELECT id, name, role, experience, notes
                FROM animators
                ORDER BY id
                """;

        return jdbcClient.sql(sql)
                .query((rs, rowNum) -> {
                    Animator animator = new Animator(
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getInt("experience"),
                            rs.getString("notes")
                    );

                    animator.setId(rs.getLong("id"));
                    return animator;
                })
                .list();
    }

    public Optional<Animator> findById(long id) {

        String sql = """
            SELECT id, name, role, experience, notes
            FROM animators
            WHERE id = ?
            """;

        return jdbcClient.sql(sql)
                .param(id)
                .query((rs, rowNum) -> {
                    Animator animator = new Animator(
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getInt("experience"),
                            rs.getString("notes")
                    );

                    animator.setId(rs.getLong("id"));
                    return animator;
                })
                .optional();
    }

    public long create(String name, String role,
                       int experience, String notes) {

        String sql = """
            INSERT INTO animators (name, role, experience, notes)
            VALUES (:name, :role, :experience, :notes)
            RETURNING id
            """;

        return jdbcClient.sql(sql)
                .param("name", name)
                .param("role", role)
                .param("experience", experience)
                .param("notes", notes)
                .query(Long.class)
                .single();
    }

    public int update(long id, String name, String role,
                      int experience, String notes) {

        String sql = """
            UPDATE animators
            SET name = :name,
                role = :role,
                experience = :experience,
                notes = :notes
            WHERE id = :id
            """;

        return jdbcClient.sql(sql)
                .param("name", name)
                .param("role", role)
                .param("experience", experience)
                .param("notes", notes)
                .param("id", id)
                .update();
    }

    public int deleteById(long id) {

        String sql = """
            DELETE FROM animators
            WHERE id = :id
            """;

        return jdbcClient.sql(sql)
                .param("id", id)
                .update();
    }

    public List<Animator> findByRoleAndMinExperience(
            String role, int minExperience) {

        String sql = """
            SELECT id, name, role, experience, notes
            FROM animators
            WHERE role = :role
              AND experience >= :minExperience
            ORDER BY experience DESC
            """;

        return jdbcClient.sql(sql)
                .param("role", role)
                .param("minExperience", minExperience)
                .query((rs, rowNum) -> {
                    Animator animator = new Animator(
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getInt("experience"),
                            rs.getString("notes")
                    );

                    animator.setId(rs.getLong("id"));
                    return animator;
                })
                .list();
    }

}
