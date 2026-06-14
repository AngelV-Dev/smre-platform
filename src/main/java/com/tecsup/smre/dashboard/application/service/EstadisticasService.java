package com.tecsup.smre.dashboard.application.service;

import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasGeneralesResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;
import com.tecsup.smre.dashboard.domain.port.in.GetAlumnosAltoRiesgoUseCase;
import com.tecsup.smre.dashboard.domain.port.in.GetEstadisticasPorTutorUseCase;
import com.tecsup.smre.dashboard.domain.port.in.GetEstadisticasUseCase;
import com.tecsup.smre.dashboard.domain.port.out.EstadisticaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadisticasService implements GetEstadisticasUseCase, GetEstadisticasPorTutorUseCase, GetAlumnosAltoRiesgoUseCase {

    private final EstadisticaRepositoryPort estadisticaRepositoryPort;

    public EstadisticasService(EstadisticaRepositoryPort estadisticaRepositoryPort) {
        this.estadisticaRepositoryPort = estadisticaRepositoryPort;
    }

    @Override
    public EstadisticasGeneralesResponse obtenerEstadisticasGenerales() {
        // El service orquesta las consultas y construye el DTO de respuesta
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
}