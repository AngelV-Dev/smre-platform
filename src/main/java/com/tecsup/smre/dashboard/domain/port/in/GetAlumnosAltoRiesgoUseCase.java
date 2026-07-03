package com.tecsup.smre.dashboard.domain.port.in;

import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import java.util.List;

public interface GetAlumnosAltoRiesgoUseCase {
    List<AlumnoAltoRiesgoResponse> obtenerAlumnosAltoRiesgo();
}