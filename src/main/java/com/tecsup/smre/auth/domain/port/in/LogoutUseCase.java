package com.tecsup.smre.auth.domain.port.in;

public interface LogoutUseCase {
    void logout(String token);
}