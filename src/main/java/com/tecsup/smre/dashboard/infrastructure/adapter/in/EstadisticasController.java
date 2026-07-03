package com.tecsup.smre.dashboard.infrastructure.adapter.in;

import com.tecsup.smre.dashboard.application.dto.response.AlumnoAltoRiesgoResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasGeneralesResponse;
import com.tecsup.smre.dashboard.application.dto.response.EstadisticasPorTutorResponse;
import com.tecsup.smre.dashboard.domain.port.in.GetAlumnosAltoRiesgoUseCase;
import com.tecsup.smre.dashboard.domain.port.in.GetEstadisticasPorTutorUseCase;
import com.tecsup.smre.dashboard.domain.port.in.GetEstadisticasUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/estadisticas")
public class EstadisticasController {

    private final GetEstadisticasUseCase getEstadisticasUseCase;
    private final GetEstadisticasPorTutorUseCase getEstadisticasPorTutorUseCase;
    private final GetAlumnosAltoRiesgoUseCase getAlumnosAltoRiesgoUseCase;

    public EstadisticasController(GetEstadisticasUseCase getEstadisticasUseCase,
                                  GetEstadisticasPorTutorUseCase getEstadisticasPorTutorUseCase,
                                  GetAlumnosAltoRiesgoUseCase getAlumnosAltoRiesgoUseCase) {
        this.getEstadisticasUseCase = getEstadisticasUseCase;
        this.getEstadisticasPorTutorUseCase = getEstadisticasPorTutorUseCase;
        this.getAlumnosAltoRiesgoUseCase = getAlumnosAltoRiesgoUseCase;
    }

    @GetMapping
    public ResponseEntity<EstadisticasGeneralesResponse> generales() {
        return ResponseEntity.ok(getEstadisticasUseCase.obtenerEstadisticasGenerales());
    }

    @GetMapping("/por-tutor")
    public ResponseEntity<List<EstadisticasPorTutorResponse>> porTutor() {
        return ResponseEntity.ok(getEstadisticasPorTutorUseCase.obtenerEntrevistasPorTutor());
    }

    @GetMapping("/alto-riesgo")
    public ResponseEntity<List<AlumnoAltoRiesgoResponse>> altoRiesgo() {
        return ResponseEntity.ok(getAlumnosAltoRiesgoUseCase.obtenerAlumnosAltoRiesgo());
    }

    @GetMapping("/alumnos-riesgo")
    public ResponseEntity<List<com.tecsup.smre.dashboard.application.dto.response.AlumnoRiesgoEstadistica>> alumnosRiesgo() {
        return ResponseEntity.ok(getEstadisticasUseCase.obtenerAlumnosRiesgo());
    }
}