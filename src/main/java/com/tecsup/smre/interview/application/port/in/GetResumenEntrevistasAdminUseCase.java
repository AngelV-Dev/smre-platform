package com.tecsup.smre.interview.application.port.in;

import com.tecsup.smre.interview.application.dto.response.ResumenEntrevistasResponseDto;

public interface GetResumenEntrevistasAdminUseCase {
    /**
     * Devuelve la lista de alumnos agrupada por tutor o por carrera,
     * con contadores de total y evaluados, para la vista del admin.
     *
     * @param agruparPor "tutor" o "carrera" (null ó vacío → "tutor")
     * @throws com.tecsup.smre.exception.BadRequestException si el valor es distinto de los dos permitidos
     */
    ResumenEntrevistasResponseDto execute(String agruparPor);
}
