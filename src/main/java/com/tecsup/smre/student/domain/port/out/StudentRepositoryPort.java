package com.tecsup.smre.student.domain.port.out;

import com.tecsup.smre.student.domain.model.Alumno;
import java.util.List;
import java.util.Optional;

public interface StudentRepositoryPort {
    List<Alumno> findAll();
    Optional<Alumno> findByCodigo(String codigo);
    Alumno save(Alumno alumno);
    List<Alumno> saveAll(List<Alumno> alumnos);
    boolean existsByCodigo(String codigo);
    List<Alumno> findByCarreraAndSemestre(String carrera, String semestre);
    boolean existsByEmail(String email);
}
