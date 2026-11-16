package org.example.krutishe_za_vsih.service.jpa;

import org.example.krutishe_za_vsih.dto.ProgramDto;
import org.example.krutishe_za_vsih.repository.jpa.ProgramJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class JpaProgramService {

    private final ProgramJpaRepository programRepository;

    public JpaProgramService(ProgramJpaRepository programRepository) {
        this.programRepository = programRepository;
    }

    public List<ProgramDto> findAll(String namePart) {
        var programs = (namePart == null || namePart.isBlank())
                ? programRepository.findAllByOrderByIdAsc()
                : programRepository.findByNameContainingIgnoreCaseOrderByName(namePart);
        return programs.stream().map(ProgramDto::from).toList();
    }
}