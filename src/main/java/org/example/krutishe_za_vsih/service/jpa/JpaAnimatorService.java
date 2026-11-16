package org.example.krutishe_za_vsih.service.jpa;

import org.example.krutishe_za_vsih.dto.AnimatorDto;
import org.example.krutishe_za_vsih.dto.AnimatorNameOnly;
import org.example.krutishe_za_vsih.dto.AnimatorOrderCount;
import org.example.krutishe_za_vsih.dto.AnimatorRequest;

import java.util.List;

public interface JpaAnimatorService {

    List<AnimatorDto> findAll();

    AnimatorDto findById(long id);

    List<AnimatorDto> filter(String role, int minExperience);

    List<AnimatorDto> top3ByExperience();

    List<AnimatorDto> searchByName(String namePart);

    List<AnimatorOrderCount> orderCounts();

    List<AnimatorNameOnly> namesByRole(String role);

    AnimatorDto create(AnimatorRequest request);

    AnimatorDto update(long id, AnimatorRequest request);

    void delete(long id);
}