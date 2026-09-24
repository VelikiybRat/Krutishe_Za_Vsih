package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Animator;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AnimatorRepository {

    public List<Animator> findAll() {
        return List.of(
                // Передаємо всі 4 параметри: ім'я, роль, досвід, примітки
                new Animator("Ваня", "Stitch", 3, "Рве зали, топовий реквізит"),
                new Animator("Діана", "Spider-Man", 1, null),
                new Animator("Аріна", "Універсальний", 2, "Відмінно веде квести")
        );
    }
}