package org.example.krutishe_za_vsih.controller.jpa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.krutishe_za_vsih.dto.AnimatorDto;
import org.example.krutishe_za_vsih.dto.AnimatorNameOnly;
import org.example.krutishe_za_vsih.dto.AnimatorOrderCount;
import org.example.krutishe_za_vsih.dto.AnimatorRequest;
import org.example.krutishe_za_vsih.service.jpa.JpaAnimatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jpa/animators")
@Tag(name = "JPA Animator API", description = "Аніматори: Spring Data JPA (Hibernate) + PostgreSQL")
public class JpaAnimatorRestController {

    private final JpaAnimatorService service;

    public JpaAnimatorRestController(JpaAnimatorService service) {
        this.service = service;
    }

    @Operation(summary = "Список усіх аніматорів",
            description = "Повертає всіх аніматорів, відсортованих за ідентифікатором. "
                    + "Дані читаються через CrudRepository (метод findAllByOrderByIdAsc).")
    @ApiResponse(responseCode = "200", description = "Список успішно отримано (може бути порожнім)")
    @GetMapping
    public List<AnimatorDto> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Аніматор за ID",
            description = "Повертає одного аніматора за його первинним ключем.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аніматора знайдено"),
            @ApiResponse(responseCode = "404", description = "Аніматора з таким ID не існує", content = @Content)
    })
    @GetMapping("/{id}")
    public AnimatorDto getById(
            @Parameter(description = "Ідентифікатор аніматора", example = "1") @PathVariable long id) {
        return service.findById(id);
    }

    @Operation(summary = "Фільтр за роллю та мінімальним досвідом",
            description = "Query method за назвою: findByRoleAndExperienceGreaterThanEqualOrderByExperienceDesc. "
                    + "Повертає аніматорів із точно такою роллю та досвідом не меншим за заданий, від найдосвідченіших.")
    @ApiResponse(responseCode = "200", description = "Список (порожній, якщо нікого не знайдено)")
    @GetMapping("/filter")
    public List<AnimatorDto> filter(
            @Parameter(description = "Роль у команді, точний збіг", example = "Аніматор") @RequestParam String role,
            @Parameter(description = "Мінімальний досвід, років", example = "2") @RequestParam(defaultValue = "0") int minExperience) {
        return service.filter(role, minExperience);
    }

    @Operation(summary = "Топ-3 за досвідом",
            description = "Query method findTop3ByOrderByExperienceDesc: ключове слово Top3 обмежує вибірку трьома записами (LIMIT 3).")
    @ApiResponse(responseCode = "200", description = "Не більше трьох аніматорів")
    @GetMapping("/top")
    public List<AnimatorDto> top() {
        return service.top3ByExperience();
    }

    @Operation(summary = "Пошук за частиною імені",
            description = "JPQL-запит через @Query: LIKE без урахування регістру.")
    @ApiResponse(responseCode = "200", description = "Список знайдених (може бути порожнім)")
    @GetMapping("/search")
    public List<AnimatorDto> search(
            @Parameter(description = "Частина імені", example = "ва") @RequestParam String name) {
        return service.searchByName(name);
    }

    @Operation(summary = "Кількість замовлень на кожного аніматора",
            description = "JPQL @Query з LEFT JOIN і GROUP BY. Результат - інтерфейсна проекція "
                    + "(тільки ім'я й лічильник, без завантаження сутностей).")
    @ApiResponse(responseCode = "200", description = "Статистика отримана",
            content = @Content(schema = @Schema(implementation = AnimatorOrderCount.class)))
    @GetMapping("/stats")
    public List<AnimatorOrderCount> stats() {
        return service.orderCounts();
    }

    @Operation(summary = "Імена аніматорів певної ролі (динамічна проекція)",
            description = "Метод репозиторію <T> List<T> findByRoleOrderByNameAsc(String, Class<T>) "
                    + "повертає лише поле name у вигляді DTO-record AnimatorNameOnly.")
    @ApiResponse(responseCode = "200", description = "Список імен")
    @GetMapping("/names")
    public List<AnimatorNameOnly> names(
            @Parameter(description = "Роль у команді", example = "Аніматор") @RequestParam String role) {
        return service.namesByRole(role);
    }

    @Operation(summary = "Створити аніматора",
            description = "Створює запис у таблиці animators. Ідентифікатор генерує PostgreSQL (identity). "
                    + "У відповіді є заголовок Location із посиланням на новий ресурс.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Аніматора створено"),
            @ApiResponse(responseCode = "400", description = "Порожнє ім'я/роль або від'ємний досвід", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AnimatorDto> create(@Valid @RequestBody AnimatorRequest request) {
        AnimatorDto created = service.create(request);
        return ResponseEntity.created(URI.create("/api/jpa/animators/" + created.id())).body(created);
    }

    @Operation(summary = "Оновити аніматора",
            description = "Повністю замінює поля name, role, experience та notes існуючого аніматора.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аніматора оновлено"),
            @ApiResponse(responseCode = "400", description = "Некоректні дані", content = @Content),
            @ApiResponse(responseCode = "404", description = "Аніматора не знайдено", content = @Content)
    })
    @PutMapping("/{id}")
    public AnimatorDto update(
            @Parameter(description = "Ідентифікатор аніматора", example = "1") @PathVariable long id,
            @Valid @RequestBody AnimatorRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Видалити аніматора",
            description = "Видаляє аніматора, якщо в нього немає замовлень. Інакше повертає 409, дані не змінюються.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Видалено"),
            @ApiResponse(responseCode = "404", description = "Аніматора не знайдено", content = @Content),
            @ApiResponse(responseCode = "409", description = "У аніматора є замовлення", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ідентифікатор аніматора", example = "1") @PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}