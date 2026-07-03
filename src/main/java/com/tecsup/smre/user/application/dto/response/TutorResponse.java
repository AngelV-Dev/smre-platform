package com.tecsup.smre.user.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;
    private boolean activo;
}