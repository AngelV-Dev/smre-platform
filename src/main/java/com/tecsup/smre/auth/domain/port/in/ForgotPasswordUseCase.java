package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.application.dto.request.ForgotPasswordRequest;

public interface ForgotPasswordUseCase {
    void solicitarRecuperacion(ForgotPasswordRequest request);
}
