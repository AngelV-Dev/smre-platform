package com.tecsup.smre.interview.infrastructure.adapter.out;

import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "entrevistas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrevistaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private String alumnoId;

    @Column(name = "alumno_nombre", nullable = false)
    private String alumnoNombre;

    @Column(name = "alumno_apellido", nullable = false)
    private String alumnoApellido;

    @Column(name = "tutor_id", nullable = false)
    private String tutorId;

    @Column(name = "tutor_nombre", nullable = false)
    private String tutorNombre;

    @Column(nullable = false)
    private int puntajeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRiesgo nivelRiesgo;

    @Column(nullable = false, length = 500)
    private String recomendacion;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
