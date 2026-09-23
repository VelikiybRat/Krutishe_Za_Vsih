package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Program;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProgramRepository {
    public List<Program> findAll() {
        return List.of(
                new Program("Roblox Квест", "Інтерактивні ігри з тематичним реквізитом"),
                new Program("Стітч-паті", "Анімація в ростовому костюмі Стітча")
        );
    }
}
