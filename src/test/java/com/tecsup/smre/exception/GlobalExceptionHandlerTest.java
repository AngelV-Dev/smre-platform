package com.tecsup.smre.exception;

import com.tecsup.smre.common.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void whenResourceNotFound_thenReturn404AndApiResponse() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("El recurso no existe"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    public void whenBadRequest_thenReturn400AndApiResponse() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Solicitud inválida"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    public void whenUnauthorized_thenReturn401AndApiResponse() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    public void whenDomainException_thenReturn422AndApiResponse() throws Exception {
        mockMvc.perform(get("/test/domain-error"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Violación de regla de negocio"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    public void whenValidationFails_thenReturn400AndFieldErrorMap() throws Exception {
        String invalidJson = "{\"name\":\"\"}";
        mockMvc.perform(post("/test/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error de validación en los campos de la solicitud"))
                .andExpect(jsonPath("$.data.name").value("El nombre no puede estar en blanco"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    public void whenGenericException_thenReturn500AndApiResponse() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ha ocurrido un error interno en el servidor"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("El recurso no existe");
        }

        @GetMapping("/test/bad-request")
        public void badRequest() {
            throw new BadRequestException("Solicitud inválida");
        }

        @GetMapping("/test/unauthorized")
        public void unauthorized() {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        @GetMapping("/test/domain-error")
        public void domainError() {
            throw new DomainException("Violación de regla de negocio");
        }

        @GetMapping("/test/generic-error")
        public void genericError() {
            throw new RuntimeException("Error inesperado en BD");
        }

        @PostMapping("/test/validate")
        public ApiResponse<String> validate(@Valid @RequestBody TestDto dto) {
            return ApiResponse.success("ok");
        }
    }

    @Data
    static class TestDto {
        @NotBlank(message = "El nombre no puede estar en blanco")
        private String name;
    }
}
