package com.tecsup.smre.auth.infrastructure.config;

import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.RoleAssignmentLogRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.auth.domain.service.RoleAssignmentService;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.RoleAssignmentLogRepositoryAdapter;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.JpaRoleAssignmentLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleAssignmentConfig {

    @Bean
    public RoleAssignmentLogRepositoryPort roleAssignmentLogRepositoryPort(
            JpaRoleAssignmentLogRepository jpaRoleAssignmentLogRepository) {
        return new RoleAssignmentLogRepositoryAdapter(jpaRoleAssignmentLogRepository);
    }

    @Bean
    public RoleAssignmentService roleAssignmentService(
            UsuarioRepositoryPort usuarioRepositoryPort,
            EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort,
            RoleAssignmentLogRepositoryPort roleAssignmentLogRepositoryPort) {
        return new RoleAssignmentService(
                usuarioRepositoryPort,
                emailMatriculadoRepositoryPort,
                roleAssignmentLogRepositoryPort);
    }
}
