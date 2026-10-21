package com.tecsup.smre.auth.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.in.AsignarRolUseCase;
import com.tecsup.smre.auth.domain.port.in.GetRoleAssignmentLogUseCase;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.RoleAssignmentLogRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class RoleAssignmentService implements AsignarRolUseCase, GetRoleAssignmentLogUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort;
    private final RoleAssignmentLogRepositoryPort logRepositoryPort;

    public RoleAssignmentService(UsuarioRepositoryPort usuarioRepositoryPort,
                                  EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort,
                                  RoleAssignmentLogRepositoryPort logRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.emailMatriculadoRepositoryPort = emailMatriculadoRepositoryPort;
        this.logRepositoryPort = logRepositoryPort;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Asignar rol
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void asignarRol(String emailSolicitante, String emailDestino, Role rol) {
        validarRolPermitido(rol);

        String destino = normalizar(emailDestino);
        validarMatriculado(destino);

        Usuario usuario = buscarUsuario(destino);
        Role rolAnterior = usuario.getRol();
        Role rolNuevo = calcularRolAlAsignar(rolAnterior, rol);

        if (rolAnterior == rolNuevo) {
            throw new BadRequestException("El usuario ya tiene el rol " + rol.name() + " asignado.");
        }

        usuario.setRol(rolNuevo);
        usuarioRepositoryPort.save(usuario);

        guardarLog(destino, rolAnterior, rolNuevo, emailSolicitante);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Consultar historial
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<RoleAssignmentLog> getLogs(String emailSolicitante) {
        return logRepositoryPort.findAll();
    }

    public void registrarLog(String emailAsignado, Role rolAnterior, Role rolNuevo, String asignadoPor) {
        guardarLog(emailAsignado, rolAnterior, rolNuevo, asignadoPor);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private void validarRolPermitido(Role rol) {
        if (rol == Role.ADMIN_TUTOR) {
            throw new BadRequestException(
                    "No se puede asignar el rol ADMIN_TUTOR directamente. Asigna ADMIN o TUTOR por separado.");
        }
    }

    private void validarMatriculado(String email) {
        if (!emailMatriculadoRepositoryPort.existsByEmail(email)) {
            throw new BadRequestException(
                    "El correo " + email + " no está matriculado en el sistema.");
        }
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe ningún usuario registrado con el correo: " + email));
    }

    /**
     * Calcula el nuevo rol cuando se ASIGNA un rol:
     * - TUTOR + recibe ADMIN  → ADMIN_TUTOR (upgrade)
     * - ADMIN + recibe TUTOR  → ADMIN_TUTOR (upgrade)
     * - Mismo rol             → sin cambio (caller lanza error)
     * - ADMIN_TUTOR           → ya tiene ambos → sin cambio (caller lanza error)
     */
    private Role calcularRolAlAsignar(Role actual, Role rolAAgregar) {
        if (actual == rolAAgregar) return actual;     // sin cambio → caller lanza BadRequest
        if (actual == Role.ADMIN_TUTOR) return actual; // ya tiene ambos → sin cambio
        return Role.ADMIN_TUTOR;
    }

    private void guardarLog(String emailAsignado, Role rolAnterior, Role rolNuevo, String asignadoPor) {
        logRepositoryPort.save(RoleAssignmentLog.builder()
                .emailAsignado(emailAsignado)
                .rolAnterior(rolAnterior != null ? rolAnterior.name() : "SIN_ROL")
                .rolNuevo(rolNuevo.name())
                .asignadoPor(asignadoPor)
                .fecha(LocalDateTime.now())
                .build());
    }

    private String normalizar(String email) {
        return email != null ? email.trim().toLowerCase() : "";
    }
}
