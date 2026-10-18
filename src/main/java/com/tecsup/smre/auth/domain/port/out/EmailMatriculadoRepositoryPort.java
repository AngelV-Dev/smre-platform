package com.tecsup.smre.auth.domain.port.out;

import com.tecsup.smre.auth.domain.model.EmailMatriculado;

import java.util.List;
import java.util.Optional;

public interface EmailMatriculadoRepositoryPort {
    List<EmailMatriculado> findAll();
    Optional<EmailMatriculado> findByEmail(String email);
    boolean existsByEmail(String email);
    EmailMatriculado save(EmailMatriculado emailMatriculado);
    void deleteById(Long id);
}
