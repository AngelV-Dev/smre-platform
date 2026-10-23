package com.tecsup.smre.interview.application.port.in;

import com.tecsup.smre.interview.application.dto.HistorialGrupoDto;

import java.util.List;

public interface GetHistorialGruposTutorUseCase {
    List<HistorialGrupoDto> getHistorialGruposTutor(String ciclo, String periodo);
}
