package org.example.krutishe_za_vsih.service;

import org.example.krutishe_za_vsih.model.Addon;
import org.example.krutishe_za_vsih.model.Animator;
import org.example.krutishe_za_vsih.model.Program;
import org.example.krutishe_za_vsih.repository.AddonRepository;
import org.example.krutishe_za_vsih.repository.AnimatorRepository;
import org.example.krutishe_za_vsih.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CrmService {
    private final ProgramRepository programRepository;
    private final AddonRepository addonRepository;
    private AnimatorRepository animatorRepository;

    @Autowired
    public CrmService(ProgramRepository programRepository, AddonRepository addonRepository) {
        this.programRepository = programRepository;
        this.addonRepository = addonRepository;
    }

    @Autowired
    public void setAnimatorRepository(AnimatorRepository animatorRepository) {
        this.animatorRepository = animatorRepository;
    }

    public List<Program> getAvailablePrograms() { return programRepository.findAll(); }
    public List<Addon> getAvailableAddons() { return addonRepository.findAll(); }
    public List<Animator> getTeam() { return animatorRepository.findAll(); }
}