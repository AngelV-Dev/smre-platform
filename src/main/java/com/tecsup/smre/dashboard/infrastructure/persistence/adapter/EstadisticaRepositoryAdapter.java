package com.tecsup.smre.dashboard.infrastructure.persistence.adapter;

import com.tecsup.smre.assignment.infrastructure.persistence.repository.AsignacionJpaRepository;
import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;
import com.tecsup.smre.dashboard.domain.port.out.EstadisticaRepositoryPort;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaEntity;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.user.infrastructure.adapter.out.persistence.JpaTutorRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EstadisticaRepositoryAdapter implements EstadisticaRepositoryPort {

    private final JpaTutorRepository tutorRepository;
    private final AsignacionJpaRepository asignacionRepository;
    private final EntrevistaJpaRepository entrevistaRepository;

    public EstadisticaRepositoryAdapter(JpaTutorRepository tutorRepository,
                                         AsignacionJpaRepository asignacionRepository,
                                         EntrevistaJpaRepository entrevistaRepository) {
        this.tutorRepository = tutorRepository;
        this.asignacionRepository = asignacionRepository;
        this.entrevistaRepository = entrevistaRepository;
    }

    @Override
    public Long contarTutoresActivos() {
        return tutorRepository.findAll().stream()
                .filter(t -> t.isActivo())
                .count();
    }

    @Override
    public Long contarAsignaciones() {
        return asignacionRepository.count();
    }

    @Override
    public Long contarEntrevistas() {
        return entrevistaRepository.count();
    }

    @Override
    public List<EstadisticasPorTutorResponse> agruparEntrevistasPorTutor() {
        Map<String, List<EntrevistaEntity>> porTutor = entrevistaRepository.findAll().stream()
                .filter(e -> e.getTutorId() != null)
                .collect(Collectors.groupingBy(EntrevistaEntity::getTutorId));

        return porTutor.entrySet().stream()
                .map(entry -> {
                    List<EntrevistaEntity> entrevistas = entry.getValue();
                    String tutorNombre = entrevistas.get(0).getTutorNombre();
                    Long tutorId;
                    try {
                        tutorId = Long.valueOf(entry.getKey());
                    } catch (NumberFormatException ex) {
                        tutorId = null;
                    }
                    return EstadisticasPorTutorResponse.builder()
                            .tutorId(tutorId)
                            .tutorNombre(tutorNombre)
                            .cantidadEntrevistas((long) entrevistas.size())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AlumnoAltoRiesgoResponse> buscarAlumnosAltoRiesgo() {
        // Toma la entrevista más reciente por alumno y filtra las que quedaron en ALTO
        Map<String, List<EntrevistaEntity>> porAlumno = entrevistaRepository.findAll().stream()
                .filter(e -> e.getAlumnoId() != null)
                .collect(Collectors.groupingBy(EntrevistaEntity::getAlumnoId));

        return porAlumno.values().stream()
                .map(entrevistas -> entrevistas.stream()
                        .max(Comparator.comparing(EntrevistaEntity::getFecha))
                        .orElse(null))
                .filter(e -> e != null && e.getNivelRiesgo() == NivelRiesgo.ALTO)
                .map(e -> AlumnoAltoRiesgoResponse.builder()
                        .alumnoId(Long.valueOf(e.getAlumnoId()))
                        .alumnoNombre(e.getAlumnoNombre() + " " + e.getAlumnoApellido())
                        .nivelRiesgo(e.getNivelRiesgo().name())
                        .tutorAsignado(e.getTutorNombre())
                        .build())
                .collect(Collectors.toList());
    }
}
