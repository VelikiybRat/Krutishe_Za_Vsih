package org.example.krutishe_za_vsih.model;

public class Animator {
    private String name;
    private String role;
    private int experience; // Ось наше поле досвіду
    private String notes;

    // Оновлений конструктор
    public Animator(String name, String role, int experience, String notes) {
        this.name = name;
        this.role = role;
        this.experience = experience;
        this.notes = notes;
    }

    // Геттери та сеттери
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    // Той самий геттер, який шукає Thymeleaf!
    public int getExperience() {
        return experience;
    }
    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}