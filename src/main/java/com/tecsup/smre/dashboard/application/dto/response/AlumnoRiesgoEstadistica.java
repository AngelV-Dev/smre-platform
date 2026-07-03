package com.tecsup.smre.dashboard.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoRiesgoEstadistica {
    private String carrera;
    private String semestre;
    private String grupo;
    private String nivelRiesgo;
}
