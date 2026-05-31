package com.tecsup.smre.student.infrastructure.adapter.out.persistence;

import com.tecsup.smre.student.domain.model.Alumno;

public class AlumnoMapper {

    public static Alumno toDomain(AlumnoEntity entity) {
        if (entity == null) return null;
        return Alumno.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .carrera(entity.getCarrera())
                .semestre(entity.getSemestre())
                .grupo(entity.getGrupo())
                .build();
    }

    public static AlumnoEntity toEntity(Alumno domain) {
        if (domain == null) return null;
        return AlumnoEntity.builder()
                .id(domain.getId())
                .codigo(domain.getCodigo())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .email(domain.getEmail())
                .carrera(domain.getCarrera())
                .semestre(domain.getSemestre())
                .grupo(domain.getGrupo())
                .build();
    }
}