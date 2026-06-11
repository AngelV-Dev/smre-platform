package com.tecsup.smre.auth.infrastructure.adapter.out.persistence;

import com.tecsup.smre.auth.domain.model.Usuario;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .rol(entity.getRol())
                .activo(entity.isActivo())
                .build();
    }

    public static UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .rol(domain.getRol())
                .activo(domain.isActivo())
                .build();
    }
}