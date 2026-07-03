package com.tecsup.smre.interview.application.dto;

import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrevistaResponseDto {
    private Long id;
    private String alumnoId;
    private String alumnoNombre;
    private String alumnoApellido;
    private String tutorNombre;
    private int puntaje;
    private NivelRiesgo nivelRiesgo;
    private String recomendacion;
    private LocalDateTime fecha;
}
