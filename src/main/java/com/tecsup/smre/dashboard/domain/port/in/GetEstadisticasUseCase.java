package com.tecsup.smre.dashboard.domain.port.in;

import com.tecsup.smre.dashboard.application.dto.response.EstadisticasGeneralesResponse;

public interface GetEstadisticasUseCase {
    EstadisticasGeneralesResponse obtenerEstadisticasGenerales();
}