package org.example.krutishe_za_vsih.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.krutishe_za_vsih.model.Animator;
import org.example.krutishe_za_vsih.service.CrmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animators")
@Tag(name = "Animator API", description = "CRUD операції для керування командою аніматорів (RESTful вебсервіс)")
public class AnimatorRestController {

    private final CrmService crmService;
    private final List<Animator> apiDatabase = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public AnimatorRestController(CrmService crmService) {
        this.crmService = crmService;
    }

    // Ініціалізація імітаційної БД для API
    @PostConstruct
    public void init() {
        for (Animator a : crmService.getAllAnimators()) {
            a.setId(idGenerator.getAndIncrement());
            apiDatabase.add(a);
        }
    }

    // 1. READ ALL + Фільтрація + Пагінація (Вимога 2.2)
    @Operation(summary = "Отримати всіх аніматорів", description = "Повертає список аніматорів з підтримкою фільтрації за досвідом та пагінації.")
    @ApiResponse(responseCode = "200", description = "Успішне отримання списку")
    @GetMapping
    public ResponseEntity<List<Animator>> getAllAnimators(
            @Parameter(description = "Мінімальний досвід (років)") @RequestParam(required = false) Integer minExperience,
            @Parameter(description = "Номер сторінки (починається з 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Кількість елементів на сторінці") @RequestParam(defaultValue = "10") int size) {

        // Фільтрація
        List<Animator> filtered = apiDatabase.stream()
                .filter(a -> minExperience == null || a.getExperience() >= minExperience)
                .collect(Collectors.toList());

        // Пагінація
        int start = Math.min(page * size, filtered.size());
        int end = Math.min((page + 1) * size, filtered.size());

        return ResponseEntity.ok(filtered.subList(start, end));
    }

    // 2. READ ONE
    @Operation(summary = "Знайти аніматора за ID", description = "Повертає одного аніматора за його унікальним ідентифікатором.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аніматора знайдено"),
            @ApiResponse(responseCode = "404", description = "Аніматора не знайдено", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Animator> getAnimatorById(@PathVariable Long id) {
        return apiDatabase.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 3. CREATE (Вимога 2.1)
    @Operation(summary = "Створити нового аніматора", description = "Додає нового аніматора до системи.")
    @ApiResponse(responseCode = "201", description = "Аніматора успішно створено")
    @PostMapping
    public ResponseEntity<Animator> createAnimator(@RequestBody Animator newAnimator) {
        newAnimator.setId(idGenerator.getAndIncrement());
        apiDatabase.add(newAnimator);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAnimator); // Статус 201 Created (Вимога 2.4)
    }

    // 4. UPDATE (Повне оновлення)
    @Operation(summary = "Повне оновлення аніматора", description = "Оновлює всі поля існуючого аніматора.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успішно оновлено"),
            @ApiResponse(responseCode = "404", description = "Аніматора не знайдено")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Animator> updateAnimator(@PathVariable Long id, @RequestBody Animator updatedAnimator) {
        for (int i = 0; i < apiDatabase.size(); i++) {
            if (apiDatabase.get(i).getId().equals(id)) {
                updatedAnimator.setId(id);
                apiDatabase.set(i, updatedAnimator);
                return ResponseEntity.ok(updatedAnimator);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 5. PATCH (Часткове оновлення - Вимога 2.3)
    @Operation(summary = "Часткове оновлення (PATCH)", description = "Оновлює лише передані поля аніматора (наприклад, тільки досвід).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успішно оновлено"),
            @ApiResponse(responseCode = "404", description = "Аніматора не знайдено")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Animator> patchAnimator(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        for (Animator animator : apiDatabase) {
            if (animator.getId().equals(id)) {
                fields.forEach((key, value) -> {
                    Field field = ReflectionUtils.findField(Animator.class, key);
                    if (field != null) {
                        field.setAccessible(true);
                        ReflectionUtils.setField(field, animator, value);
                    }
                });
                return ResponseEntity.ok(animator);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 6. DELETE (Вимога 2.1)
    @Operation(summary = "Видалити аніматора", description = "Видаляє аніматора за вказаним ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успішно видалено (No Content)"),
            @ApiResponse(responseCode = "404", description = "Аніматора не знайдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimator(@PathVariable Long id) {
        boolean removed = apiDatabase.removeIf(a -> a.getId().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build(); // Статус 204 No Content
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
