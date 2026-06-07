package com.tecsup.smre.user.infrastructure.adapter.out.persistence;

import com.tecsup.smre.user.domain.model.Tutor;

public class TutorMapper {

    public static Tutor toDomain(TutorEntity entity) {
        return Tutor.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .telefono(entity.getTelefono())
                .rol(entity.getRol())
                .activo(entity.isActivo())
                .build();
    }

    public static TutorEntity toEntity(Tutor tutor) {
        return TutorEntity.builder()
                .id(tutor.getId())
                .nombre(tutor.getNombre())
                .apellido(tutor.getApellido())
                .email(tutor.getEmail())
                .password(tutor.getPassword())
                .telefono(tutor.getTelefono())
                .rol(tutor.getRol())
                .activo(tutor.isActivo())
                .build();
    }
}