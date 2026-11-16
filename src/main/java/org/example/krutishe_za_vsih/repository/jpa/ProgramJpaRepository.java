package org.example.krutishe_za_vsih.repository.jpa;

import org.example.krutishe_za_vsih.entity.ProgramEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProgramJpaRepository extends CrudRepository<ProgramEntity, Long> {

    List<ProgramEntity> findAllByOrderByIdAsc();

    List<ProgramEntity> findByNameContainingIgnoreCaseOrderByName(String namePart);
}