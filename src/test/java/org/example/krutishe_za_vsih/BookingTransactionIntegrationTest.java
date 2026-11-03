
package org.example.krutishe_za_vsih;

import org.example.krutishe_za_vsih.service.BookingTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingTransactionIntegrationTest {

    @Autowired
    private BookingTransactionService bookingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void rollbackOnInvalidProgram() {

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String animatorName = "JUnitRollback_" + suffix;
        String clientName = "JUnitClient_" + suffix;

        System.out.println("=== JUNIT ROLLBACK TEST ===");

        try {
            assertThrows(
                    DataIntegrityViolationException.class,
                    () -> bookingService.createBookingWithNewAnimator(
                            animatorName,
                            clientName,
                            LocalDate.of(2026, 12, 1),
                            999999L
                    )
            );

            Integer animators = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM animators WHERE name = ?",
                    Integer.class,
                    animatorName
            );

            Integer orders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders WHERE client_name = ?",
                    Integer.class,
                    clientName
            );

            System.out.println("Аніматорів після помилки: " + animators);
            System.out.println("Замовлень після помилки: " + orders);

            assertEquals(0, animators);
            assertEquals(0, orders);

            System.out.println("ROLLBACK TEST PASSED");

        } finally {
            jdbcTemplate.update(
                    "DELETE FROM orders WHERE client_name = ?",
                    clientName
            );
            jdbcTemplate.update(
                    "DELETE FROM animators WHERE name = ?",
                    animatorName
            );
        }
    }

    @Test
    void commitPersistsBothRecords() {

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String animatorName = "JUnitCommit_" + suffix;
        String clientName = "JUnitCommitClient_" + suffix;

        Long programId = jdbcTemplate.queryForObject(
                "SELECT id FROM programs ORDER BY id LIMIT 1",
                Long.class
        );

        assertNotNull(programId);

        System.out.println("=== JUNIT COMMIT TEST ===");

        try {
            long animatorId = bookingService.createBookingWithNewAnimator(
                    animatorName,
                    clientName,
                    LocalDate.of(2026, 12, 5),
                    programId
            );

            Integer animators = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*) FROM animators
                    WHERE id = ? AND name = ?
                    """,
                    Integer.class,
                    animatorId, animatorName
            );

            Integer orders = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*) FROM orders
                    WHERE animator_id = ?
                      AND client_name = ?
                      AND program_id = ?
                    """,
                    Integer.class,
                    animatorId, clientName, programId
            );

            System.out.println("Створено аніматорів: " + animators);
            System.out.println("Створено замовлень: " + orders);

            assertEquals(1, animators);
            assertEquals(1, orders);

            System.out.println("COMMIT TEST PASSED");

        } finally {
            // Видаляємо лише записи цього тесту
            jdbcTemplate.update(
                    "DELETE FROM orders WHERE client_name = ?",
                    clientName
            );

            jdbcTemplate.update(
                    "DELETE FROM animators WHERE name = ?",
                    animatorName
            );
        }
    }
}