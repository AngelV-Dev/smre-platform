package com.tecsup.smre.result.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.exception.UnauthorizedException;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;
import com.tecsup.smre.result.domain.port.out.ResultadoRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultadoServiceTest {

    private final ResultadoRepositoryPort repositoryPort = mock(ResultadoRepositoryPort.class);
    private final ResultadoService service = new ResultadoService(repositoryPort);

    private Entrevista entrevistaDeEjemplo() {
        return Entrevista.builder()
                .id(1L)
                .alumnoId(10L)
                .alumnoNombre("Juana")
                .alumnoApellido("Perez")
                .tutorId(99L)
                .tutorNombre("Karina Salas")
                .puntajeTotal(45)
                .nivelRiesgo(NivelRiesgo.ALTO)
                .recomendacion("Requiere seguimiento cercano")
                .observaciones("Faltas reiteradas")
                .fecha(LocalDateTime.now())
                .build();
    }

    @Test
    void adminPuedeVerCualquierResultado() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(entrevistaDeEjemplo()));

        Usuario admin = Usuario.builder().id(1L).rol(Role.ADMIN).build();

        ResultadoResponseDto resultado = service.getResultado(1L, admin);

        assertEquals("ALTO", resultado.getNivelRiesgo());
        assertEquals("Juana", resultado.getAlumnoNombre());
    }

    @Test
    void tutorAsignadoPuedeVerSuResultado() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(entrevistaDeEjemplo()));

        Usuario tutorAsignado = Usuario.builder().id(99L).rol(Role.TUTOR).build();

        ResultadoResponseDto resultado = service.getResultado(1L, tutorAsignado);

        assertEquals("ALTO", resultado.getNivelRiesgo());
    }

    @Test
    void tutorNoAsignadoNoPuedeVerElResultado() {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(entrevistaDeEjemplo()));

        Usuario tutorAjeno = Usuario.builder().id(5L).rol(Role.TUTOR).build();

        assertThrows(UnauthorizedException.class, () -> service.getResultado(1L, tutorAjeno));
    }

    @Test
    void entrevistaInexistenteLanzaResourceNotFound() {
        when(repositoryPort.findById(404L)).thenReturn(Optional.empty());

        Usuario admin = Usuario.builder().id(1L).rol(Role.ADMIN).build();

        assertThrows(ResourceNotFoundException.class, () -> service.getResultado(404L, admin));
    }

    @Test
    void historialDevuelveTodasLasEntrevistasDelAlumnoOrdenadas() {
        Entrevista e1 = entrevistaDeEjemplo();
        Entrevista e2 = entrevistaDeEjemplo();
        e2.setId(2L);
        e2.setNivelRiesgo(NivelRiesgo.MEDIO);

        when(repositoryPort.findHistorialByAlumnoId(10L)).thenReturn(List.of(e1, e2));

        Usuario admin = Usuario.builder().id(1L).rol(Role.ADMIN).build();

        List<ResultadoResponseDto> historial = service.getHistorial(10L, admin);

        assertEquals(2, historial.size());
        assertEquals("ALTO", historial.get(0).getNivelRiesgo());
        assertEquals("MEDIO", historial.get(1).getNivelRiesgo());
    }
}