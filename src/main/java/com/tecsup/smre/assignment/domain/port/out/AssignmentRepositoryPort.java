package com.tecsup.smre.assignment.domain.port.out;

import com.tecsup.smre.assignment.domain.model.Assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepositoryPort {
    Assignment save(Assignment assignment);
    List<Assignment> findByPeriodo(String periodo);
    List<Assignment> findAll();
    Optional<Assignment> findById(Long id);
    void deleteById(Long id);
}
