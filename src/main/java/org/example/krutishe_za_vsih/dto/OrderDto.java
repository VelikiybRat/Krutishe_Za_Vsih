package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.krutishe_za_vsih.entity.OrderEntity;
import org.example.krutishe_za_vsih.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Замовлення з іменем аніматора та назвою програми")
public record OrderDto(
        @Schema(description = "Ідентифікатор замовлення", example = "10") Long id,
        @Schema(description = "Ім'я замовника", example = "Олена Коваль") String clientName,
        @Schema(description = "Дата свята", example = "2026-12-05") LocalDate eventDate,
        @Schema(description = "Статус замовлення") OrderStatus status,
        @Schema(description = "Вартість, грн", example = "3500.00") BigDecimal price,
        @Schema(description = "ID аніматора", example = "1") Long animatorId,
        @Schema(description = "Ім'я аніматора", example = "Іван") String animatorName,
        @Schema(description = "ID програми", example = "1") Long programId,
        @Schema(description = "Назва програми", example = "Roblox Квест") String programName
) {
    /** Викликати лише коли animator і program вже завантажені (усередині транзакції). */
    public static OrderDto from(OrderEntity o) {
        return new OrderDto(
                o.getId(), o.getClientName(), o.getEventDate(), o.getStatus(), o.getPrice(),
                o.getAnimator().getId(), o.getAnimator().getName(),
                o.getProgram().getId(), o.getProgram().getName());
    }
}