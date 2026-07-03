package com.tecsup.smre.result.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.exception.UnauthorizedException;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;
import com.tecsup.smre.result.domain.port.in.ExportarResultadoUseCase;
import com.tecsup.smre.result.domain.port.in.GetHistorialEntrevistasUseCase;
import com.tecsup.smre.result.domain.port.in.GetResultadoEntrevistaUseCase;
import com.tecsup.smre.result.domain.port.out.ResultadoRepositoryPort;

import java.util.List;

public class ResultadoService implements
        GetResultadoEntrevistaUseCase,
        GetHistorialEntrevistasUseCase,
        ExportarResultadoUseCase {

    private final ResultadoRepositoryPort resultadoRepositoryPort;

    public ResultadoService(ResultadoRepositoryPort resultadoRepositoryPort) {
        this.resultadoRepositoryPort = resultadoRepositoryPort;
    }

    @Override
    public ResultadoResponseDto getResultado(Long entrevistaId, Usuario solicitante) {
        Entrevista entrevista = resultadoRepositoryPort.findById(entrevistaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la entrevista con id: " + entrevistaId));

        validarAcceso(entrevista, solicitante);
        return toResponse(entrevista);
    }

    @Override
    public List<ResultadoResponseDto> getHistorial(Long alumnoId, Usuario solicitante) {
        List<Entrevista> entrevistas = resultadoRepositoryPort.findHistorialByAlumnoId(alumnoId);

        // El historial es por alumno: si hay al menos una entrevista, basta validar acceso con la primera
        // (todas pertenecen al mismo alumno/tutor en este flujo).
        if (!entrevistas.isEmpty()) {
            validarAcceso(entrevistas.get(0), solicitante);
        }

        return entrevistas.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public byte[] exportar(Long entrevistaId, Usuario solicitante) {
        ResultadoResponseDto resultado = getResultado(entrevistaId, solicitante);

        StringBuilder csv = new StringBuilder();

        // Encabezados
        csv.append("Campo,Valor\n");

        // Datos del alumno
        csv.append("Nombre,").append(resultado.getAlumnoNombre()).append(" ").append(resultado.getAlumnoApellido()).append("\n");
        csv.append("ID Alumno,").append(resultado.getAlumnoId()).append("\n");
        csv.append("Tutor,").append(resultado.getTutorNombre()).append("\n");
        csv.append("Fecha,").append(resultado.getFecha() != null ? resultado.getFecha().toString() : "").append("\n");

        // Resultado
        csv.append("Puntaje Total,").append(resultado.getPuntajeTotal()).append("\n");
        csv.append("Nivel de Riesgo,").append(resultado.getNivelRiesgo()).append("\n");
        csv.append("Recomendación,\"").append(resultado.getRecomendacion() != null ? resultado.getRecomendacion().replace("\"", "\"\"") : "").append("\"\n");
        csv.append("Observaciones,\"").append(resultado.getObservaciones() != null ? resultado.getObservaciones().replace("\"", "\"\"") : "").append("\"\n");

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private void validarAcceso(Entrevista entrevista, Usuario solicitante) {
        boolean esAdmin = solicitante.getRol() == Role.ADMIN;
        boolean esTutorAsignado = solicitante.getRol() == Role.TUTOR
                && entrevista.getTutorId() != null
                && entrevista.getTutorId().equals(solicitante.getId().toString());

        if (!esAdmin && !esTutorAsignado) {
            throw new UnauthorizedException(
                    "No tienes permiso para ver esta entrevista");
        }
    }

    private ResultadoResponseDto toResponse(Entrevista entrevista) {
        return ResultadoResponseDto.builder()
                .entrevistaId(entrevista.getId())
                .alumnoId(entrevista.getAlumnoId() != null ? Long.valueOf(entrevista.getAlumnoId()) : null)
                .alumnoNombre(entrevista.getAlumnoNombre())
                .alumnoApellido(entrevista.getAlumnoApellido())
                .tutorNombre(entrevista.getTutorNombre())
                .puntajeTotal(entrevista.getPuntajeTotal())
                .nivelRiesgo(entrevista.getNivelRiesgo() != null ? entrevista.getNivelRiesgo().name() : null)
                .recomendacion(entrevista.getRecomendacion())
                .observaciones(entrevista.getObservaciones())
                .fecha(entrevista.getFecha())
                .build();
    }
}
