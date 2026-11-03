
package org.example.krutishe_za_vsih.service;

import org.example.krutishe_za_vsih.repository.JdbcAnimatorRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BookingTransactionService {

    private final JdbcAnimatorRepository animatorRepository;
    private final JdbcTemplate jdbcTemplate;

    public BookingTransactionService(
            JdbcAnimatorRepository animatorRepository,
            JdbcTemplate jdbcTemplate) {

        this.animatorRepository = animatorRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public long createBookingWithNewAnimator(
            String animatorName,
            String clientName,
            LocalDate eventDate,
            long programId) {

        // 1. Створюємо аніматора
        long animatorId = animatorRepository.create(
                animatorName,
                "Аніматор",
                1,
                "Створений у межах транзакції"
        );

        // 2. Створюємо замовлення
        String sql = """
                INSERT INTO orders
                (client_name, event_date, animator_id, program_id)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                clientName,
                java.sql.Date.valueOf(eventDate),
                animatorId,
                programId
        );

        return animatorId;
    }
}

