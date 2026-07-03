package com.tecsup.smre.result.infrastructure.config;

import com.tecsup.smre.interview.infrastructure.adapter.out.EntrevistaJpaRepository;
import com.tecsup.smre.result.domain.port.out.ResultadoRepositoryPort;
import com.tecsup.smre.result.domain.service.ResultadoService;
import com.tecsup.smre.result.infrastructure.adapter.out.persistence.ResultadoRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResultConfig {

    @Bean
    public ResultadoRepositoryPort resultadoRepositoryPort(EntrevistaJpaRepository jpaEntrevistaRepository) {
        return new ResultadoRepositoryAdapter(jpaEntrevistaRepository);
    }

    @Bean
    public ResultadoService resultadoService(ResultadoRepositoryPort resultadoRepositoryPort) {
        return new ResultadoService(resultadoRepositoryPort);
    }
}
