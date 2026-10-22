package com.tecsup.smre.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentLogResponse {
    private Long id;
    private String emailAsignado;
    private String rolAnterior;
    private String rolNuevo;
    private String asignadoPor;
    private LocalDateTime fecha;
}
