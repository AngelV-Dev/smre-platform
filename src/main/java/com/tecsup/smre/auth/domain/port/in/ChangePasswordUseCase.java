package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.application.dto.request.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    void cambiarPassword(String token, ChangePasswordRequest request);
}
