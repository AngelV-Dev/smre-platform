package com.tecsup.smre.student.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaAlumnoRepository extends JpaRepository<AlumnoEntity, Long> {
    Optional<AlumnoEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
