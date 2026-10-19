package com.tecsup.smre.auth.infrastructure.adapter.in.web;

import com.tecsup.smre.auth.application.dto.request.AsignarRolRequest;
import com.tecsup.smre.auth.application.dto.response.RoleAssignmentLogResponse;
import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.RoleAssignmentLog;
import com.tecsup.smre.auth.domain.port.in.AsignarRolUseCase;
import com.tecsup.smre.auth.domain.port.in.GetRoleAssignmentLogUseCase;
import com.tecsup.smre.auth.domain.port.in.QuitarRolUseCase;
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
 * Controlador exclusivo para la gestión de roles por parte del superadmin (ksalas).
 * La verificación de email se hace en el servicio de dominio (no solo en frontend).
 * La anotación @PreAuthorize limita la llamada a usuarios con rol ADMIN como primera barrera,
 * y el servicio verifica que sea específicamente ksalas@tecsup.edu.pe.
 */
@RestController
@RequestMapping("/api/v1/ksalas/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleAssignmentController {

    private final AsignarRolUseCase asignarRolUseCase;
    private final QuitarRolUseCase quitarRolUseCase;
    private final GetRoleAssignmentLogUseCase getRoleAssignmentLogUseCase;

    public RoleAssignmentController(AsignarRolUseCase asignarRolUseCase,
                                     QuitarRolUseCase quitarRolUseCase,
                                     GetRoleAssignmentLogUseCase getRoleAssignmentLogUseCase) {
        this.asignarRolUseCase = asignarRolUseCase;
        this.quitarRolUseCase = quitarRolUseCase;
        this.getRoleAssignmentLogUseCase = getRoleAssignmentLogUseCase;
    }

    /**
     * POST /api/v1/ksalas/roles → asignar un rol
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
     * DELETE /api/v1/ksalas/roles → quitar un rol
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> quitar(
            @Valid @RequestBody AsignarRolRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        Role rol = parsearRol(request.getRol());
        quitarRolUseCase.quitarRol(
                principal.getUsername(),
                request.getEmailDestino(),
                rol);

        return ResponseEntity.ok(ApiResponse.success(null,
                "Rol " + rol.name() + " removido exitosamente de " + request.getEmailDestino()));
    }

    /**
     * GET /api/v1/ksalas/roles/log → consultar historial de asignaciones
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
