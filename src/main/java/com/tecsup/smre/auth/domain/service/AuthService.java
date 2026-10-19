package com.tecsup.smre.auth.domain.service;

import com.tecsup.smre.auth.application.dto.request.ChangePasswordRequest;
import com.tecsup.smre.auth.application.dto.request.ForgotPasswordRequest;
import com.tecsup.smre.auth.application.dto.request.LoginRequest;
import com.tecsup.smre.auth.application.dto.request.ResetPasswordRequest;
import com.tecsup.smre.auth.application.dto.response.LoginResponse;
import com.tecsup.smre.auth.application.dto.response.UsuarioActualResponse;
import com.tecsup.smre.auth.domain.model.Usuario;
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
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.UnauthorizedException;

public class AuthService implements LoginUseCase, LogoutUseCase, GetUsuarioActualUseCase,
        ForgotPasswordUseCase, ResetPasswordUseCase, ChangePasswordUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenServicePort tokenServicePort;
    private final EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort;
    private final EmailServicePort emailServicePort;
    private final String frontendUrl;

    public AuthService(UsuarioRepositoryPort usuarioRepositoryPort,
                       PasswordEncoderPort passwordEncoderPort,
                       TokenServicePort tokenServicePort) {
        this(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort, null, (to, name, link) -> {}, "http://localhost:5173");
    }

    public AuthService(UsuarioRepositoryPort usuarioRepositoryPort,
                       PasswordEncoderPort passwordEncoderPort,
                       TokenServicePort tokenServicePort,
                       EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort) {
        this(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort, emailMatriculadoRepositoryPort, (to, name, link) -> {}, "http://localhost:5173");
    }

    public AuthService(UsuarioRepositoryPort usuarioRepositoryPort,
                       PasswordEncoderPort passwordEncoderPort,
                       TokenServicePort tokenServicePort,
                       EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort,
                       EmailServicePort emailServicePort,
                       String frontendUrl) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenServicePort = tokenServicePort;
        this.emailMatriculadoRepositoryPort = emailMatriculadoRepositoryPort;
        this.emailServicePort = emailServicePort != null ? emailServicePort : (to, name, link) -> {};
        this.frontendUrl = (frontendUrl != null && !frontendUrl.isBlank()) ? frontendUrl : "http://localhost:5173";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";

        // 1. Dominio Tecsup obligatorio
        if (!email.toLowerCase().endsWith("@tecsup.edu.pe")) {
            throw new BadRequestException("Solo se permiten correos de Tecsup (@tecsup.edu.pe)");
        }

        // 2. Correo debe estar matriculado en el sistema
        if (emailMatriculadoRepositoryPort != null && !emailMatriculadoRepositoryPort.existsByEmail(email)) {
            throw new UnauthorizedException("El correo no se encuentra matriculado en el sistema. Contacte al administrador.");
        }

        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!passwordEncoderPort.matches(request.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        if (!usuario.isActivo()) {
            throw new UnauthorizedException("Usuario desactivado. Contacte al administrador.");
        }

        String token = tokenServicePort.generateToken(usuario);

        return LoginResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .role(usuario.getRol().name())
                .build();
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token inválido");
        }
    }

    @Override
    public UsuarioActualResponse getUsuario(String token) {
        String email = tokenServicePort.extractEmail(token);

        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        return UsuarioActualResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .role(usuario.getRol().name())
                .build();
    }

    @Override
    public void solicitarRecuperacion(ForgotPasswordRequest request) {
        if (request.getEmail() == null || !request.getEmail().endsWith("@tecsup.edu.pe")) {
            throw new BadRequestException("Solo se permiten correos de Tecsup (@tecsup.edu.pe)");
        }

        Usuario usuario = usuarioRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No se encontró ningún usuario con el correo ingresado."));

        if (!usuario.isActivo()) {
            throw new BadRequestException("El usuario se encuentra desactivado. Contacte al administrador.");
        }

        String resetToken = tokenServicePort.generateResetToken(usuario);
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        emailServicePort.sendPasswordResetEmail(usuario.getEmail(), usuario.getNombre(), resetLink);
    }

    @Override
    public void restablecerPassword(ResetPasswordRequest request) {
        String email = tokenServicePort.validateAndExtractResetEmail(request.getToken());
        if (email == null) {
            throw new BadRequestException("El enlace de restablecimiento es inválido o ha expirado. Solicita uno nuevo.");
        }

        if (request.getNuevaPassword() == null || request.getNuevaPassword().length() < 8) {
            throw new BadRequestException("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado."));

        usuario.setPassword(passwordEncoderPort.encode(request.getNuevaPassword()));
        usuarioRepositoryPort.save(usuario);
    }

    @Override
    public void cambiarPassword(String token, ChangePasswordRequest request) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Sesión inválida");
        }

        String email = tokenServicePort.extractEmail(token);
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!passwordEncoderPort.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BadRequestException("La contraseña actual es incorrecta.");
        }

        if (request.getPasswordNueva() == null || request.getPasswordNueva().length() < 8) {
            throw new BadRequestException("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        usuario.setPassword(passwordEncoderPort.encode(request.getPasswordNueva()));
        usuarioRepositoryPort.save(usuario);
    }
}