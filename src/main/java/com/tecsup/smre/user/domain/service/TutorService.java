package com.tecsup.smre.user.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.user.application.dto.request.TutorRequest;
import com.tecsup.smre.user.application.dto.response.TutorResponse;
import com.tecsup.smre.user.domain.model.Tutor;
import com.tecsup.smre.user.domain.port.in.TutorUseCase;
import com.tecsup.smre.user.domain.port.out.TutorRepositoryPort;
import com.tecsup.smre.user.domain.port.out.PasswordEncoderPort;

import java.util.List;

public class TutorService implements TutorUseCase {

    private final TutorRepositoryPort tutorRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public TutorService(TutorRepositoryPort tutorRepositoryPort,
                        PasswordEncoderPort passwordEncoderPort) {
        this.tutorRepositoryPort = tutorRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public TutorResponse crear(TutorRequest request) {
        if (tutorRepositoryPort.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un tutor con el correo: " + request.getEmail());
        }

        Tutor tutor = Tutor.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoderPort.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .rol(Role.TUTOR)
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
    public TutorResponse actualizar(Long id, TutorRequest request) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));

        tutor.setNombre(request.getNombre());
        tutor.setApellido(request.getApellido());
        tutor.setTelefono(request.getTelefono());

        return toResponse(tutorRepositoryPort.save(tutor));
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));
        tutor.setActivo(activo);
        tutorRepositoryPort.save(tutor);
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