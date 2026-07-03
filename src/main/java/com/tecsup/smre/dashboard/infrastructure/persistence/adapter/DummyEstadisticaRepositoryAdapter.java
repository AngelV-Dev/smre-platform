package com.tecsup.smre.dashboard.infrastructure.persistence.adapter;

import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;
import com.tecsup.smre.dashboard.domain.port.out.EstadisticaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component // Esto evita que Spring Boot crashee al faltar dependencias de otros módulos
public class DummyEstadisticaRepositoryAdapter implements EstadisticaRepositoryPort {

    @Override
    public Long contarTutoresActivos() { return 0L; }

    @Override
    public Long contarAsignaciones() { return 0L; }

    @Override
    public Long contarEntrevistas() { return 0L; }

    @Override
    public List<EstadisticasPorTutorResponse> agruparEntrevistasPorTutor() {
        return Collections.emptyList();
    }

    @Override
    public List<AlumnoAltoRiesgoResponse> buscarAlumnosAltoRiesgo() {
        return Collections.emptyList();
    }
}