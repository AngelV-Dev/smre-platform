package com.tecsup.smre.result.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoResponseDto {
    private Long entrevistaId;
    private Long alumnoId;
    private String alumnoNombre;
    private String alumnoApellido;
    private String tutorNombre;
    private Integer puntajeTotal;
    private String nivelRiesgo;     // ALTO / MEDIO / BAJO -> lo pinta Semaforo.jsx
    private String recomendacion;
    private String observaciones;
    private LocalDateTime fecha;
}
