package com.tecsup.smre.dashboard.application.service;

import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import com.tecsup.smre.dashboard.application.dto.response.AlumnoRiesgoEstadistica;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasGeneralesResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;
import com.tecsup.smre.dashboard.domain.port.in.GetAlumnosAltoRiesgoUseCase;
import com.tecsup.smre.dashboard.domain.port.in.GetEstadisticasPorTutorUseCase;
import com.tecsup.smre.dashboard.domain.port.in.GetEstadisticasUseCase;
import com.tecsup.smre.dashboard.domain.port.out.EstadisticaRepositoryPort;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaEntity;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadisticasService implements GetEstadisticasUseCase, GetEstadisticasPorTutorUseCase, GetAlumnosAltoRiesgoUseCase {

    private final EstadisticaRepositoryPort estadisticaRepositoryPort;
    private final StudentRepositoryPort studentRepositoryPort;
    private final EntrevistaJpaRepository entrevistaJpaRepository;

    public EstadisticasService(EstadisticaRepositoryPort estadisticaRepositoryPort,
                               StudentRepositoryPort studentRepositoryPort,
                               EntrevistaJpaRepository entrevistaJpaRepository) {
        this.estadisticaRepositoryPort = estadisticaRepositoryPort;
        this.studentRepositoryPort = studentRepositoryPort;
        this.entrevistaJpaRepository = entrevistaJpaRepository;
    }

    @Override
    public EstadisticasGeneralesResponse obtenerEstadisticasGenerales() {
        return EstadisticasGeneralesResponse.builder()
                .totalTutoresActivos(estadisticaRepositoryPort.contarTutoresActivos())
                .totalAsignaciones(estadisticaRepositoryPort.contarAsignaciones())
                .totalEntrevistasProgramadas(estadisticaRepositoryPort.contarEntrevistas())
                .build();
    }

    @Override
    public List<EstadisticasPorTutorResponse> obtenerEntrevistasPorTutor() {
        return estadisticaRepositoryPort.agruparEntrevistasPorTutor();
    }

    @Override
    public List<AlumnoAltoRiesgoResponse> obtenerAlumnosAltoRiesgo() {
        return estadisticaRepositoryPort.buscarAlumnosAltoRiesgo();
    }

    public List<AlumnoRiesgoEstadistica> obtenerAlumnosRiesgo() {
        List<Alumno> alumnos = studentRepositoryPort.findAll();
        return alumnos.stream().map(alumno -> {
            List<EntrevistaEntity> entrevistas = entrevistaJpaRepository.findByAlumnoId(alumno.getId().toString());
            String nivelRiesgo = "BAJO"; // Default if no interview
            if (!entrevistas.isEmpty()) {
                EntrevistaEntity latest = entrevistas.get(entrevistas.size() - 1);
                nivelRiesgo = latest.getNivelRiesgo() != null ? latest.getNivelRiesgo().name() : "BAJO";
            }
            return AlumnoRiesgoEstadistica.builder()
                    .carrera(alumno.getCarrera())
                    .semestre(alumno.getSemestre())
                    .grupo(alumno.getGrupo())
                    .nivelRiesgo(nivelRiesgo)
                    .build();
        }).collect(Collectors.toList());
    }
}