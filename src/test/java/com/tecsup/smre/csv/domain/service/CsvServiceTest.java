package com.tecsup.smre.csv.domain.service;

import com.tecsup.smre.csv.application.dto.response.CsvUploadResponseDto;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CsvServiceTest {

    private final StudentRepositoryPort studentRepositoryPort = mock(StudentRepositoryPort.class);
    private final CsvService csvService = new CsvService(studentRepositoryPort);

    @Test
    void cargaCorrectamenteAlumnosValidos() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,Juana,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n"
                + "2024002,Pedro,Lopez,plopez@tecsup.edu.pe,Software,2024-1,B\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        when(studentRepositoryPort.existsByCodigo(any())).thenReturn(false);
        when(studentRepositoryPort.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(2, reporte.getTotalProcesados());
        assertEquals(2, reporte.getGuardados());
        assertEquals(0, reporte.getErrores());
        verify(studentRepositoryPort).saveAll(any());
    }

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
        assertTrue(reporte.getDetalleErrores().get(0).contains("ya existe"));
    }

    @Test
    void detectaCamposVacios() {
        String contenido = "codigo,nombre,apellido,email,carrera,semestre,grupo\n"
                + "2024001,,Perez,jperez@tecsup.edu.pe,Software,2024-1,A\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "alumnos.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        CsvUploadResponseDto reporte = csvService.cargar(file);

        assertEquals(1, reporte.getErrores());
        assertTrue(reporte.getDetalleErrores().get(0).contains("obligatorios"));
    }

    @Test
    void generarPlantillaIncluyeCabeceraCorrecta() {
        byte[] plantilla = csvService.generarPlantilla();
        String texto = new String(plantilla, StandardCharsets.UTF_8);

        assertTrue(texto.startsWith("codigo,nombre,apellido,email,carrera,semestre,grupo"));
    }
}