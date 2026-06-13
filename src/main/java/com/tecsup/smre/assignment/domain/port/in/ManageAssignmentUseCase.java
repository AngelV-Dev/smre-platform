package com.tecsup.smre.assignment.domain.port.in;

import com.tecsup.smre.assignment.application.dto.request.AssignmentRequest;
import com.tecsup.smre.assignment.application.dto.response.AssignmentResponse;

import java.util.List;

public interface ManageAssignmentUseCase {
    AssignmentResponse crear(AssignmentRequest request);
    List<AssignmentResponse> listarPorPeriodo(String periodo);
    List<AssignmentResponse> listarTodo();
    void eliminar(Long id);
}
