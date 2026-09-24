package org.example.krutishe_za_vsih.model;

public class Program {
    private String name;
    private String description;
    private String category; // 1. Додали саме поле категорії

    // 2. Оновили конструктор, щоб він одразу приймав категорію
    public Program(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    // Геттери та сеттери (всі всередині класу!)
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}