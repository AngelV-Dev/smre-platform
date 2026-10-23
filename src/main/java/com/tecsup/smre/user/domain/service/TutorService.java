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

import com.tecsup.smre.auth.domain.service.RoleAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class TutorService implements TutorUseCase {

    private final TutorRepositoryPort tutorRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RoleAssignmentService roleAssignmentService;

    public TutorService(TutorRepositoryPort tutorRepositoryPort,
                        PasswordEncoderPort passwordEncoderPort,
                        UsuarioRepositoryPort usuarioRepositoryPort,
                        RoleAssignmentService roleAssignmentService) {
        this.tutorRepositoryPort = tutorRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.roleAssignmentService = roleAssignmentService;
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

        if (request.getCarreras() != null) {
            tutor.setCarreras(normalizarCarreras(request.getCarreras()));
            tutor.setCarrerasConfiguradas(true);
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
    @Transactional
    public String cambiarRol(Long id, Role rol, String adminEmail) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));

        if (adminEmail != null && adminEmail.equalsIgnoreCase(tutor.getEmail())) {
            throw new BadRequestException("No puedes cambiar tu propio rol.");
        }

        Usuario usuario = usuarioRepositoryPort.findByEmail(tutor.getEmail())
                .orElse(null);

        Role rolActualUsuarios = usuario != null ? usuario.getRol() : tutor.getRol();

        if (usuario != null && usuario.getRol() != tutor.getRol()) {
            log.warn("Discrepancia detectada para {}: usuarios.rol={} vs tutores.rol={}",
                    tutor.getEmail(), usuario.getRol(), tutor.getRol());
        }

        if (rolActualUsuarios == rol) {
            return "El usuario ya tiene asignado el rol " + rol.name() + ". No se realizaron cambios.";
        }

        tutor.setRol(rol);
        tutorRepositoryPort.save(tutor);

        if (usuario != null) {
            usuario.setRol(rol);
            usuarioRepositoryPort.save(usuario);
        }

        roleAssignmentService.registrarLog(tutor.getEmail(), rolActualUsuarios, rol, adminEmail);

        return "Rol actualizado correctamente a " + rol.name();
    }

    @Override
    public void eliminar(Long id) {
        Tutor tutor = tutorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor no encontrado con id: " + id));
        
        usuarioRepositoryPort.findByEmail(tutor.getEmail())
                .ifPresent(usuario -> {
                    usuarioRepositoryPort.delete(usuario);
                });
                
        tutorRepositoryPort.deleteById(id);
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
                .carreras(tutor.getCarreras() == null ? List.of() : List.copyOf(tutor.getCarreras()))
                .carrerasConfiguradas(tutor.isCarrerasConfiguradas())
                .build();
    }

    private List<String> normalizarCarreras(List<String> carreras) {
        Map<String, String> carrerasUnicas = new LinkedHashMap<>();
        for (String carrera : carreras) {
            if (carrera == null || carrera.isBlank()) {
                continue;
            }
            String nombre = carrera.trim();
            carrerasUnicas.putIfAbsent(nombre.toLowerCase(Locale.ROOT), nombre);
        }
        return new ArrayList<>(carrerasUnicas.values());
    }
}