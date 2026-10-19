package com.tecsup.smre.auth.infrastructure.adapter.out.persistence;

import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;
import com.tecsup.smre.auth.domain.port.out.RoleAssignmentLogRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleAssignmentLogRepositoryAdapter implements RoleAssignmentLogRepositoryPort {

    private final JpaRoleAssignmentLogRepository jpaRepository;

    public RoleAssignmentLogRepositoryAdapter(JpaRoleAssignmentLogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RoleAssignmentLog save(RoleAssignmentLog log) {
        RoleAssignmentLogEntity entity = toEntity(log);
        RoleAssignmentLogEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<RoleAssignmentLog> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private RoleAssignmentLogEntity toEntity(RoleAssignmentLog log) {
        return RoleAssignmentLogEntity.builder()
                .id(log.getId())
                .emailAsignado(log.getEmailAsignado())
                .rolAnterior(log.getRolAnterior())
                .rolNuevo(log.getRolNuevo())
                .asignadoPor(log.getAsignadoPor())
                .fecha(log.getFecha())
                .build();
    }

    private RoleAssignmentLog toDomain(RoleAssignmentLogEntity entity) {
        return RoleAssignmentLog.builder()
                .id(entity.getId())
                .emailAsignado(entity.getEmailAsignado())
                .rolAnterior(entity.getRolAnterior())
                .rolNuevo(entity.getRolNuevo())
                .asignadoPor(entity.getAsignadoPor())
                .fecha(entity.getFecha())
                .build();
    }
}
