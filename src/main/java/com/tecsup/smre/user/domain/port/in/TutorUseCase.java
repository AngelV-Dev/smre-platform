package com.tecsup.smre.user.domain.port.in;

import com.tecsup.smre.user.application.dto.request.EditarTutorRequest;
import com.tecsup.smre.user.application.dto.request.TutorRequest;
import com.tecsup.smre.user.application.dto.response.TutorResponse;

import java.util.List;

public interface TutorUseCase {
    TutorResponse crear(TutorRequest request);
    List<TutorResponse> listar();
    TutorResponse obtener(Long id);
    TutorResponse actualizar(Long id, EditarTutorRequest request);
    void cambiarEstado(Long id, boolean activo);
    void cambiarRol(Long id, com.tecsup.smre.auth.domain.model.Role rol);
}