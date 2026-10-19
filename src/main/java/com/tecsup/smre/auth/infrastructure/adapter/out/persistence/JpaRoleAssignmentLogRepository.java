package com.tecsup.smre.auth.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleAssignmentLogRepository extends JpaRepository<RoleAssignmentLogEntity, Long> {
}
