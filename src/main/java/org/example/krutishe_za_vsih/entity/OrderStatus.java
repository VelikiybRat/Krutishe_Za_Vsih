package org.example.krutishe_za_vsih.entity;

/** Допустимі статуси замовлення (збігаються з CHECK-обмеженням у таблиці orders). */
public enum OrderStatus {
    NEW,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    /** Активне замовлення: аніматор на цю дату зайнятий. */
    public boolean isActive() {
        return this == NEW || this == CONFIRMED;
    }
}
