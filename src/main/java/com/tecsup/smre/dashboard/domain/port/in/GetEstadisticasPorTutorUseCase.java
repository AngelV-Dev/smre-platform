package com.tecsup.smre.dashboard.domain.port.in;

import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;
import java.util.List;

public interface GetEstadisticasPorTutorUseCase {
    List<EstadisticasPorTutorResponse> obtenerEntrevistasPorTutor();
}