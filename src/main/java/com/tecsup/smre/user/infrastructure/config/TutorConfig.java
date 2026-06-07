package com.tecsup.smre.user.infrastructure.config;

import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.user.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import com.tecsup.smre.user.domain.service.TutorService;
import com.tecsup.smre.user.infrastructure.adapter.out.persistence.JpaTutorRepository;
import com.tecsup.smre.user.infrastructure.adapter.out.persistence.PasswordEncoderAdapter;
import com.tecsup.smre.user.infrastructure.adapter.out.persistence.TutorRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class TutorConfig {

    @Bean
    public TutorRepositoryPort tutorRepositoryPort(JpaTutorRepository jpaTutorRepository) {
        return new TutorRepositoryAdapter(jpaTutorRepository);
    }

    @Bean
    public PasswordEncoderPort tutorPasswordEncoderPort(PasswordEncoder passwordEncoder) {
        return new PasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public TutorService tutorService(TutorRepositoryPort tutorRepositoryPort,
                                     PasswordEncoderPort tutorPasswordEncoderPort,
                                     UsuarioRepositoryPort usuarioRepositoryPort) {
        return new TutorService(tutorRepositoryPort, tutorPasswordEncoderPort, usuarioRepositoryPort);
    }
}