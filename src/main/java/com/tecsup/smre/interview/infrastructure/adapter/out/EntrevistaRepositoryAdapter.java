package com.tecsup.smre.interview.infrastructure.adapter.out;

import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioMapper;
import com.tecsup.smre.interview.application.port.out.EntrevistaRepositoryPort;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaEntity;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.student.infrastructure.adapter.out.persistence.AlumnoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EntrevistaRepositoryAdapter implements EntrevistaRepositoryPort {

    private final EntrevistaJpaRepository jpaRepository;

    public EntrevistaRepositoryAdapter(EntrevistaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private EntrevistaEntity toEntity(Entrevista domain) {
        EntrevistaEntity entity = new EntrevistaEntity();
        entity.setId(domain.getId());
        entity.setAlumno(AlumnoMapper.toEntity(domain.getAlumno()));
        entity.setTutor(UsuarioMapper.toEntity(domain.getTutor()));
        entity.setPuntajeTotal(domain.getPuntajeTotal());
        entity.setNivelRiesgo(domain.getNivelRiesgo());
        entity.setObservaciones(domain.getObservaciones());
        entity.setFecha(domain.getFecha());
        return entity;
    }

    private Entrevista toDomain(EntrevistaEntity entity) {
        return new Entrevista(
                entity.getId(),
                AlumnoMapper.toDomain(entity.getAlumno()),
                UsuarioMapper.toDomain(entity.getTutor()),
                entity.getPuntajeTotal(),
                entity.getNivelRiesgo(),
                entity.getObservaciones(),
                entity.getFecha()
        );
    }

    @Override
    public Entrevista save(Entrevista entrevista) {
        EntrevistaEntity entity = toEntity(entrevista);
        EntrevistaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Entrevista> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Entrevista> findByAlumnoId(Long alumnoId) {
        return jpaRepository.findByAlumnoId(alumnoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
