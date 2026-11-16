package org.example.krutishe_za_vsih.controller.jpa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.krutishe_za_vsih.dto.ProgramDto;
import org.example.krutishe_za_vsih.service.jpa.JpaProgramService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jpa/programs")
@Tag(name = "JPA Program API", description = "Розважальні програми (довідник для замовлень)")
public class JpaProgramRestController {

    private final JpaProgramService service;

    public JpaProgramRestController(JpaProgramService service) {
        this.service = service;
    }

    @Operation(summary = "Список програм",
            description = "Повертає всі програми. Якщо передано параметр name, працює query method "
                    + "findByNameContainingIgnoreCaseOrderByName (пошук за частиною назви).")
    @ApiResponse(responseCode = "200", description = "Список програм (може бути порожнім)")
    @GetMapping
    public List<ProgramDto> getAll(
            @Parameter(description = "Необов'язково: частина назви програми", example = "кві")
            @RequestParam(required = false) String name) {
        return service.findAll(name);
    }
}