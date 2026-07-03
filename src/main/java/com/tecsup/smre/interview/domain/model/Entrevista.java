package com.tecsup.smre.interview.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entrevista {
    private Long id;
    private String alumnoId;
    private String alumnoNombre;
    private String alumnoApellido;
    private String tutorId;
    private String tutorNombre;
    private int puntajeTotal;
    private NivelRiesgo nivelRiesgo;
    private String recomendacion;
    private String observaciones;
    private LocalDateTime fecha;
}
