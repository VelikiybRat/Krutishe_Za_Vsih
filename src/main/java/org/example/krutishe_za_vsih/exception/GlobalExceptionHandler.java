package org.example.krutishe_za_vsih.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Єдине місце, де винятки сервісів перетворюються на HTTP-статуси. */
@RestControllerAdvice(basePackages = "org.example.krutishe_za_vsih.controller.jpa")
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingConflictException.class)
    public ProblemDetail handleConflict(BookingConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegrity(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Операція порушує цілісність даних (наприклад, є пов'язані замовлення або дата вже зайнята)");
    }
}