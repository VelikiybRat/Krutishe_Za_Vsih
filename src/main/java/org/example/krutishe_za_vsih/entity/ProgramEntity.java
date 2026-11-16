package org.example.krutishe_za_vsih.entity;

import jakarta.persistence.*;

/** Розважальна програма. Сторона "один" у зв'язку один-до-багатьох з OrderEntity. */
@Entity
@Table(name = "programs")
public class ProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    protected ProgramEntity() {
    }

    public ProgramEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
