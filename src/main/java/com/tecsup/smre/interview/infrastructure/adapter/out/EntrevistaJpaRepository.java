package com.tecsup.smre.interview.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrevistaJpaRepository extends JpaRepository<EntrevistaEntity, Long> {
    List<EntrevistaEntity> findByAlumnoId(String alumnoId);
    List<EntrevistaEntity> findByAlumnoIdOrderByFechaDesc(String alumnoId);
}
