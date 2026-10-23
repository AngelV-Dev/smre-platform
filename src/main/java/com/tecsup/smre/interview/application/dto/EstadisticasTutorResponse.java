package com.tecsup.smre.interview.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasTutorResponse {
    // Totales globales del tutor
    private int totalAlumnos;
    private int evaluadosCount;
    private int pendientesCount;
    
    // Conteo por nivel de riesgo (basado estrictamente en la entrevista más reciente por alumno)
    private int altoRiesgoCount;
    private int medioRiesgoCount;
    private int bajoRiesgoCount;

    // Desglose por cada grupo asignado al tutor
    private List<GrupoEstadisticaDto> grupos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrupoEstadisticaDto {
        private Long asignacionId;
        private String periodo;
        private String ciclo;
        private String especialidad;
        private String grupo;
        private String secciones;
        
        private int totalAlumnos;
        private int evaluadosCount;
        private int pendientesCount;
        
        private int altoRiesgoCount;
        private int medioRiesgoCount;
        private int bajoRiesgoCount;
        
        private String nota;
    }
}
