package com.tecsup.smre.interview.infrastructure.adapter.out.persistence;

import com.tecsup.smre.interview.domain.model.NivelRiesgo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ⚠️ STUB TEMPORAL — BORRAR cuando Angelo Ricasca suba la versión real a develop (orden 4°).
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

    @Column(nullable = false)
    private Long alumnoId;

    @Column(nullable = false)
    private String alumnoNombre;

    @Column(nullable = false)
    private String alumnoApellido;

    @Column(nullable = false)
    private Long tutorId;

    @Column(nullable = false)
    private String tutorNombre;

    @Column(nullable = false)
    private Integer puntajeTotal;

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
