package com.tecsup.smre.user.domain.model;

import com.tecsup.smre.auth.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    private Role rol;
    private boolean activo;

    @Builder.Default
    private List<String> carreras = new ArrayList<>();
    private boolean carrerasConfiguradas;
}