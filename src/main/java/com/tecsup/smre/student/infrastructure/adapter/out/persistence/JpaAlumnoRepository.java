package com.tecsup.smre.student.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface JpaAlumnoRepository extends JpaRepository<AlumnoEntity, Long> {
    Optional<AlumnoEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    boolean existsByEmail(String email);

    @Query("SELECT a FROM AlumnoEntity a WHERE " +
           "(:carrera IS NULL OR :carrera = '' OR a.carrera = :carrera) AND " +
           "(:semestre IS NULL OR :semestre = '' OR a.semestre = :semestre)")
    List<AlumnoEntity> findByCarreraAndSemestre(
            @Param("carrera") String carrera,
            @Param("semestre") String semestre);
}