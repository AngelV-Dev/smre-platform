package com.tecsup.smre.interview.infrastructure.adapter.out;

import com.tecsup.smre.interview.application.port.out.EntrevistaRepositoryPort;
import com.tecsup.smre.interview.domain.model.Entrevista;
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
        entity.setAlumnoId(domain.getAlumnoId());
        entity.setAlumnoNombre(domain.getAlumnoNombre());
        entity.setAlumnoApellido(domain.getAlumnoApellido());
        entity.setTutorId(domain.getTutorId());
        entity.setTutorNombre(domain.getTutorNombre());
        entity.setPuntajeTotal(domain.getPuntajeTotal());
        entity.setNivelRiesgo(domain.getNivelRiesgo());
        entity.setRecomendacion(domain.getRecomendacion());
        entity.setObservaciones(domain.getObservaciones());
        entity.setFecha(domain.getFecha());
        entity.setEdad(domain.getEdad());
        entity.setRespuestas(domain.getRespuestas());
        return entity;
    }

    private Entrevista toDomain(EntrevistaEntity entity) {
        return Entrevista.builder()
                .id(entity.getId())
                .alumnoId(entity.getAlumnoId())
                .alumnoNombre(entity.getAlumnoNombre())
                .alumnoApellido(entity.getAlumnoApellido())
                .tutorId(entity.getTutorId())
                .tutorNombre(entity.getTutorNombre())
                .puntajeTotal(entity.getPuntajeTotal())
                .nivelRiesgo(entity.getNivelRiesgo())
                .recomendacion(entity.getRecomendacion())
                .observaciones(entity.getObservaciones())
                .fecha(entity.getFecha())
                .edad(entity.getEdad())
                .respuestas(entity.getRespuestas())
                .build();
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
    public List<Entrevista> findByAlumnoId(String alumnoId) {
        return jpaRepository.findByAlumnoId(alumnoId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
