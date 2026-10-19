package com.tecsup.smre.config;

import com.tecsup.smre.auth.domain.port.in.ChangePasswordUseCase;
import com.tecsup.smre.auth.domain.port.in.ForgotPasswordUseCase;
import com.tecsup.smre.auth.domain.port.in.GetUsuarioActualUseCase;
import com.tecsup.smre.auth.domain.port.in.LoginUseCase;
import com.tecsup.smre.auth.domain.port.in.LogoutUseCase;
import com.tecsup.smre.auth.domain.port.in.ResetPasswordUseCase;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
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
                                   EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort,
                                   EmailServicePort emailServicePort,
                                   @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        return new AuthService(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort,
                               emailMatriculadoRepositoryPort, emailServicePort, frontendUrl);
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

    @Bean
    public ForgotPasswordUseCase forgotPasswordUseCase(AuthService authService) {
        return authService;
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(AuthService authService) {
        return authService;
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(AuthService authService) {
        return authService;
    }
}
