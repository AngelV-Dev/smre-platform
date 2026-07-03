package com.tecsup.smre.user.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.user.application.dto.request.EditarTutorRequest;
import com.tecsup.smre.user.application.dto.request.TutorRequest;
import com.tecsup.smre.user.application.dto.response.TutorResponse;
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.in.TutorUseCase;
import com.tecsup.smre.user.domain.port.out.PasswordEncoderPort;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;

import java.util.List;

public class TutorService implements TutorUseCase {

    private final TutorRepositoryPort tutorRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public TutorService(TutorRepositoryPort tutorRepositoryPort,
                        PasswordEncoderPort passwordEncoderPort,
                        UsuarioRepositoryPort usuarioRepositoryPort) {
        this.tutorRepositoryPort = tutorRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public TutorResponse crear(TutorRequest request) {
        if (tutorRepositoryPort.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un tutor con el correo: " + request.getEmail());
        }

        String passwordEncriptado = passwordEncoderPort.encode(request.getPassword());
        Role rol = request.getRol() != null ? request.getRol() : Role.TUTOR;

        usuarioRepositoryPort.save(Usuario.builder()
                .nombre(request.getNombre() + " " + request.getApellido())
                .email(request.getEmail())
                .password(passwordEncriptado)
                .rol(rol)
                .activo(true)
                .build());

        Tutor tutor = Tutor.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncriptado)
                .telefono(request.getTelefono())
                .rol(rol)
                .activo(true)
                .build();

        return toResponse(tutorRepositoryPort.save(tutor));
    }

    @Override
    public List<TutorResponse> listar() {
        return tutorRepositoryPort.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TutorResponse obtener(Long id) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));
        return toResponse(tutor);
    }

    @Override
    public TutorResponse actualizar(Long id, EditarTutorRequest request) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));

        tutor.setNombre(request.getNombre());
        tutor.setApellido(request.getApellido());
        tutor.setTelefono(request.getTelefono());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            tutor.setPassword(passwordEncoderPort.encode(request.getPassword()));
        }

        return toResponse(tutorRepositoryPort.save(tutor));
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));
        tutor.setActivo(activo);
        tutorRepositoryPort.save(tutor);

        // También actualizar en tabla usuarios para bloquear el login
        usuarioRepositoryPort.findByEmail(tutor.getEmail())
                .ifPresent(usuario -> {
                    usuario.setActivo(activo);
                    usuarioRepositoryPort.save(usuario);
                });
    }

    @Override
    public void cambiarRol(Long id, Role rol) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));
        tutor.setRol(rol);
        tutorRepositoryPort.save(tutor);

        usuarioRepositoryPort.findByEmail(tutor.getEmail())
                .ifPresent(usuario -> {
                    usuario.setRol(rol);
                    usuarioRepositoryPort.save(usuario);
                });
    }

    private TutorResponse toResponse(Tutor tutor) {
        return TutorResponse.builder()
                .id(tutor.getId())
                .nombre(tutor.getNombre())
                .apellido(tutor.getApellido())
                .email(tutor.getEmail())
                .telefono(tutor.getTelefono())
                .rol(tutor.getRol().name())
                .activo(tutor.isActivo())
                .build();
    }
}