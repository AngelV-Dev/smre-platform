package com.tecsup.smre.assignment.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {

    @NotNull(message = "El ID del tutor es obligatorio")
    private Long tutorId;

    @NotBlank(message = "El período es obligatorio")
    private String periodo; // Ej: "2026-1"

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @NotBlank(message = "El ciclo es obligatorio")
    private String ciclo;

    @NotBlank(message = "Las secciones son obligatorias")
    private String secciones;
}
