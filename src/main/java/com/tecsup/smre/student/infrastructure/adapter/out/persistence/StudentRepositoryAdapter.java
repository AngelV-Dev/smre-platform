package com.tecsup.smre.student.infrastructure.adapter.out.persistence;

import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import java.util.List;
import java.util.Optional;

// No uses @Repository aquí — el bean se registra desde StudentConfig
public class StudentRepositoryAdapter implements StudentRepositoryPort {

    private final JpaAlumnoRepository jpaAlumnoRepository;

    public StudentRepositoryAdapter(JpaAlumnoRepository jpaAlumnoRepository) {
        this.jpaAlumnoRepository = jpaAlumnoRepository;
    }

    @Override
    public List<Alumno> findAll() {
        return jpaAlumnoRepository.findAll()
                .stream()
                .map(AlumnoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Alumno> findByCodigo(String codigo) {
        return jpaAlumnoRepository.findByCodigo(codigo)
                .map(AlumnoMapper::toDomain);
    }

    @Override
    public Alumno save(Alumno alumno) {
        AlumnoEntity entity = AlumnoMapper.toEntity(alumno);
        return AlumnoMapper.toDomain(jpaAlumnoRepository.save(entity));
    }

    @Override
    public List<Alumno> saveAll(List<Alumno> alumnos) {
        List<AlumnoEntity> entities = alumnos.stream()
                .map(AlumnoMapper::toEntity)
                .toList();
        return jpaAlumnoRepository.saveAll(entities)
                .stream()
                .map(AlumnoMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return jpaAlumnoRepository.existsByCodigo(codigo);
    }

    @Override
    public List<Alumno> findByCarreraAndSemestre(String carrera, String semestre) {
        String c = (carrera == null || carrera.trim().isEmpty()) ? null : carrera.trim();
        String s = (semestre == null || semestre.trim().isEmpty()) ? null : semestre.trim();
        return jpaAlumnoRepository.findByCarreraAndSemestre(c, s)
                .stream()
                .map(AlumnoMapper::toDomain)
                .toList();
    }
}