package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.krutishe_za_vsih.entity.ProgramEntity;

@Schema(description = "Розважальна програма")
public record ProgramDto(
        @Schema(description = "Ідентифікатор програми", example = "1") Long id,
        @Schema(description = "Назва програми", example = "Roblox Квест") String name,
        @Schema(description = "Опис програми") String description
) {
    public static ProgramDto from(ProgramEntity p) {
        return new ProgramDto(p.getId(), p.getName(), p.getDescription());
    }
}