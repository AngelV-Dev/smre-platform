package com.tecsup.smre.result.domain.port.in;

import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;

public interface GetResultadoEntrevistaUseCase {
    ResultadoResponseDto getResultado(Long entrevistaId, Usuario solicitante);
}
