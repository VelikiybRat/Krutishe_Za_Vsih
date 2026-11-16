package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Дані для створення або оновлення аніматора")
public record AnimatorRequest(
        @Schema(description = "Ім'я аніматора", example = "Тетяна", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100) String name,
        @Schema(description = "Роль у команді", example = "Аніматор", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100) String role,
        @Schema(description = "Досвід роботи, років (>= 0)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(0) int experience,
        @Schema(description = "Довільні примітки", example = "Грим і аквагрим") String notes
) {
}
