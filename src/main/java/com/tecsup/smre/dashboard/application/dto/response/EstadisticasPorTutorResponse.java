package com.tecsup.smre.dashboard.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasPorTutorResponse {
    private Long tutorId;
    private String tutorNombre;
    private Long cantidadEntrevistas;
}