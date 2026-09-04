package com.tecsup.smre.result.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> respuestas; // Severidad de cada una de las 6 preguntas, en orden
}
