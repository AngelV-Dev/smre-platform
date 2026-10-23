package com.tecsup.smre.interview.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenEntrevistasResponseDto {
    /** Parámetro con el que se agrupó: "tutor" o "carrera". */
    private String agruparPor;
    /** Grupos resultantes. */
    private List<GrupoEntrevistasDto> grupos;
}
