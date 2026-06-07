package com.tecsup.smre.user.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTutorRepository extends JpaRepository<TutorEntity, Long> {
    boolean existsByEmail(String email);
    Optional<TutorEntity> findByEmail(String email);
}