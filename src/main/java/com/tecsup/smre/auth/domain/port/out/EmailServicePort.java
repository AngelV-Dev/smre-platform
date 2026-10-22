package com.tecsup.smre.auth.domain.port.out;

public interface EmailServicePort {
    void sendPasswordResetEmail(String toEmail, String nombre, String resetLink);
}
