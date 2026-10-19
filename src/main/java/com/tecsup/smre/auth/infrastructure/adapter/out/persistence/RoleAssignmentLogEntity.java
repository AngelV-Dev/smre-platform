package com.tecsup.smre.auth.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_assignment_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_asignado", nullable = false)
    private String emailAsignado;

    @Column(name = "rol_anterior", nullable = false)
    private String rolAnterior;

    @Column(name = "rol_nuevo", nullable = false)
    private String rolNuevo;

    @Column(name = "asignado_por", nullable = false)
    private String asignadoPor;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
