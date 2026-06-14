package com.tecsup.smre.assignment.infrastructure.persistence.mapper;

import com.tecsup.smre.assignment.domain.model.Assignment;
import com.tecsup.smre.assignment.infrastructure.persistence.entity.AsignacionEntity;

public class AsignacionMapper {

    public static AsignacionEntity toEntity(Assignment model) {
        return AsignacionEntity.builder()
                .id(model.getId())
                .tutorId(model.getTutorId())
                .tutorNombre(model.getTutorNombre())
                .periodo(model.getPeriodo())
                .especialidad(model.getEspecialidad())
                .ciclo(model.getCiclo())
                .grupo(model.getGrupo())
                .secciones(model.getSecciones())
                .build();
    }

    public static Assignment toModel(AsignacionEntity entity) {
        return Assignment.builder()
                .id(entity.getId())
                .tutorId(entity.getTutorId())
                .tutorNombre(entity.getTutorNombre())
                .periodo(entity.getPeriodo())
                .especialidad(entity.getEspecialidad())
                .ciclo(entity.getCiclo())
                .grupo(entity.getGrupo())
                .secciones(entity.getSecciones())
                .build();
    }
}