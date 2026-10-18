package com.tecsup.smre.auth.infrastructure.adapter.out.persistence;

import com.tecsup.smre.auth.domain.model.EmailMatriculado;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class EmailMatriculadoRepositoryAdapter implements EmailMatriculadoRepositoryPort {

    private final JpaEmailMatriculadoRepository jpaEmailMatriculadoRepository;

    public EmailMatriculadoRepositoryAdapter(JpaEmailMatriculadoRepository jpaEmailMatriculadoRepository) {
        this.jpaEmailMatriculadoRepository = jpaEmailMatriculadoRepository;
    }

    @Override
    public List<EmailMatriculado> findAll() {
        return jpaEmailMatriculadoRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<EmailMatriculado> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return jpaEmailMatriculadoRepository.findByEmailIgnoreCase(email.trim())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return jpaEmailMatriculadoRepository.existsByEmailIgnoreCase(email.trim());
    }

    @Override
    public EmailMatriculado save(EmailMatriculado emailMatriculado) {
        EmailMatriculadoEntity entity = EmailMatriculadoEntity.builder()
                .id(emailMatriculado.getId())
                .email(emailMatriculado.getEmail().trim().toLowerCase())
                .fechaRegistro(emailMatriculado.getFechaRegistro() != null
                        ? emailMatriculado.getFechaRegistro()
                        : LocalDateTime.now())
                .build();
        EmailMatriculadoEntity saved = jpaEmailMatriculadoRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaEmailMatriculadoRepository.deleteById(id);
    }

    private EmailMatriculado toDomain(EmailMatriculadoEntity entity) {
        if (entity == null) return null;
        return EmailMatriculado.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}
