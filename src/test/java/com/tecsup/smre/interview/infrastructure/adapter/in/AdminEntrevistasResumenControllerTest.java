package com.tecsup.smre.interview.infrastructure.adapter.in;

import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.interview.application.dto.response.AlumnoResumenDto;
import com.tecsup.smre.interview.application.dto.response.GrupoEntrevistasDto;
import com.tecsup.smre.interview.application.dto.response.ResumenEntrevistasResponseDto;
import com.tecsup.smre.interview.application.port.in.GetResumenEntrevistasAdminUseCase;
import com.tecsup.smre.security.CustomUserDetailsService;
import com.tecsup.smre.security.JwtTokenProvider;
import com.tecsup.smre.security.OAuth2LoginSuccessHandler;
import com.tecsup.smre.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEntrevistasResumenController.class)
@Import(SecurityConfig.class)
public class AdminEntrevistasResumenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetResumenEntrevistasAdminUseCase useCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Test
    @WithMockUser(username = "admin@tecsup.edu.pe", roles = {"ADMIN"})
    @DisplayName("Admin agrupa por tutor exitosamente (200 OK)")
    void whenAdminGroupsByTutor_thenReturn200() throws Exception {
        AlumnoResumenDto alumno = AlumnoResumenDto.builder()
                .id(1L)
                .nombre("Luis")
                .apellido("Gonzales")
                .carrera("Software")
                .tutorNombre("Juan Perez")
                .ultimoNivelRiesgo("MEDIO")
                .totalEntrevistas(1)
                .build();

        GrupoEntrevistasDto grupo = GrupoEntrevistasDto.builder()
                .nombre("Juan Perez")
                .totalAlumnos(1)
                .evaluados(1)
                .alumnos(List.of(alumno))
                .build();

        ResumenEntrevistasResponseDto dto = ResumenEntrevistasResponseDto.builder()
                .agruparPor("tutor")
                .grupos(List.of(grupo))
                .build();

        when(useCase.execute(eq("tutor"))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/admin/entrevistas/resumen?agruparPor=tutor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.agruparPor").value("tutor"))
                .andExpect(jsonPath("$.data.grupos", hasSize(1)))
                .andExpect(jsonPath("$.data.grupos[0].nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.data.grupos[0].totalAlumnos").value(1))
                .andExpect(jsonPath("$.data.grupos[0].evaluados").value(1))
                .andExpect(jsonPath("$.data.grupos[0].alumnos[0].ultimoNivelRiesgo").value("MEDIO"));
    }

    @Test
    @WithMockUser(username = "admin@tecsup.edu.pe", roles = {"ADMIN"})
    @DisplayName("Admin agrupa por carrera exitosamente (200 OK)")
    void whenAdminGroupsByCarrera_thenReturn200() throws Exception {
        GrupoEntrevistasDto grupo = GrupoEntrevistasDto.builder()
                .nombre("Software")
                .totalAlumnos(5)
                .evaluados(2)
                .alumnos(List.of())
                .build();

        ResumenEntrevistasResponseDto dto = ResumenEntrevistasResponseDto.builder()
                .agruparPor("carrera")
                .grupos(List.of(grupo))
                .build();

        when(useCase.execute(eq("carrera"))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/admin/entrevistas/resumen?agruparPor=carrera")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.agruparPor").value("carrera"))
                .andExpect(jsonPath("$.data.grupos[0].nombre").value("Software"));
    }

    @Test
    @WithMockUser(username = "admin@tecsup.edu.pe", roles = {"ADMIN"})
    @DisplayName("Parámetro inválido retorna 400 Bad Request")
    void whenInvalidAgruparPor_thenReturn400() throws Exception {
        when(useCase.execute(eq("invalido")))
                .thenThrow(new BadRequestException("El parámetro agruparPor solo permite 'tutor' o 'carrera'"));

        mockMvc.perform(get("/api/v1/admin/entrevistas/resumen?agruparPor=invalido")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("El parámetro agruparPor solo permite 'tutor' o 'carrera'"));
    }

    @Test
    @WithMockUser(username = "tutor@tecsup.edu.pe", roles = {"TUTOR"})
    @DisplayName("Tutor puro recibe 403 Forbidden")
    void whenPureTutorRequestsResumen_thenReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/entrevistas/resumen?agruparPor=tutor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Usuario no autenticado recibe 401 Unauthorized")
    void whenUnauthenticated_thenReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/entrevistas/resumen?agruparPor=tutor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
