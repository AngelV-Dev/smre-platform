package com.tecsup.smre.auth.domain.port.in;

import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;

import java.util.List;

public interface GetRoleAssignmentLogUseCase {
    /**
     * Retorna el historial completo de asignaciones de roles.
     * Solo accesible por el superadmin (ksalas).
     *
     * @param emailSolicitante email del usuario que solicita el historial
     */
    List<RoleAssignmentLog> getLogs(String emailSolicitante);
}
