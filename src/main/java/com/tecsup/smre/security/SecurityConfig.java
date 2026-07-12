package com.tecsup.smre.security;

import com.tecsup.smre.auth.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.JpaUsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final JpaUsuarioRepository jpaUsuarioRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    public SecurityConfig(JwtTokenProvider tokenProvider,
                          CustomUserDetailsService customUserDetailsService,
                          JpaUsuarioRepository jpaUsuarioRepository) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.jpaUsuarioRepository = jpaUsuarioRepository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🌟 NUEVO: Vincula de forma explícita tu servicio de usuarios con BCrypt
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new PasswordEncoderPort() {
            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return passwordEncoder.matches(rawPassword, encodedPassword);
            }

            @Override
            public String encode(String rawPassword) {
                return passwordEncoder.encode(rawPassword);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customOAuth2SuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String nombre = oAuth2User.getAttribute("name");
            if (nombre == null) {
                nombre = oAuth2User.getAttribute("given_name");
                if (nombre == null) {
                    nombre = "Usuario Google";
                }
            }

            if (email == null || !email.endsWith("@tecsup.edu.pe")) {
                response.sendRedirect("http://localhost:5173/login?error=domain_not_allowed");
                return;
            }

            java.util.Optional<com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity> usuarioOpt = 
                    jpaUsuarioRepository.findByEmail(email);

            com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity entity;
            if (usuarioOpt.isEmpty()) {
                entity = com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity.builder()
                        .nombre(nombre)
                        .email(email)
                        .password("$2a$10$OnbvO29b4UPh6hkse30Ry.9jVhC5IZa6WzMoOEPldlvQgYsrTQ8Ti")
                        .rol(com.tecsup.smre.auth.domain.model.Role.TUTOR)
                        .activo(true)
                        .build();
                entity = jpaUsuarioRepository.save(entity);
            } else {
                entity = usuarioOpt.get();
            }

            com.tecsup.smre.auth.domain.model.Usuario domainUser = 
                    com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioMapper.toDomain(entity);
            String token = tokenProvider.generateToken(domainUser);

            response.sendRedirect("http://localhost:5173/login?token=" + token);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;

                if (httpRequest.getRequestURI().equals("/oauth2/authorization/google") &&
                        ("placeholder-client-id".equals(googleClientId) || googleClientId == null || googleClientId.isBlank())) {

                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("email", "angelo.ricasca@tecsup.edu.pe");
                    attributes.put("name", "Angelo Ricasca");
                    attributes.put("sub", "angelo.ricasca@tecsup.edu.pe");

                    OAuth2User dummyUser = new DefaultOAuth2User(
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                            attributes,
                            "email"
                    );

                    OAuth2AuthenticationToken dummyAuth = new OAuth2AuthenticationToken(
                            dummyUser,
                            dummyUser.getAuthorities(),
                            "google"
                    );

                    try {
                        customOAuth2SuccessHandler().onAuthenticationSuccess(httpRequest, httpResponse, dummyAuth);
                    } catch (Exception e) {
                        throw new ServletException("Error al simular éxito de OAuth2", e);
                    }
                    return;
                }

                chain.doFilter(request, response);
            }
        }, org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.class);

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login", "/login/oauth2/**", "/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(customOAuth2SuccessHandler())
            );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
