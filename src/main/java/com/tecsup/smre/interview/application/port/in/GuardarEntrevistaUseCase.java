package com.tecsup.smre.interview.application.port.in;

import com.tecsup.smre.interview.application.dto.GuardarEntrevistaDto;
import com.tecsup.smre.interview.application.dto.EntrevistaResponseDto;

public interface GuardarEntrevistaUseCase {
    EntrevistaResponseDto guardarEntrevista(GuardarEntrevistaDto dto);
}
