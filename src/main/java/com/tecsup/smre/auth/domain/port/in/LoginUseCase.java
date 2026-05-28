package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.application.dto.request.LoginRequest;
import com.tecsup.smre.auth.application.dto.response.LoginResponse;

public interface LoginUseCase {
    LoginResponse login(LoginRequest request);
}
