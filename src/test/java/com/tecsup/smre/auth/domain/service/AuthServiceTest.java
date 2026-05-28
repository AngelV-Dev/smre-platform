package com.tecsup.smre.auth.domain.service;

import com.tecsup.smre.auth.application.dto.request.LoginRequest;
import com.tecsup.smre.auth.application.dto.response.LoginResponse;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.auth.domain.port.out.TokenServicePort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    private UsuarioRepositoryPort usuarioRepositoryPort;
    private PasswordEncoderPort passwordEncoderPort;
    private TokenServicePort tokenServicePort;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        usuarioRepositoryPort = Mockito.mock(UsuarioRepositoryPort.class);
        passwordEncoderPort = Mockito.mock(PasswordEncoderPort.class);
        tokenServicePort = Mockito.mock(TokenServicePort.class);
        authService = new AuthService(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort);
    }

    @Test
    public void whenValidCredentials_thenReturnLoginResponse() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan.perez@tecsup.edu.pe")
                .password("encoded_pass")
                .rol(Role.TUTOR)
                .build();

        when(usuarioRepositoryPort.findByEmail("juan.perez@tecsup.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches("raw_pass", "encoded_pass")).thenReturn(true);
        when(tokenServicePort.generateToken(usuario)).thenReturn("mock_token");

        LoginRequest request = new LoginRequest("juan.perez@tecsup.edu.pe", "raw_pass");
        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock_token", response.getToken());
        assertEquals("juan.perez@tecsup.edu.pe", response.getEmail());
        assertEquals("TUTOR", response.getRole());
    }

    @Test
    public void whenEmailNotExists_thenThrowUnauthorizedException() {
        when(usuarioRepositoryPort.findByEmail("nonexistent@tecsup.edu.pe")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("nonexistent@tecsup.edu.pe", "pass");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    public void whenEmailNotTecsup_thenThrowBadRequestException() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@gmail.com")
                .password("encoded_pass")
                .rol(Role.TUTOR)
                .build();

        when(usuarioRepositoryPort.findByEmail("juan@gmail.com")).thenReturn(Optional.of(usuario));

        LoginRequest request = new LoginRequest("juan@gmail.com", "pass");

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    public void whenPasswordInvalid_thenThrowUnauthorizedException() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@tecsup.edu.pe")
                .password("encoded_pass")
                .rol(Role.TUTOR)
                .build();

        when(usuarioRepositoryPort.findByEmail("juan@tecsup.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches("invalid_pass", "encoded_pass")).thenReturn(false);

        LoginRequest request = new LoginRequest("juan@tecsup.edu.pe", "invalid_pass");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
