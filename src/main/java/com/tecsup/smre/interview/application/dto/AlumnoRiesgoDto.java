package com.tecsup.smre.interview.application.dto;

import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoRiesgoDto {
    private Long id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String email;
    private String carrera;
    private String semestre;
    private String grupo;
    private NivelRiesgo nivelRiesgo;
    private Integer edad;

    public AlumnoRiesgoDto(Long id, String nombre, NivelRiesgo nivelRiesgo) {
        this.id = id;
        this.nombre = nombre;
        this.nivelRiesgo = nivelRiesgo;
    }

    public AlumnoRiesgoDto(Long id, String nombre, NivelRiesgo nivelRiesgo, Integer edad) {
        this.id = id;
        this.nombre = nombre;
        this.nivelRiesgo = nivelRiesgo;
        this.edad = edad;
    }
}
