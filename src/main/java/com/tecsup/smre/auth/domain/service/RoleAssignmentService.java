package com.tecsup.smre.auth.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.auth.domain.port.in.AsignarRolUseCase;
import com.tecsup.smre.auth.domain.port.in.GetRoleAssignmentLogUseCase;
import com.tecsup.smre.auth.domain.port.in.QuitarRolUseCase;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.RoleAssignmentLogRepositoryPort;
import com.tecsup.smre.auth.domain.port.out.UsuarioRepositoryPort;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.exception.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.List;

public class RoleAssignmentService implements AsignarRolUseCase, QuitarRolUseCase, GetRoleAssignmentLogUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort;
    private final RoleAssignmentLogRepositoryPort logRepositoryPort;
    private final String superAdminEmail;

    public RoleAssignmentService(UsuarioRepositoryPort usuarioRepositoryPort,
                                  EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort,
                                  RoleAssignmentLogRepositoryPort logRepositoryPort,
                                  String superAdminEmail) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.emailMatriculadoRepositoryPort = emailMatriculadoRepositoryPort;
        this.logRepositoryPort = logRepositoryPort;
        this.superAdminEmail = superAdminEmail != null ? superAdminEmail.trim().toLowerCase() : "";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Asignar rol
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void asignarRol(String emailSolicitante, String emailDestino, Role rol) {
        validarSuperAdmin(emailSolicitante);
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
    // Quitar rol
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void quitarRol(String emailSolicitante, String emailDestino, Role rol) {
        validarSuperAdmin(emailSolicitante);
        validarRolPermitido(rol);

        String destino = normalizar(emailDestino);
        validarMatriculado(destino);

        Usuario usuario = buscarUsuario(destino);
        Role rolAnterior = usuario.getRol();
        Role rolNuevo = calcularRolAlQuitar(rolAnterior, rol);

        usuario.setRol(rolNuevo);
        usuarioRepositoryPort.save(usuario);

        guardarLog(destino, rolAnterior, rolNuevo, emailSolicitante);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Consultar historial
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<RoleAssignmentLog> getLogs(String emailSolicitante) {
        validarSuperAdmin(emailSolicitante);
        return logRepositoryPort.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private void validarSuperAdmin(String email) {
        if (email == null || !normalizar(email).equals(superAdminEmail)) {
            throw new UnauthorizedException(
                    "Solo el superadministrador puede realizar operaciones de asignación de roles.");
        }
    }

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
     * Calcula el nuevo rol cuando se ASIGNA un rol.
     * - ADMIN → ADMIN recibe ADMIN    = no cambia (error)
     * - TUTOR → recibe ADMIN          = ADMIN_TUTOR (upgrade)
     * - ADMIN_TUTOR → recibe TUTOR    = no cambia (ya lo tiene)
     * - Cualquier → recibe igual      = no cambia
     */
    private Role calcularRolAlAsignar(Role actual, Role rolAAgregar) {
        if (actual == rolAAgregar) return actual; // sin cambio → caller lanza error
        if (actual == Role.ADMIN_TUTOR) return actual; // ya tiene ambos → sin cambio
        // actual es ADMIN y asignamos TUTOR, o actual es TUTOR y asignamos ADMIN
        return Role.ADMIN_TUTOR;
    }

    /**
     * Calcula el nuevo rol cuando se QUITA un rol.
     * - ADMIN_TUTOR quita ADMIN  → queda TUTOR
     * - ADMIN_TUTOR quita TUTOR  → queda ADMIN
     * - ADMIN quita ADMIN        → error (no puede quedar sin rol)
     * - TUTOR quita TUTOR        → error (no puede quedar sin rol)
     * - ADMIN quita TUTOR        → error (no tiene ese rol)
     * - TUTOR quita ADMIN        → error (no tiene ese rol)
     */
    private Role calcularRolAlQuitar(Role actual, Role rolAQuitar) {
        if (actual == Role.ADMIN_TUTOR) {
            return rolAQuitar == Role.ADMIN ? Role.TUTOR : Role.ADMIN;
        }
        if (actual == rolAQuitar) {
            throw new BadRequestException(
                    "No se puede quitar el único rol del usuario. El usuario quedaría sin rol.");
        }
        // El usuario no tiene el rol que se quiere quitar
        throw new BadRequestException(
                "El usuario no tiene el rol " + rolAQuitar.name() + " asignado.");
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
