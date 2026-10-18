package com.tecsup.smre.auth.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaEmailMatriculadoRepository extends JpaRepository<EmailMatriculadoEntity, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<EmailMatriculadoEntity> findByEmailIgnoreCase(String email);
}
