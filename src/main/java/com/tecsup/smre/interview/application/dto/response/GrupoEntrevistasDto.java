package com.tecsup.smre.interview.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoEntrevistasDto {
    /** Nombre del grupo (nombre del tutor o nombre de la carrera). */
    private String nombre;
    /** Total de alumnos en este grupo. */
    private int totalAlumnos;
    /** Cuántos de esos alumnos tienen al menos una entrevista registrada. */
    private int evaluados;
    /** Lista de alumnos del grupo. */
    private List<AlumnoResumenDto> alumnos;
}
