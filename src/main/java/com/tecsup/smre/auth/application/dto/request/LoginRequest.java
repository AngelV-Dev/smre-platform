package com.tecsup.smre.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato de correo es inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
