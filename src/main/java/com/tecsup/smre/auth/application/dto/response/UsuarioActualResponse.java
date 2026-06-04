package com.tecsup.smre.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioActualResponse {
    private Long id;
    private String email;
    private String nombre;
    private String role;
}