package com.tecsup.smre.interview.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// ⚠️ STUB TEMPORAL — BORRAR cuando Angelo Ricasca suba la versión real a develop (orden 4°).
public interface JpaEntrevistaRepository extends JpaRepository<EntrevistaEntity, Long> {
    Optional<EntrevistaEntity> findById(Long id);
    List<EntrevistaEntity> findByAlumnoIdOrderByFechaDesc(Long alumnoId);
}
