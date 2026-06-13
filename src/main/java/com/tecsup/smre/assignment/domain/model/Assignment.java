package com.tecsup.smre.assignment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private Long id;
    private Long tutorId;
    private String tutorNombre; // Nombre completo del tutor asignado
    private String periodo; // Ej: "2026-1"
    private String especialidad; // Ej: "Diseño y Desarrollo de Software"
    private String ciclo; // Ej: "I", "II", "III", "IV", "V", "VI"
    private String secciones; // Ej: "A, B"
}
