package com.tecsup.smre.interview.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ⚠️ STUB TEMPORAL — BORRAR cuando Angelo Ricasca suba la versión real a develop (orden 4°).
// Campos basados en la Tabla de Requerimientos para que mi módulo result/ funcione mientras tanto.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entrevista {
    private Long id;
    private Long alumnoId;
    private String alumnoNombre;
    private String alumnoApellido;
    private Long tutorId;
    private String tutorNombre;
    private Integer puntajeTotal;
    private NivelRiesgo nivelRiesgo;
    private String recomendacion;
    private String observaciones;
    private LocalDateTime fecha;
}
