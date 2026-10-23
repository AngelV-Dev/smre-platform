package com.tecsup.smre.interview.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialGrupoDto {
    private Long asignacionId;
    private String periodo;
    private String ciclo;
    private String especialidad;
    private String grupo;
    private String secciones;
    
    // Conteo de alumnos que actualmente coinciden con esta asignación (semestre, carrera, grupo)
    private int totalAlumnos;
    
    // Conteo de alumnos del grupo que ya cuentan con al menos una entrevista registrada
    private int entrevistasCompletadas;
    
    // Nota indicando que los conteos corresponden a los alumnos que coinciden en el estado actual de la BD
    private String nota;
}
