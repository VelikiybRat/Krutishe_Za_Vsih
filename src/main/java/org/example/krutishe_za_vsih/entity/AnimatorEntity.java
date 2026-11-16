package org.example.krutishe_za_vsih.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** Аніматор. Сторона "один" у зв'язку один-до-багатьох з OrderEntity. */
@Entity
@Table(name = "animators")
public class AnimatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // GENERATED ... AS IDENTITY у PostgreSQL
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(nullable = false)
    private int experience;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "animator")
    private List<OrderEntity> orders = new ArrayList<>();

    /** Обов'язковий для JPA конструктор без параметрів. */
    protected AnimatorEntity() {
    }

    public AnimatorEntity(String name, String role, int experience, String notes) {
        this.name = name;
        this.role = role;
        this.experience = experience;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OrderEntity> getOrders() { return orders; }
}
