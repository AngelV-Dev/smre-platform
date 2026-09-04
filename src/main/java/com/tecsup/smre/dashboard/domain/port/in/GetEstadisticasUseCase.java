package com.tecsup.smre.dashboard.domain.port.in;

import com.tecsup.smre.dashboard.application.dto.response.EstadisticasGeneralesResponse;
import com.tecsup.smre.dashboard.application.dto.response.AlumnoRiesgoEstadistica;
import java.util.List;

public interface GetEstadisticasUseCase {
    EstadisticasGeneralesResponse obtenerEstadisticasGenerales(String carrera, String ciclo);
    List<AlumnoRiesgoEstadistica> obtenerAlumnosRiesgo();
}