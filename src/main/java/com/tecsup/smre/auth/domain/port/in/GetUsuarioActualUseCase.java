package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.application.dto.response.UsuarioActualResponse;

public interface GetUsuarioActualUseCase {
    UsuarioActualResponse getUsuario(String token);
}