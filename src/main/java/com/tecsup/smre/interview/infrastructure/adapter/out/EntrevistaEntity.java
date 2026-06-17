package com.tecsup.smre.interview.infrastructure.adapter.out;

import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.UsuarioEntity;
import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import com.tecsup.smre.student.infrastructure.adapter.out.persistence.AlumnoEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrevistas")
public class EntrevistaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alumno_id", nullable = false)
    private AlumnoEntity alumno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id", nullable = false)
    private UsuarioEntity tutor;

    @Column(nullable = false)
    private int puntajeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRiesgo nivelRiesgo;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AlumnoEntity getAlumno() { return alumno; }
    public void setAlumno(AlumnoEntity alumno) { this.alumno = alumno; }

    public UsuarioEntity getTutor() { return tutor; }
    public void setTutor(UsuarioEntity tutor) { this.tutor = tutor; }

    public int getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(int puntajeTotal) { this.puntajeTotal = puntajeTotal; }

    public NivelRiesgo getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(NivelRiesgo nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
