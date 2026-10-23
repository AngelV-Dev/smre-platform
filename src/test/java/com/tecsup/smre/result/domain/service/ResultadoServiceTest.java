package com.tecsup.smre.result.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.exception.UnauthorizedException;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;
import com.tecsup.smre.result.domain.port.out.ResultadoRepositoryPort;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
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
                .alumnoId("10")
                .alumnoNombre("Juana")
                .alumnoApellido("Perez")
                .tutorId("99")
                .tutorNombre("Karina Salas")
                .puntajeTotal(45)
                .nivelRiesgo(NivelRiesgo.ALTO)
                .recomendacion("Requiere seguimiento cercano")
                .observaciones("Faltas reiteradas")
                .fecha(LocalDateTime.now())
                .respuestas("ALTO,MEDIO,BAJO,ALTO,MEDIO,BAJO")
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

    @Test
    void exportarGeneraExcelValidoConDatosYRespuestas() throws Exception {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(entrevistaDeEjemplo()));

        Usuario admin = Usuario.builder().id(1L).rol(Role.ADMIN).build();

        byte[] excelBytes = service.exportar(1L, admin);

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheet("Entrevista");
            assertNotNull(sheet);
            assertEquals("REPORTE DE ENTREVISTA - SMRE", sheet.getRow(0).getCell(0).getStringCellValue());
        }
    }

    @Test
    void exportarTutorDuenioLanzaUnauthorizedException() {
        // En entrevistaDeEjemplo el tutorId es '2'
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(entrevistaDeEjemplo()));

        Usuario tutorDuenio = Usuario.builder().id(2L).rol(Role.TUTOR).build();

        assertThrows(UnauthorizedException.class, () -> service.exportar(1L, tutorDuenio));
    }

    @Test
    void exportarAdminTutorPermiteGenerarExcel() throws Exception {
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(entrevistaDeEjemplo()));

        Usuario adminTutor = Usuario.builder().id(2L).rol(Role.ADMIN_TUTOR).build();

        byte[] excelBytes = service.exportar(1L, adminTutor);

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            assertNotNull(workbook.getSheet("Entrevista"));
        }
    }
}
