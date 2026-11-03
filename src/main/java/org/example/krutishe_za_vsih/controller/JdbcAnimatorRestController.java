
package org.example.krutishe_za_vsih.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.krutishe_za_vsih.model.Animator;
import org.example.krutishe_za_vsih.repository.AnimatorDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/jdbc/animators")
@Tag(name = "JDBC Animator API",
        description = "Робота з PostgreSQL через JdbcTemplate")
public class JdbcAnimatorRestController {

    private final AnimatorDao animatorDao;

    public JdbcAnimatorRestController(
            @Qualifier("jdbcAnimatorRepository")
            AnimatorDao animatorDao) {
        this.animatorDao = animatorDao;
    }

    @Operation(summary = "Отримати всіх аніматорів із PostgreSQL")
    @GetMapping
    public List<Animator> getAll() {
        return animatorDao.findAll();
    }

    @Operation(summary = "Знайти аніматора за ID")
    @GetMapping("/{id}")
    public ResponseEntity<Animator> getById(
            @PathVariable long id) {

        return ResponseEntity.of(animatorDao.findById(id));
    }

    @Operation(summary = "Фільтрація за роллю та досвідом")
    @GetMapping("/filter")
    public List<Animator> filter(
            @RequestParam String role,
            @RequestParam int minExperience) {

        return animatorDao.findByRoleAndMinExperience(
                role, minExperience);
    }

    @Operation(summary = "Створити нового аніматора в PostgreSQL")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Аніматора успішно створено"),
            @ApiResponse(responseCode = "400",
                    description = "Некоректні дані аніматора")
    })
    @PostMapping
    public ResponseEntity<Animator> create(
            @RequestBody CreateAnimatorRequest request) {

        if (request.name() == null || request.name().isBlank()
                || request.role() == null || request.role().isBlank()
                || request.experience() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некоректні дані аніматора"
            );
        }

        long id = animatorDao.create(
                request.name().trim(),
                request.role().trim(),
                request.experience(),
                request.notes()
        );

        Animator created = animatorDao.findById(id)
                .orElseThrow();

        return ResponseEntity
                .created(URI.create("/api/jdbc/animators/" + id))
                .body(created);
    }

    public record CreateAnimatorRequest(
            String name,
            String role,
            int experience,
            String notes
    ) {}

    @Operation(summary = "Оновити аніматора в PostgreSQL")
    @PutMapping("/{id}")
    public ResponseEntity<Animator> update(
            @PathVariable long id,
            @RequestBody CreateAnimatorRequest request) {

        if (request.name() == null || request.name().isBlank()
                || request.role() == null || request.role().isBlank()
                || request.experience() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некоректні дані аніматора"
            );
        }

        if (animatorDao.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        animatorDao.update(
                id,
                request.name().trim(),
                request.role().trim(),
                request.experience(),
                request.notes()
        );

        return ResponseEntity.of(animatorDao.findById(id));
    }


    @Operation(summary = "Видалити аніматора з PostgreSQL")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Аніматора успішно видалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Аніматора не знайдено"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id) {

        if (animatorDao.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        int deleted = animatorDao.deleteById(id);

        if (deleted == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}