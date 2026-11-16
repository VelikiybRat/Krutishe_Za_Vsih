package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Результат успішної передачі замовлень")
public record ReassignResult(
        @Schema(description = "Скільки замовлень передано", example = "2") int movedOrders,
        @Schema(description = "Від кого передано", example = "5") Long fromAnimatorId,
        @Schema(description = "Кому передано", example = "6") Long toAnimatorId,
        @Schema(description = "Передані замовлення після зміни") List<OrderDto> orders
) {
}
