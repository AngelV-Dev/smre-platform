package com.tecsup.smre.auth.domain.port.out;

public interface PasswordEncoderPort {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
