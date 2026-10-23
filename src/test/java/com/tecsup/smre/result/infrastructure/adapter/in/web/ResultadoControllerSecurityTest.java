package com.tecsup.smre.result.infrastructure.adapter.in.web;

import com.tecsup.smre.result.domain.port.in.ExportarResultadoUseCase;
import com.tecsup.smre.result.domain.port.in.GetHistorialEntrevistasUseCase;
import com.tecsup.smre.result.domain.port.in.GetResultadoEntrevistaUseCase;
import com.tecsup.smre.security.CustomUserDetailsService;
import com.tecsup.smre.security.JwtAuthenticationFilter;
import com.tecsup.smre.security.JwtTokenProvider;
import com.tecsup.smre.security.OAuth2LoginSuccessHandler;
import com.tecsup.smre.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResultadoController.class)
@Import(SecurityConfig.class)
class ResultadoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetResultadoEntrevistaUseCase getResultadoEntrevistaUseCase;

    @MockBean
    private GetHistorialEntrevistasUseCase getHistorialEntrevistasUseCase;

    @MockBean
    private ExportarResultadoUseCase exportarResultadoUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Test
    @WithMockUser(username = "tutor@tecsup.edu.pe", roles = {"TUTOR"})
    void exportarConRolTutorRetornaForbidden403() throws Exception {
        mockMvc.perform(get("/api/entrevistas/1/exportar"))
                .andExpect(status().isForbidden());
    }

    @Test
    void exportarConRolAdminTutorRetornaOk200() throws Exception {
        com.tecsup.smre.auth.domain.model.Usuario usuario = com.tecsup.smre.auth.domain.model.Usuario.builder()
                .id(1L)
                .email("admintutor@tecsup.edu.pe")
                .rol(com.tecsup.smre.auth.domain.model.Role.ADMIN_TUTOR)
                .build();
        com.tecsup.smre.security.UserPrincipal principal = new com.tecsup.smre.security.UserPrincipal(usuario);
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());

        when(exportarResultadoUseCase.exportar(eq(1L), any())).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/entrevistas/1/exportar").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk());
    }

    @Test
    void exportarConRolAdminRetornaOk200() throws Exception {
        com.tecsup.smre.auth.domain.model.Usuario usuario = com.tecsup.smre.auth.domain.model.Usuario.builder()
                .id(2L)
                .email("admin@tecsup.edu.pe")
                .rol(com.tecsup.smre.auth.domain.model.Role.ADMIN)
                .build();
        com.tecsup.smre.security.UserPrincipal principal = new com.tecsup.smre.security.UserPrincipal(usuario);
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());

        when(exportarResultadoUseCase.exportar(eq(1L), any())).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/entrevistas/1/exportar").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk());
    }
}
