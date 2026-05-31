package com.tecsup.smre.student.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email es inválido")
    private String email;

    @NotBlank(message = "La carrera es obligatoria")
    private String carrera;

    @NotBlank(message = "El semestre es obligatorio")
    private String semestre;

    @NotBlank(message = "El grupo es obligatorio")
    private String grupo;
}