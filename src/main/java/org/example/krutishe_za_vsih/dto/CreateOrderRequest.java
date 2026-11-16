package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Дані для створення замовлення")
public record CreateOrderRequest(
        @Schema(description = "Ім'я замовника", example = "Анна Мороз", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100) String clientName,
        @Schema(description = "Дата свята (не в минулому)", example = "2027-01-10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @FutureOrPresent LocalDate eventDate,
        @Schema(description = "ID аніматора", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long animatorId,
        @Schema(description = "ID програми", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long programId,
        @Schema(description = "Вартість, грн (>= 0)", example = "3000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @DecimalMin("0.00") BigDecimal price
) {
}