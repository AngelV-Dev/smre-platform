package com.tecsup.smre.auth.domain.service;

import com.tecsup.smre.auth.application.dto.request.LoginRequest;
import com.tecsup.smre.auth.application.dto.response.LoginResponse;
import com.tecsup.smre.auth.application.dto.response.UsuarioActualResponse;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.in.GetUsuarioActualUseCase;
import com.tecsup.smre.auth.domain.port.in.LoginUseCase;
import com.tecsup.smre.auth.domain.port.in.LogoutUseCase;
import com.tecsup.smre.auth.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.auth.domain.port.out.TokenServicePort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.UnauthorizedException;

public class AuthService implements LoginUseCase, LogoutUseCase, GetUsuarioActualUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenServicePort tokenServicePort;

    public AuthService(UsuarioRepositoryPort usuarioRepositoryPort,
                       PasswordEncoderPort passwordEncoderPort,
                       TokenServicePort tokenServicePort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenServicePort = tokenServicePort;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!usuario.hasTecsupDomain()) {
            throw new BadRequestException("Solo se permiten correos de Tecsup (@tecsup.edu.pe)");
        }

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
}