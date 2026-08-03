package com.tecsup.smre.security;

import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(UsuarioRepositoryPort usuarioRepositoryPort,
                                      JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        log.info("Login con Google recibido para el correo: {}", email);

        if (email == null || !email.endsWith("@tecsup.edu.pe")) {
            redirectWithError(response, "dominio_no_autorizado");
            return;
        }

        Optional<Usuario> usuarioOpt = usuarioRepositoryPort.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            // No se crea cuenta automática: un admin debe registrarlo antes
            redirectWithError(response, "usuario_no_registrado");
            return;
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.isActivo()) {
            redirectWithError(response, "usuario_inactivo");
            return;
        }

        String token = jwtTokenProvider.generateToken(usuario);

        String destino = switch (usuario.getRol()) {
            case ADMIN -> "/admin/dashboard";
            case TUTOR -> "/admin/entrevistas";
        };

        String redirectUrl = frontendUrl + "/oauth-callback"
                + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&redirect=" + URLEncoder.encode(destino, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }

    private void redirectWithError(HttpServletResponse response, String errorCode) throws IOException {
        response.sendRedirect(frontendUrl + "/login?error=" + errorCode);
    }
}