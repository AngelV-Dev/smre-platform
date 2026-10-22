package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.application.dto.request.ResetPasswordRequest;

public interface ResetPasswordUseCase {
    void restablecerPassword(ResetPasswordRequest request);
}
