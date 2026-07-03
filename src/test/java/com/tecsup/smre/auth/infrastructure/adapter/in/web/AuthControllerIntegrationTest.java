package com.tecsup.smre.auth.infrastructure.adapter.in.web;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.JpaUsuarioRepository;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUsuarioRepository jpaUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        jpaUsuarioRepository.deleteAll();

        UsuarioEntity entity = UsuarioEntity.builder()
                .nombre("Eduardo Tutor")
                .email("eduardo.tutor@tecsup.edu.pe")
                .password(passwordEncoder.encode("secret123"))
                .rol(Role.TUTOR)
                .activo(true)
                .build();

        jpaUsuarioRepository.save(entity);
    }

    @Test
    public void whenValidLoginRequest_thenReturnToken() throws Exception {
        String loginJson = "{\"email\":\"eduardo.tutor@tecsup.edu.pe\",\"password\":\"secret123\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sesión iniciada con éxito"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.email").value("eduardo.tutor@tecsup.edu.pe"))
                .andExpect(jsonPath("$.data.nombre").value("Eduardo Tutor"))
                .andExpect(jsonPath("$.data.role").value("TUTOR"));
    }

    @Test
    public void whenInvalidLoginPassword_thenReturn401() throws Exception {
        String loginJson = "{\"email\":\"eduardo.tutor@tecsup.edu.pe\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }
}
