package com.tecsup.smre.config;

import com.tecsup.smre.auth.domain.port.out.EmailServicePort;
import com.tecsup.smre.auth.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.auth.domain.port.out.TokenServicePort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.auth.domain.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public AuthService authService(UsuarioRepositoryPort usuarioRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort,
                                   TokenServicePort tokenServicePort,
                                   EmailServicePort emailServicePort,
                                   @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        return new AuthService(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort, emailServicePort, frontendUrl);
    }
}
