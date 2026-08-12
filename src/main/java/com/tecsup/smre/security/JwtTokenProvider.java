package com.tecsup.smre.security;

import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.TokenServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider implements TokenServicePort {

    private final SecretKey key;
    private final long jwtExpirationInMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:default_jwt_secret_key_at_least_32_bytes_long_for_smre_app_security}") String jwtSecret,
            @Value("${jwt.expiration:86400000}") long jwtExpirationInMs) {
        byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(secretBytes, 0, padded, 0, secretBytes.length);
            this.key = Keys.hmacShaKeyFor(padded);
        } else {
            this.key = Keys.hmacShaKeyFor(secretBytes);
        }
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Override
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("role", usuario.getRol().name())
                .claim("nombre", usuario.getNombre())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return getUsernameFromJWT(token);
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Token JWT inválido: {}", ex.getMessage());
        }
        return false;
    }
}
