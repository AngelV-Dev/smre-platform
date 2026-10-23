package com.tecsup.smre.interview.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoResumenDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String carrera;
    private String semestre;
    private String grupo;
    private Long tutorId;
    private String tutorNombre;
    private String ultimoNivelRiesgo;
    private Integer totalEntrevistas;
    private LocalDateTime fechaUltimaEntrevista;
}
