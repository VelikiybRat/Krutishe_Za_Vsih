package org.example.krutishe_za_vsih.service;

import org.example.krutishe_za_vsih.model.Animator;
import org.example.krutishe_za_vsih.model.Program;
import org.example.krutishe_za_vsih.repository.AnimatorRepository;
import org.example.krutishe_za_vsih.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrmService {

    private final ProgramRepository programRepository;
    private final AnimatorRepository animatorRepository;

    public CrmService(ProgramRepository programRepository, AnimatorRepository animatorRepository) {
        this.programRepository = programRepository;
        this.animatorRepository = animatorRepository;
    }

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public List<Animator> getAllAnimators() {
        return animatorRepository.findAll();
    }
}