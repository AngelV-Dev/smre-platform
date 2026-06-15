package com.tecsup.smre.assignment.domain.port.in;

import com.tecsup.smre.assignment.application.dto.response.AssignmentResponse;
import java.util.List;

public interface ListarAsignacionesUseCase {
    List<AssignmentResponse> listarPorPeriodo(String periodo);
    List<AssignmentResponse> listarTodo();
}