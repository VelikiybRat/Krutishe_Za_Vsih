
package org.example.krutishe_za_vsih;

import org.example.krutishe_za_vsih.service.BookingTransactionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


public class DatabaseCheck implements CommandLineRunner {

    private final BookingTransactionService bookingService;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseCheck(
            BookingTransactionService bookingService,
            JdbcTemplate jdbcTemplate) {
        this.bookingService = bookingService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {

        System.out.println("=== TRANSACTION COMMIT TEST ===");

        String animatorName = "CommitTestAnimator";
        String clientName = "CommitTestClient";

        Integer animatorsBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM animators WHERE name = ?",
                Integer.class, animatorName
        );

        Integer ordersBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE client_name = ?",
                Integer.class, clientName
        );

        System.out.println("Аніматорів ДО: " + animatorsBefore);
        System.out.println("Замовлень ДО: " + ordersBefore);

        if (animatorsBefore > 0 || ordersBefore > 0) {
            System.out.println(
                    "Тестові записи вже існують. Повторне створення пропущено."
            );
            return;
        }

        long animatorId = bookingService.createBookingWithNewAnimator(
                animatorName,
                clientName,
                LocalDate.of(2026, 11, 26),
                1L
        );

        Integer animatorsAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM animators WHERE name = ?",
                Integer.class, animatorName
        );

        Integer ordersAfter = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM orders
                WHERE client_name = ?
                  AND animator_id = ?
                  AND program_id = ?
                """,
                Integer.class, clientName, animatorId, 1L
        );

        System.out.println("Створений ID аніматора: " + animatorId);
        System.out.println("Аніматорів ПІСЛЯ: " + animatorsAfter);
        System.out.println("Пов'язаних замовлень ПІСЛЯ: " + ordersAfter);

        boolean success =
                animatorsAfter == 1 && ordersAfter == 1;

        System.out.println("COMMIT SUCCESS: " + success);
        System.out.println("=== END TRANSACTION TEST ===");
    }
}
