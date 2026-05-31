package com.tecsup.smre.student.infrastructure.config;

import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import com.tecsup.smre.student.domain.service.StudentService;
import com.tecsup.smre.student.infrastructure.adapter.out.persistence.JpaAlumnoRepository;
import com.tecsup.smre.student.infrastructure.adapter.out.persistence.StudentRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentConfig {

    @Bean
    public StudentRepositoryPort studentRepositoryPort(JpaAlumnoRepository jpaAlumnoRepository) {
        return new StudentRepositoryAdapter(jpaAlumnoRepository);
    }

    @Bean
    public StudentService studentService(StudentRepositoryPort studentRepositoryPort) {
        return new StudentService(studentRepositoryPort);
    }
}