package com.tecsup.smre.result.domain.port.in;

import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;

import java.util.List;

public interface GetHistorialEntrevistasUseCase {
    List<ResultadoResponseDto> getHistorial(Long alumnoId, Usuario solicitante);
}
