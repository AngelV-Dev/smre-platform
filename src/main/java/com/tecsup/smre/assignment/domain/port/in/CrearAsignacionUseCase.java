package com.tecsup.smre.assignment.domain.port.in;

import com.tecsup.smre.assignment.application.dto.request.AssignmentRequest;
import com.tecsup.smre.assignment.application.dto.response.AssignmentResponse;

public interface CrearAsignacionUseCase {
    AssignmentResponse crear(AssignmentRequest request);
}