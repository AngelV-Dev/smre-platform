package com.tecsup.smre.student.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alumnos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String carrera;

    @Column(nullable = false)
    private String semestre;  // Ej: "2026-1"

    @Column(nullable = false, length = 5)
    private String grupo;     // Ej: "A", "B", "C"

    @Column
    private Integer edad;
}