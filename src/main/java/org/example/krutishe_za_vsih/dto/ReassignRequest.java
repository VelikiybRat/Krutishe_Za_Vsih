package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запит на передачу всіх активних замовлень від одного аніматора іншому")
public record ReassignRequest(
        @Schema(description = "ID аніматора, у якого забираємо замовлення", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long fromAnimatorId,
        @Schema(description = "ID аніматора, якому передаємо замовлення", example = "6", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long toAnimatorId
) {
}