package com.tecsup.smre.auth.domain.port.out;

import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;

import java.util.List;

public interface RoleAssignmentLogRepositoryPort {
    RoleAssignmentLog save(RoleAssignmentLog log);
    List<RoleAssignmentLog> findAll();
}
