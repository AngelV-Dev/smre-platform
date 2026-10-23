package com.tecsup.smre.user.infrastructure.adapter.out.persistence;

import com.tecsup.smre.auth.domain.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role rol;

    @Column(nullable = false)
    private boolean activo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tutor_carreras", joinColumns = @JoinColumn(name = "tutor_id"))
    @Column(name = "carrera", nullable = false)
    @Builder.Default
    private List<String> carreras = new ArrayList<>();

    @Column(nullable = false)
    private boolean carrerasConfiguradas;
}