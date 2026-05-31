package com.tecsup.smre.student.domain.service;

import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.student.application.dto.request.StudentRequest;
import com.tecsup.smre.student.application.dto.response.StudentResponse;
import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.student.domain.port.in.ManageStudentUseCase;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StudentService implements ManageStudentUseCase {

    private final StudentRepositoryPort studentRepositoryPort;

    public StudentService(StudentRepositoryPort studentRepositoryPort) {
        this.studentRepositoryPort = studentRepositoryPort;
    }

    @Override
    public List<StudentResponse> findAll() {
        return studentRepositoryPort.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public StudentResponse create(StudentRequest request) {
        if (studentRepositoryPort.existsByCodigo(request.getCodigo())) {
            throw new BadRequestException("Ya existe un alumno con el código: " + request.getCodigo());
        }

        Alumno alumno = Alumno.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .carrera(request.getCarrera())
                .semestre(request.getSemestre())
                .grupo(request.getGrupo())
                .build();

        return toResponse(studentRepositoryPort.save(alumno));
    }

    @Override
    public List<StudentResponse> uploadCsv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("El archivo CSV está vacío");
        }

        String contentType = file.getContentType();
        if (contentType != null
                && !contentType.equals("text/csv")
                && !contentType.equals("application/vnd.ms-excel")
                && !contentType.equals("application/octet-stream")) {
            throw new BadRequestException("El archivo debe ser de tipo CSV");
        }

        List<Alumno> alumnos = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        // Formato esperado: codigo,nombre,apellido,email,carrera,semestre,grupo
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new BadRequestException("El archivo CSV está vacío");
            }

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] fields = line.split(",", -1);

                if (fields.length < 7) {
                    errores.add("Línea " + lineNumber + ": se esperaban 7 columnas "
                            + "(codigo,nombre,apellido,email,carrera,semestre,grupo), "
                            + "se encontraron " + fields.length);
                    continue;
                }

                String codigo   = fields[0].trim();
                String nombre   = fields[1].trim();
                String apellido = fields[2].trim();
                String email    = fields[3].trim();
                String carrera  = fields[4].trim();
                String semestre = fields[5].trim();
                String grupo    = fields[6].trim();

                if (codigo.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                        || email.isEmpty() || carrera.isEmpty()
                        || semestre.isEmpty() || grupo.isEmpty()) {
                    errores.add("Línea " + lineNumber + ": todos los campos son obligatorios");
                    continue;
                }

                if (studentRepositoryPort.existsByCodigo(codigo)) {
                    errores.add("Línea " + lineNumber + ": ya existe un alumno con código " + codigo);
                    continue;
                }

                alumnos.add(Alumno.builder()
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
        } catch (Exception e) {
            throw new BadRequestException("Error al procesar el archivo CSV: " + e.getMessage());
        }

        if (!errores.isEmpty() && alumnos.isEmpty()) {
            throw new BadRequestException(
                    "No se pudo importar ningún alumno. Errores: " + String.join("; ", errores));
        }

        return studentRepositoryPort.saveAll(alumnos).stream()
                .map(this::toResponse)
                .toList();
    }

    private StudentResponse toResponse(Alumno alumno) {
        return StudentResponse.builder()
                .id(alumno.getId())
                .codigo(alumno.getCodigo())
                .nombre(alumno.getNombre())
                .apellido(alumno.getApellido())
                .email(alumno.getEmail())
                .carrera(alumno.getCarrera())
                .semestre(alumno.getSemestre())
                .grupo(alumno.getGrupo())
                .build();
    }
}