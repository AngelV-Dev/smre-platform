package com.tecsup.smre.assignment.infrastructure.adapter.out;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.domain.port.out.AssignmentRepositoryPort;
import com.tecsup.smre.assignment.infrastructure.persistence.entity.AsignacionEntity; // 👈 IMPORTANTE
import com.tecsup.smre.assignment.infrastructure.persistence.mapper.AsignacionMapper; // 👈 IMPORTANTE
import com.tecsup.smre.assignment.infrastructure.persistence.repository.AsignacionJpaRepository; // 👈 IMPORTANTE
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AsignacionRepositoryAdapter implements AssignmentRepositoryPort {

    private final AsignacionJpaRepository jpaRepository;

    public AsignacionRepositoryAdapter(AsignacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Assignment save(Assignment assignment) {
        AsignacionEntity entity = AsignacionMapper.toEntity(assignment);
        AsignacionEntity savedEntity = jpaRepository.save(entity);
        return AsignacionMapper.toModel(savedEntity);
    }

    @Override
    public List<Assignment> findByPeriodo(String periodo) {
        return jpaRepository.findByPeriodo(periodo).stream()
                .map(AsignacionMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Assignment> findAll() {
        return jpaRepository.findAll().stream()
                .map(AsignacionMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Assignment> findById(Long id) {
        return jpaRepository.findById(id)
                .map(AsignacionMapper::toModel);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}