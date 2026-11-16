package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.krutishe_za_vsih.entity.AnimatorEntity;

@Schema(description = "Аніматор у відповіді API")
public record AnimatorDto(
        @Schema(description = "Унікальний ідентифікатор", example = "1") Long id,
        @Schema(description = "Ім'я аніматора", example = "Іван") String name,
        @Schema(description = "Роль у команді", example = "Ведучий") String role,
        @Schema(description = "Досвід роботи, років", example = "5") int experience,
        @Schema(description = "Примітки", example = "Організація квестів") String notes
) {
    public static AnimatorDto from(AnimatorEntity a) {
        return new AnimatorDto(a.getId(), a.getName(), a.getRole(), a.getExperience(), a.getNotes());
    }
}