package com.tecsup.smre.interview.application.dto;

import com.tecsup.smre.interview.domain.model.NivelRiesgo;

public class AlumnoRiesgoDto {
    private Long id;
    private String nombre;
    private NivelRiesgo nivelRiesgo;

    public AlumnoRiesgoDto(Long id, String nombre, NivelRiesgo nivelRiesgo) {
        this.id = id;
        this.nombre = nombre;
        this.nivelRiesgo = nivelRiesgo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public NivelRiesgo getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }
}
