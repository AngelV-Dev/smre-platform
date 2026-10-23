package com.tecsup.smre.interview.infrastructure.config;

import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.auth.infrastructure.adapter.out.persistence.JpaUsuarioRepository;
import com.tecsup.smre.interview.application.port.in.GetResumenEntrevistasAdminUseCase;
import com.tecsup.smre.interview.application.service.GetResumenEntrevistasAdminService;
import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminEntrevistasResumenConfig {

    @Bean
    public GetResumenEntrevistasAdminUseCase getResumenEntrevistasAdminUseCase(
            StudentRepositoryPort studentRepositoryPort,
            EntrevistaJpaRepository entrevistaJpaRepository,
            AssignmentRepositoryPort assignmentRepositoryPort,
            TutorRepositoryPort tutorRepositoryPort,
            JpaUsuarioRepository jpaUsuarioRepository) {

        return new GetResumenEntrevistasAdminService(
                studentRepositoryPort,
                entrevistaJpaRepository,
                assignmentRepositoryPort,
                tutorRepositoryPort,
                jpaUsuarioRepository);
    }
}
