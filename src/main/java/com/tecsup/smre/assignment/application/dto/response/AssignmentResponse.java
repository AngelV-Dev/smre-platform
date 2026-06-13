package com.tecsup.smre.assignment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private Long tutorId;
    private String tutorNombre;
    private String periodo;
    private String especialidad;
    private String ciclo;
    private String secciones;
}
