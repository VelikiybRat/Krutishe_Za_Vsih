package org.example.krutishe_za_vsih.controller.jpa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.krutishe_za_vsih.dto.CreateOrderRequest;
import org.example.krutishe_za_vsih.dto.OrderDto;
import org.example.krutishe_za_vsih.dto.ReassignRequest;
import org.example.krutishe_za_vsih.dto.ReassignResult;
import org.example.krutishe_za_vsih.entity.OrderStatus;
import org.example.krutishe_za_vsih.service.jpa.JpaOrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/jpa/orders")
@Tag(name = "JPA Order API", description = "Замовлення: зв'язки один-до-багатьох, пошук і транзакції")
public class JpaOrderRestController {

    private final JpaOrderService service;

    public JpaOrderRestController(JpaOrderService service) {
        this.service = service;
    }

    @Operation(summary = "Список замовлень",
            description = "Повертає замовлення разом з іменем аніматора та назвою програми, від найраніших дат. "
                    + "Без параметра - усі замовлення (@EntityGraph, один SQL-запит). "
                    + "З параметром animatorId - лише замовлення цього аніматора (JPQL @Query з JOIN FETCH).")
    @ApiResponse(responseCode = "200", description = "Список замовлень (може бути порожнім)")
    @GetMapping
    public List<OrderDto> getAll(
            @Parameter(description = "Необов'язково: фільтр за ID аніматора", example = "1")
            @RequestParam(required = false) Long animatorId) {
        return service.findAll(animatorId);
    }

    @Operation(summary = "Замовлення за ID", description = "Повертає одне замовлення з даними аніматора та програми.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Замовлення знайдено"),
            @ApiResponse(responseCode = "404", description = "Замовлення не існує", content = @Content)
    })
    @GetMapping("/{id}")
    public OrderDto getById(
            @Parameter(description = "Ідентифікатор замовлення", example = "1") @PathVariable long id) {
        return service.findById(id);
    }

    @Operation(summary = "Пошук за статусом і періодом дат",
            description = "Виконує іменований запит @NamedQuery OrderEntity.findByStatusAndPeriod: "
                    + "замовлення з вказаним статусом, дата яких лежить у проміжку [from, to] включно.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список знайдених замовлень"),
            @ApiResponse(responseCode = "400", description = "Невірний статус або from пізніше за to", content = @Content)
    })
    @GetMapping("/search")
    public List<OrderDto> search(
            @Parameter(description = "Статус замовлення", example = "CONFIRMED") @RequestParam OrderStatus status,
            @Parameter(description = "Початок періоду (ISO, yyyy-MM-dd)", example = "2026-12-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Кінець періоду (ISO, yyyy-MM-dd)", example = "2026-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.findByStatusAndPeriod(status, from, to);
    }

    @Operation(summary = "Кількість замовлень за статусом",
            description = "Query method countByStatus повертає кількість замовлень із вказаним статусом.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Кількість (число)"),
            @ApiResponse(responseCode = "400", description = "Невідомий статус", content = @Content)
    })
    @GetMapping("/count")
    public long count(
            @Parameter(description = "Статус замовлення", example = "NEW") @RequestParam OrderStatus status) {
        return service.countByStatus(status);
    }

    @Operation(summary = "Створити замовлення",
            description = "Створює замовлення для існуючих аніматора й програми. "
                    + "Перевіряє, що аніматор не має іншого активного замовлення на цю дату.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Замовлення створено"),
            @ApiResponse(responseCode = "400", description = "Некоректні дані (порожнє ім'я, дата в минулому, від'ємна ціна)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Аніматор або програма не існують", content = @Content),
            @ApiResponse(responseCode = "409", description = "Аніматор уже зайнятий на цю дату", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto created = service.create(request);
        return ResponseEntity.created(URI.create("/api/jpa/orders/" + created.id())).body(created);
    }

    @Operation(summary = "Змінити статус замовлення",
            description = "Оновлює лише поле status (наприклад, CONFIRMED -> CANCELLED). "
                    + "Повернення неактивного замовлення в NEW або CONFIRMED можливе, лише якщо дата вільна.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус оновлено"),
            @ApiResponse(responseCode = "400", description = "Невідомий статус", content = @Content),
            @ApiResponse(responseCode = "404", description = "Замовлення не існує", content = @Content),
            @ApiResponse(responseCode = "409", description = "Аніматор уже зайнятий на цю дату", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public OrderDto updateStatus(
            @Parameter(description = "Ідентифікатор замовлення", example = "1") @PathVariable long id,
            @Parameter(description = "Новий статус", example = "CANCELLED") @RequestParam OrderStatus status) {
        return service.updateStatus(id, status);
    }

    @Operation(summary = "Видалити замовлення", description = "Видаляє замовлення за ідентифікатором.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Видалено"),
            @ApiResponse(responseCode = "404", description = "Замовлення не існує", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ідентифікатор замовлення", example = "1") @PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Передати всі активні замовлення іншому аніматору (транзакція)",
            description = "Метод сервісу з @Transactional. Замовлення зі статусами NEW та CONFIRMED переносяться "
                    + "по одному в порядку дат. Якщо новий аніматор зайнятий хоча б на одну з дат, "
                    + "вся операція відкочується (ROLLBACK): жодне замовлення не змінюється, відповідь 409. "
                    + "Якщо конфліктів немає, усі зміни фіксуються разом (COMMIT).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Усі замовлення передано (COMMIT)"),
            @ApiResponse(responseCode = "400", description = "Не вказано ID або вони однакові", content = @Content),
            @ApiResponse(responseCode = "404", description = "Один з аніматорів не існує", content = @Content),
            @ApiResponse(responseCode = "409", description = "Конфлікт дат, транзакцію відкотено (ROLLBACK)", content = @Content)
    })
    @PostMapping("/reassign")
    public ReassignResult reassign(@Valid @RequestBody ReassignRequest request) {
        return service.reassignOrders(request.fromAnimatorId(), request.toAnimatorId());
    }
}