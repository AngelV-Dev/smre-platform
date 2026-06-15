package com.tecsup.smre.dashboard.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoAltoRiesgoResponse {
    private Long alumnoId;
    private String alumnoNombre;
    private String nivelRiesgo; // Siempre será "ALTO" pero es bueno tenerlo
    private String tutorAsignado;
}