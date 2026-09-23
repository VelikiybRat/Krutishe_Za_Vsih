package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Addon;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class AddonRepository {
    public List<Addon> findAll() {
        return List.of(
                new Addon("Шоу мильних бульбашок", 1500),
                new Addon("Срібне паперове шоу", 2000),
                new Addon("Аквагрим", 800)
        );
    }
}