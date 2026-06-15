package com.tecsup.smre.assignment.infrastructure.persistence.repository;

import com.tecsup.smre.assignment.infrastructure.persistence.entity.AsignacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionJpaRepository extends JpaRepository<AsignacionEntity, Long> {
    List<AsignacionEntity> findByPeriodo(String periodo);
}