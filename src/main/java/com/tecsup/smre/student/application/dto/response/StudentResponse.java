package com.tecsup.smre.student.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String email;
    private String carrera;
    private String semestre;
    private String grupo;
    private Integer edad;
}