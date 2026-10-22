package com.tecsup.smre.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentLog {
    private Long id;
    private String emailAsignado;
    private String rolAnterior;
    private String rolNuevo;
    private String asignadoPor;
    private LocalDateTime fecha;
}
