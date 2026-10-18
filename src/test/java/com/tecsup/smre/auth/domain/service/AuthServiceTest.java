package com.tecsup.smre.auth.domain.service;

import com.tecsup.smre.auth.application.dto.request.LoginRequest;
import com.tecsup.smre.auth.application.dto.response.LoginResponse;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
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
    private EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        usuarioRepositoryPort = Mockito.mock(UsuarioRepositoryPort.class);
        passwordEncoderPort = Mockito.mock(PasswordEncoderPort.class);
        tokenServicePort = Mockito.mock(TokenServicePort.class);
        emailMatriculadoRepositoryPort = Mockito.mock(EmailMatriculadoRepositoryPort.class);
        authService = new AuthService(usuarioRepositoryPort, passwordEncoderPort, tokenServicePort, emailMatriculadoRepositoryPort);
    }

    @Test
    public void whenValidCredentialsAndMatriculated_thenReturnLoginResponse() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan.perez@tecsup.edu.pe")
                .password("encoded_pass")
                .rol(Role.TUTOR)
                .activo(true)
                .build();

        when(emailMatriculadoRepositoryPort.existsByEmail("juan.perez@tecsup.edu.pe")).thenReturn(true);
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
    public void whenEmailNotMatriculated_thenThrowUnauthorizedException() {
        when(emailMatriculadoRepositoryPort.existsByEmail("juan.perez@tecsup.edu.pe")).thenReturn(false);

        LoginRequest request = new LoginRequest("juan.perez@tecsup.edu.pe", "raw_pass");
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().contains("no se encuentra matriculado"));
    }

    @Test
    public void whenEmailNotTecsup_thenThrowBadRequestException() {
        LoginRequest request = new LoginRequest("juan@gmail.com", "pass");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().contains("Solo se permiten correos de Tecsup"));
    }

    @Test
    public void whenEmailNotExistsInUsuarios_thenThrowUnauthorizedException() {
        when(emailMatriculadoRepositoryPort.existsByEmail("nonexistent@tecsup.edu.pe")).thenReturn(true);
        when(usuarioRepositoryPort.findByEmail("nonexistent@tecsup.edu.pe")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("nonexistent@tecsup.edu.pe", "pass");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    public void whenPasswordInvalid_thenThrowUnauthorizedException() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@tecsup.edu.pe")
                .password("encoded_pass")
                .rol(Role.TUTOR)
                .activo(true)
                .build();

        when(emailMatriculadoRepositoryPort.existsByEmail("juan@tecsup.edu.pe")).thenReturn(true);
        when(usuarioRepositoryPort.findByEmail("juan@tecsup.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoderPort.matches("invalid_pass", "encoded_pass")).thenReturn(false);

        LoginRequest request = new LoginRequest("juan@tecsup.edu.pe", "invalid_pass");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
