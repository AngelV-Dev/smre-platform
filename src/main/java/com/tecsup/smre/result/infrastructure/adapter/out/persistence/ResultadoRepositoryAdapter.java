package com.tecsup.smre.result.infrastructure.adapter.out.persistence;

import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaEntity;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.result.domain.port.out.ResultadoRepositoryPort;

import java.util.List;
import java.util.Optional;

public class ResultadoRepositoryAdapter implements ResultadoRepositoryPort {

    private final EntrevistaJpaRepository jpaEntrevistaRepository;

    public ResultadoRepositoryAdapter(EntrevistaJpaRepository jpaEntrevistaRepository) {
        this.jpaEntrevistaRepository = jpaEntrevistaRepository;
    }

    @Override
    public Optional<Entrevista> findById(Long entrevistaId) {
        return jpaEntrevistaRepository.findById(entrevistaId)
                .map(this::toDomain);
    }

    @Override
    public List<Entrevista> findHistorialByAlumnoId(Long alumnoId) {
        return jpaEntrevistaRepository.findByAlumnoIdOrderByFechaDesc(alumnoId.toString())
                .stream()
                .map(this::toDomain)
                .toList();
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
                .build();
    }
}
