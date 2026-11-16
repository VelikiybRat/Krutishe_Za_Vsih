package org.example.krutishe_za_vsih.exception;

/**
 * Бізнес-конфлікт (аніматор уже зайнятий на дату). Це RuntimeException, тому
 * @Transactional-метод, який її кидає, автоматично відкочує всю транзакцію.
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}