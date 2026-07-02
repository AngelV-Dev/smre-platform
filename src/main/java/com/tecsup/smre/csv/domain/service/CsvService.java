package com.tecsup.smre.csv.domain.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.tecsup.smre.csv.application.dto.response.CsvUploadResponseDto;
import com.tecsup.smre.csv.domain.port.in.CargaCsvUseCase;
import com.tecsup.smre.csv.domain.port.in.DescargarPlantillaCsvUseCase;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvService implements CargaCsvUseCase, DescargarPlantillaCsvUseCase {

    // Formato esperado: codigo,nombre,apellido,email,carrera,semestre,grupo
    private static final String[] CABECERA = {
            "codigo", "nombre", "apellido", "email", "carrera", "semestre", "grupo"
    };

    private final StudentRepositoryPort studentRepositoryPort;

    public CsvService(StudentRepositoryPort studentRepositoryPort) {
        this.studentRepositoryPort = studentRepositoryPort;
    }

    @Override
    public CsvUploadResponseDto cargar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo CSV está vacío");
        }

        List<Alumno> alumnosValidos = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int totalFilas = 0;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String[] cabecera = reader.readNext(); // primera fila = encabezado, se descarta
            if (cabecera == null) {
                throw new BadRequestException("El archivo CSV no tiene encabezado ni datos");
            }

            String[] fila;
            int numeroFila = 1;

            while ((fila = reader.readNext()) != null) {
                numeroFila++;
                totalFilas++;

                if (fila.length < CABECERA.length) {
                    errores.add("Fila " + numeroFila + ": se esperaban " + CABECERA.length
                            + " columnas (codigo,nombre,apellido,email,carrera,semestre,grupo), "
                            + "se encontraron " + fila.length);
                    continue;
                }

                String codigo = fila[0].trim();
                String nombre = fila[1].trim();
                String apellido = fila[2].trim();
                String email = fila[3].trim();
                String carrera = fila[4].trim();
                String semestre = fila[5].trim();
                String grupo = fila[6].trim();

                if (codigo.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                        || email.isEmpty() || carrera.isEmpty()
                        || semestre.isEmpty() || grupo.isEmpty()) {
                    errores.add("Fila " + numeroFila + ": todos los campos son obligatorios");
                    continue;
                }

                if (studentRepositoryPort.existsByCodigo(codigo)) {
                    errores.add("Fila " + numeroFila + ": ya existe un alumno con código " + codigo);
                    continue;
                }

                alumnosValidos.add(Alumno.builder()
                        .codigo(codigo)
                        .nombre(nombre)
                        .apellido(apellido)
                        .email(email)
                        .carrera(carrera)
                        .semestre(semestre)
                        .grupo(grupo)
                        .build());
            }

        } catch (BadRequestException e) {
            throw e;
        } catch (CsvValidationException | java.io.IOException e) {
            throw new BadRequestException("Error al leer el archivo CSV: " + e.getMessage());
        }

        int guardados = 0;
        if (!alumnosValidos.isEmpty()) {
            guardados = studentRepositoryPort.saveAll(alumnosValidos).size();
        }

        return CsvUploadResponseDto.builder()
                .totalProcesados(totalFilas)
                .guardados(guardados)
                .errores(errores.size())
                .detalleErrores(errores)
                .build();
    }

    @Override
    public byte[] generarPlantilla() {
        String contenido = String.join(",", CABECERA) + "\n"
                + "2024001,Juana,Perez Lopez,jperez@tecsup.edu.pe,Software,2024-1,A\n";
        return contenido.getBytes(StandardCharsets.UTF_8);
    }
}
