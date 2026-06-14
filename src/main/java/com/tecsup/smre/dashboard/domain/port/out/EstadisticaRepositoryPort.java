package com.tecsup.smre.dashboard.domain.port.out;

import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;

import java.util.List;

public interface EstadisticaRepositoryPort {
    Long contarTutoresActivos();
    Long contarAsignaciones();
    Long contarEntrevistas();
    List<EstadisticasPorTutorResponse> agruparEntrevistasPorTutor();
    List<AlumnoAltoRiesgoResponse> buscarAlumnosAltoRiesgo();
}