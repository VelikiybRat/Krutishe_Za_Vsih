
package org.example.krutishe_za_vsih.repository;

import org.example.krutishe_za_vsih.model.Animator;

import java.util.List;
import java.util.Optional;

public interface AnimatorDao {

    List<Animator> findAll();

    Optional<Animator> findById(long id);

    long create(String name, String role,
                int experience, String notes);

    int update(long id, String name, String role,
               int experience, String notes);

    int deleteById(long id);

    List<Animator> findByRoleAndMinExperience(
            String role, int minExperience);
}
