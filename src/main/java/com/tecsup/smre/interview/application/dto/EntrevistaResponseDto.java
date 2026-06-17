package com.tecsup.smre.interview.application.dto;

import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import java.time.LocalDateTime;

public class EntrevistaResponseDto {
    private Long id;
    private Alumno alumno;
    private int puntaje;
    private NivelRiesgo nivelRiesgo;
    private String recomendacion;
    private LocalDateTime fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public NivelRiesgo getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
