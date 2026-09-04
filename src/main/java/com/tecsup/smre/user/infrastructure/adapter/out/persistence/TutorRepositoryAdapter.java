package com.tecsup.smre.user.infrastructure.adapter.out.persistence;

import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;

import java.util.List;
import java.util.Optional;

public class TutorRepositoryAdapter implements TutorRepositoryPort {

    private final JpaTutorRepository jpaTutorRepository;

    public TutorRepositoryAdapter(JpaTutorRepository jpaTutorRepository) {
        this.jpaTutorRepository = jpaTutorRepository;
    }

    @Override
    public Tutor save(Tutor tutor) {
        TutorEntity entity = TutorMapper.toEntity(tutor);
        return TutorMapper.toDomain(jpaTutorRepository.save(entity));
    }

    @Override
    public List<Tutor> findAll() {
        return jpaTutorRepository.findAll()
                .stream()
                .map(TutorMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Tutor> findById(Long id) {
        return jpaTutorRepository.findById(id)
                .map(TutorMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaTutorRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        jpaTutorRepository.deleteById(id);
    }
}