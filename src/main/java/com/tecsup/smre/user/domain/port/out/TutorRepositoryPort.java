package com.tecsup.smre.user.domain.port.out;

import com.tecsup.smre.user.domain.model.Tutor;

import java.util.List;
import java.util.Optional;

public interface TutorRepositoryPort {
    Tutor save(Tutor tutor);
    List<Tutor> findAll();
    Optional<Tutor> findById(Long id);
    boolean existsByEmail(String email);
    void deleteById(Long id);
}