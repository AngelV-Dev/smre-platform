package com.tecsup.smre.csv.domain.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.tecsup.smre.csv.application.dto.response.CsvErrorDto;
import com.tecsup.smre.csv.application.dto.response.CsvUploadResponseDto;
import com.tecsup.smre.csv.domain.port.in.CargaCsvUseCase;
import com.tecsup.smre.csv.domain.port.in.DescargarPlantillaCsvUseCase;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvService implements CargaCsvUseCase, DescargarPlantillaCsvUseCase {

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
        List<CsvErrorDto> errores = new ArrayList<>();
        Set<String> codigosEnArchivo = new HashSet<>();
        int totalFilas = 0;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String[] cabecera = reader.readNext();
            if (cabecera == null) {
                throw new BadRequestException("El archivo CSV no tiene encabezado ni datos");
            }

            String[] fila;
            int numeroFila = 1;

            while ((fila = reader.readNext()) != null) {
                numeroFila++;
                totalFilas++;
                String datosRaw = String.join(",", fila);

                if (fila.length < CABECERA.length) {
                    errores.add(CsvErrorDto.builder()
                            .fila(numeroFila)
                            .motivo("Columnas insuficientes (se esperaban " + CABECERA.length + ")")
                            .datos(datosRaw)
                            .build());
                    continue;
                }

                String codigo   = fila[0].trim();
                String nombre   = fila[1].trim();
                String apellido = fila[2].trim();
                String email    = fila[3].trim();
                String carrera  = fila[4].trim();
                String semestre = fila[5].trim();
                String grupo    = fila[6].trim();

                // Validar campos vacíos
                if (codigo.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                        || email.isEmpty() || carrera.isEmpty()
                        || semestre.isEmpty() || grupo.isEmpty()) {
                    errores.add(CsvErrorDto.builder()
                            .fila(numeroFila)
                            .motivo("Campos obligatorios vacíos")
                            .datos(datosRaw)
                            .build());
                    continue;
                }

                // Validar formato de email
                if (!email.endsWith("@tecsup.edu.pe")) {
                    errores.add(CsvErrorDto.builder()
                            .fila(numeroFila)
                            .motivo("Email inválido (debe ser @tecsup.edu.pe)")
                            .datos(datosRaw)
                            .build());
                    continue;
                }

                // Validar duplicado dentro del mismo CSV
                if (codigosEnArchivo.contains(codigo)) {
                    errores.add(CsvErrorDto.builder()
                            .fila(numeroFila)
                            .motivo("Código duplicado en el archivo: " + codigo)
                            .datos(datosRaw)
                            .build());
                    continue;
                }

                // Validar duplicado en base de datos
                if (studentRepositoryPort.existsByCodigo(codigo)) {
                    errores.add(CsvErrorDto.builder()
                            .fila(numeroFila)
                            .motivo("Ya existe un alumno con código " + codigo + " en el sistema")
                            .datos(datosRaw)
                            .build());
                    continue;
                }

                codigosEnArchivo.add(codigo);
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