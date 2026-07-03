package com.tecsup.smre.csv.infrastructure.config;

import com.tecsup.smre.csv.domain.service.CsvService;
import com.tecsup.smre.student.domain.port.out.StudentRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvConfig {

    @Bean
    public CsvService csvService(StudentRepositoryPort studentRepositoryPort) {
        return new CsvService(studentRepositoryPort);
    }
}
