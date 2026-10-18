package com.tecsup.smre.config;

import com.tecsup.smre.auth.domain.port.in.GetUsuarioActualUseCase;
import com.tecsup.smre.auth.domain.port.in.LoginUseCase;
import com.tecsup.smre.auth.domain.port.in.LogoutUseCase;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.auth.domain.port.out.TokenServicePort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.auth.domain.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public AuthService authService(UsuarioRepositoryPort usuarioRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort,
                                   TokenServicePort tokenServicePort,
                                   EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort) {
        return new AuthService(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort, emailMatriculadoRepositoryPort);
    }

    @Bean
    public LoginUseCase loginUseCase(AuthService authService) {
        return authService;
    }

    @Bean
    public LogoutUseCase logoutUseCase(AuthService authService) {
        return authService;
    }

    @Bean
    public GetUsuarioActualUseCase getUsuarioActualUseCase(AuthService authService) {
        return authService;
    }
}
