package com.tecsup.smre.interview.domain.model;

import com.tecsup.smre.student.domain.model.Alumno;
import com.tecsup.smre.auth.domain.model.Usuario;
import java.time.LocalDateTime;

public class Entrevista {

    private Long id;
    private Alumno alumno;
    private Usuario tutor;
    private int puntajeTotal;
    private NivelRiesgo nivelRiesgo;
    private String observaciones;
    private LocalDateTime fecha;

    public Entrevista() {
    }

    public Entrevista(Long id, Alumno alumno, Usuario tutor, int puntajeTotal, NivelRiesgo nivelRiesgo, String observaciones, LocalDateTime fecha) {
        this.id = id;
        this.alumno = alumno;
        this.tutor = tutor;
        this.puntajeTotal = puntajeTotal;
        this.nivelRiesgo = nivelRiesgo;
        this.observaciones = observaciones;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Usuario getTutor() {
        return tutor;
    }

    public void setTutor(Usuario tutor) {
        this.tutor = tutor;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(int puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public NivelRiesgo getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
