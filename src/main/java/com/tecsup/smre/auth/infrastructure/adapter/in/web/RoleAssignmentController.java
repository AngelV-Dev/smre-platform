package com.tecsup.smre.auth.infrastructure.adapter.in.web;

import com.tecsup.smre.auth.application.dto.request.AsignarRolRequest;
import com.tecsup.smre.auth.application.dto.response.RoleAssignmentLogResponse;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;
import com.tecsup.smre.auth.domain.port.in.AsignarRolUseCase;
import com.tecsup.smre.auth.domain.port.in.GetRoleAssignmentLogUseCase;
import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.exception.BadRequestException;
import com.tecsup.smre.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de roles.
 * La anotación @PreAuthorize limita el acceso a cualquier usuario con rol ADMIN (ADMIN o ADMIN_TUTOR).
 * Los tutores puros recibirán 403 Forbidden y los no autenticados 401 Unauthorized.
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleAssignmentController {

    private final AsignarRolUseCase asignarRolUseCase;
    private final GetRoleAssignmentLogUseCase getRoleAssignmentLogUseCase;

    public RoleAssignmentController(AsignarRolUseCase asignarRolUseCase,
                                     GetRoleAssignmentLogUseCase getRoleAssignmentLogUseCase) {
        this.asignarRolUseCase = asignarRolUseCase;
        this.getRoleAssignmentLogUseCase = getRoleAssignmentLogUseCase;
    }

    /**
     * POST /api/v1/ksalas/roles → asignar un rol (disponible para ADMIN y ADMIN_TUTOR)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> asignar(
            @Valid @RequestBody AsignarRolRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        Role rol = parsearRol(request.getRol());
        asignarRolUseCase.asignarRol(
                principal.getUsername(),
                request.getEmailDestino(),
                rol);

        return ResponseEntity.ok(ApiResponse.success(null,
                "Rol " + rol.name() + " asignado exitosamente a " + request.getEmailDestino()));
    }

    /**
     * GET /api/v1/ksalas/roles/log → consultar historial de asignaciones (disponible para ADMIN y ADMIN_TUTOR)
     */
    @GetMapping("/log")
    public ResponseEntity<ApiResponse<List<RoleAssignmentLogResponse>>> getLogs(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<RoleAssignmentLog> logs = getRoleAssignmentLogUseCase.getLogs(principal.getUsername());
        List<RoleAssignmentLogResponse> response = logs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response, "Historial de asignaciones obtenido correctamente"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Role parsearRol(String rolStr) {
        try {
            Role rol = Role.valueOf(rolStr.toUpperCase());
            if (rol == Role.ADMIN_TUTOR) {
                throw new BadRequestException(
                        "No se puede asignar ADMIN_TUTOR directamente. Usa ADMIN o TUTOR.");
            }
            return rol;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol inválido: '" + rolStr + "'. Los valores válidos son: ADMIN, TUTOR");
        }
    }

    private RoleAssignmentLogResponse toResponse(RoleAssignmentLog log) {
        return RoleAssignmentLogResponse.builder()
                .id(log.getId())
                .emailAsignado(log.getEmailAsignado())
                .rolAnterior(log.getRolAnterior())
                .rolNuevo(log.getRolNuevo())
                .asignadoPor(log.getAsignadoPor())
                .fecha(log.getFecha())
                .build();
    }
}
