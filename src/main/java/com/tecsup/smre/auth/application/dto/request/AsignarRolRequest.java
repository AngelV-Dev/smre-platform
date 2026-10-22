package com.tecsup.smre.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarRolRequest {

    @NotBlank(message = "El correo del destinatario es obligatorio")
    @Email(message = "El formato del correo no es válido")
    private String emailDestino;

    @NotNull(message = "El rol es obligatorio")
    private String rol; // "ADMIN" o "TUTOR"
}
