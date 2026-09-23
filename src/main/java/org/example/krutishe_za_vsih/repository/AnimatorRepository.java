package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Animator;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AnimatorRepository {
    public List<Animator> findAll() {
        return List.of(
                new Animator("Ваня"),
                new Animator("Діана"),
                new Animator("Аріна")
        );
    }
}
