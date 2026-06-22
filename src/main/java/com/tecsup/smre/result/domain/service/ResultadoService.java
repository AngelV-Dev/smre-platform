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
        // Pendiente: generación real de PDF (prioridad Media, "si alcanza tiempo").
        getResultado(entrevistaId, solicitante); // reutiliza la validación de acceso
        throw new UnsupportedOperationException("Exportar a PDF aún no está implementado");
    }

    private void validarAcceso(Entrevista entrevista, Usuario solicitante) {
        boolean esAdmin = solicitante.getRol() == Role.ADMIN;
        boolean esTutorAsignado = solicitante.getRol() == Role.TUTOR
                && entrevista.getTutorId() != null
                && entrevista.getTutorId().equals(solicitante.getId());

        if (!esAdmin && !esTutorAsignado) {
            throw new UnauthorizedException(
                    "No tienes permiso para ver esta entrevista");
        }
    }

    private ResultadoResponseDto toResponse(Entrevista entrevista) {
        return ResultadoResponseDto.builder()
                .entrevistaId(entrevista.getId())
                .alumnoId(entrevista.getAlumnoId())
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
