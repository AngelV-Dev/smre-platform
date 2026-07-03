package com.tecsup.smre.interview.application.dto;

import com.tecsup.smre.interview.domain.model.Severidad;
import java.util.List;

public class GuardarEntrevistaDto {
    private Long alumnoId;
    private List<Severidad> respuestas;
    private String observaciones;

    public Long getAlumnoId() { return alumnoId; }
    public void setAlumnoId(Long alumnoId) { this.alumnoId = alumnoId; }
    
    public List<Severidad> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Severidad> respuestas) { this.respuestas = respuestas; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    private Integer edad;
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
}
