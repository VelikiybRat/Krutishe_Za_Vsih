package org.example.krutishe_za_vsih.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Interface-based проекція Spring Data: репозиторій читає з БД лише два поля
 * (name та кількість замовлень), а не всю сутність.
 */
@Schema(description = "Аніматор і кількість його замовлень")
public interface AnimatorOrderCount {

    @Schema(description = "Ім'я аніматора", example = "Іван")
    String getName();

    @Schema(description = "Кількість замовлень", example = "2")
    Long getOrdersCount();
}