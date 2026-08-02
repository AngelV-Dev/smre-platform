package com.tecsup.smre.csv.domain.service;

import com.tecsup.smre.csv.application.dto.response.CsvUploadResponseDto;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CsvServiceTest {

    private final StudentRepositoryPort studentRepositoryPort = mock(StudentRepositoryPort.class);
    private final CsvService csvService = new CsvService(studentRepositoryPort);

    // ------------------------------------------------------------------ //
    //  Caso: ÉXITO COMPLETO — todos los alumnos se guardan correctamente  //
    // ------------------------------------------------------------------ //

    @Test
    void cargaExitosaRetornaExitosoTrue() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,Juana,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n"
                + "2024002,Pedro,Lopez,plopez@tecsup.edu.pe,Software,2024-1,B\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        when(studentRepositoryPort.existsByCodigo(anyString())).thenReturn(false);
        when(studentRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(studentRepositoryPort.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(2, reporte.getTotalProcesados());
        assertEquals(2, reporte.getGuardados());
        assertEquals(0, reporte.getErrores());
        assertTrue(reporte.isExitoso(), "La carga deberia ser marcada como exitosa");
        assertTrue(reporte.getDetalleErrores().isEmpty());
        verify(studentRepositoryPort).saveAll(any());
    }

    @Test
    void cargaCorrectamenteAlumnosValidos() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,Juana,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n"
                + "2024002,Pedro,Lopez,plopez@tecsup.edu.pe,Software,2024-1,B\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        when(studentRepositoryPort.existsByCodigo(any())).thenReturn(false);
        when(studentRepositoryPort.existsByEmail(any())).thenReturn(false);
        when(studentRepositoryPort.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(2, reporte.getTotalProcesados());
        assertEquals(2, reporte.getGuardados());
        assertEquals(0, reporte.getErrores());
        verify(studentRepositoryPort).saveAll(any());
    }

    // ------------------------------------------------------------------ //
    //  Caso: ÉXITO PARCIAL — algunos guardados, algunos con error         //
    // ------------------------------------------------------------------ //

    @Test
    void cargaParcialRetornaExitosoFalse() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,Juana,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n"
                + "2024002,Pedro,Lopez,plopez@tecsup.edu.pe,Software,2024-1,B\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        // Solo el primer codigo ya existe
        when(studentRepositoryPort.existsByCodigo("2024001")).thenReturn(true);
        when(studentRepositoryPort.existsByCodigo("2024002")).thenReturn(false);
        when(studentRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(studentRepositoryPort.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(2, reporte.getTotalProcesados());
        assertEquals(1, reporte.getGuardados());
        assertEquals(1, reporte.getErrores());
        assertFalse(reporte.isExitoso(), "Carga parcial no debe marcarse como exitosa");
    }

    // ------------------------------------------------------------------ //
    //  Caso: FALLO — codigo duplicado en BD                               //
    // ------------------------------------------------------------------ //

    @Test
    void detectaCodigoDuplicadoEnBD() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,Juana,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        when(studentRepositoryPort.existsByCodigo("2024001")).thenReturn(true);

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(1, reporte.getTotalProcesados());
        assertEquals(0, reporte.getGuardados());
        assertEquals(1, reporte.getErrores());
        assertFalse(reporte.isExitoso());
        assertTrue(reporte.getDetalleErrores().get(0).getMotivo().contains("ya existe")
                || reporte.getDetalleErrores().get(0).getMotivo().contains("código"));
    }

    // ------------------------------------------------------------------ //
    //  Caso: FALLO — email duplicado en BD                                //
    // ------------------------------------------------------------------ //

    @Test
    void detectaEmailDuplicadoEnBD() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024099,Ana,Torres,atorres@tecsup.edu.pe,Sistemas,2024-1,C\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        when(studentRepositoryPort.existsByCodigo("2024099")).thenReturn(false);
        when(studentRepositoryPort.existsByEmail("atorres@tecsup.edu.pe")).thenReturn(true);

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(1, reporte.getTotalProcesados());
        assertEquals(0, reporte.getGuardados());
        assertEquals(1, reporte.getErrores());
        assertFalse(reporte.isExitoso());
        assertTrue(reporte.getDetalleErrores().get(0).getMotivo().contains("email"));
    }

    // ------------------------------------------------------------------ //
    //  Caso: FALLO — codigo duplicado dentro del mismo CSV                //
    // ------------------------------------------------------------------ //

    @Test
    void detectaCodigoDuplicado() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,Juana,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        when(studentRepositoryPort.existsByCodigo("2024001")).thenReturn(true);

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(1, reporte.getTotalProcesados());
        assertEquals(0, reporte.getGuardados());
        assertEquals(1, reporte.getErrores());
        assertTrue(reporte.getDetalleErrores().get(0).getMotivo().contains("ya existe")
                || reporte.getDetalleErrores().get(0).getMotivo().contains("código"));
    }

    // ------------------------------------------------------------------ //
    //  Caso: FALLO — campos obligatorios vacíos                           //
    // ------------------------------------------------------------------ //

    @Test
    void detectaCamposVacios() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(1, reporte.getErrores());
        assertFalse(reporte.isExitoso());
        assertTrue(reporte.getDetalleErrores().get(0).getMotivo().contains("obligatorios"));
    }

    // ------------------------------------------------------------------ //
    //  Caso: FALLO — email con dominio incorrecto                         //
    // ------------------------------------------------------------------ //

    @Test
    void detectaEmailConDominioInvalido() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024003,Carlos,Ruiz,cruiz@gmail.com,Software,2024-1,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(1, reporte.getErrores());
        assertFalse(reporte.isExitoso());
        assertTrue(reporte.getDetalleErrores().get(0).getMotivo().contains("inválido")
                || reporte.getDetalleErrores().get(0).getMotivo().contains("Email"));
    }

    // ------------------------------------------------------------------ //
    //  Plantilla                                                          //
    // ------------------------------------------------------------------ //

    @Test
    void generarPlantillaIncluyeCabeceraCorrecta() {
        byte[] plantilla = csvService.generarPlantilla();
        String texto = new String(plantilla, StandardCharsets.UTF_8);

        assertTrue(texto.startsWith("codigo,nombre,apellido,email,carrera,semestre,grupo"));
    }
}