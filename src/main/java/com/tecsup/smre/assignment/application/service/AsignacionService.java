package com.tecsup.smre.assignment.application.service;

import com.tecsup.smre.assignment.application.dto.request.AssignmentRequest;
import com.tecsup.smre.assignment.application.dto.response.AssignmentResponse;
import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.in.CrearAsignacionUseCase;
import com.tecsup.smre.assignment.domain.port.in.EliminarAsignacionUseCase;
import com.tecsup.smre.assignment.domain.port.in.ListarAsignacionesUseCase;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // 👈 Le decimos a Spring que esta clase es un bean gestionado
public class AsignacionService implements CrearAsignacionUseCase, ListarAsignacionesUseCase, EliminarAsignacionUseCase {

    private final AssignmentRepositoryPort repositoryPort;

    // Inyección por constructor (Buena práctica)
    public AsignacionService(AssignmentRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public AssignmentResponse crear(AssignmentRequest request) {
        Assignment assignment = Assignment.builder()
                .tutorId(request.getTutorId())
                .periodo(request.getPeriodo())
                .especialidad(request.getEspecialidad())
                .ciclo(request.getCiclo())
                .grupo(request.getGrupo()) // 👈 Incluimos grupo
                .secciones(request.getSecciones())
                .build();

        Assignment saved = repositoryPort.save(assignment);
        return mapToResponse(saved);
    }

    @Override
    public List<AssignmentResponse> listarPorPeriodo(String periodo) {
        return repositoryPort.findByPeriodo(periodo).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentResponse> listarTodo() {
        return repositoryPort.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        repositoryPort.deleteById(id);
    }

    // Método utilitario para mapear del Dominio al DTO de respuesta
    private AssignmentResponse mapToResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .tutorId(assignment.getTutorId())
                .tutorNombre(assignment.getTutorNombre())
                .periodo(assignment.getPeriodo())
                .especialidad(assignment.getEspecialidad())
                .ciclo(assignment.getCiclo())
                .grupo(assignment.getGrupo()) // 👈 Incluimos grupo
                .secciones(assignment.getSecciones())
                .build();
    }
}