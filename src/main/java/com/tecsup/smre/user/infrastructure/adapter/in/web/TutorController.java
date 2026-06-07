package com.tecsup.smre.user.infrastructure.adapter.in.web;

import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.user.application.dto.request.TutorRequest;
import com.tecsup.smre.user.application.dto.response.TutorResponse;
import com.tecsup.smre.user.domain.port.in.TutorUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tutores")
public class TutorController {

    private final TutorUseCase tutorUseCase;

    public TutorController(TutorUseCase tutorUseCase) {
        this.tutorUseCase = tutorUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TutorResponse>> crear(@Valid @RequestBody TutorRequest request) {
        TutorResponse response = tutorUseCase.crear(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tutor registrado correctamente"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TutorResponse>>> listar() {
        List<TutorResponse> tutores = tutorUseCase.listar();
        return ResponseEntity.ok(ApiResponse.success(tutores, "Tutores obtenidos correctamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TutorResponse>> obtener(@PathVariable Long id) {
        TutorResponse response = tutorUseCase.obtener(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Tutor obtenido correctamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TutorResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TutorRequest request) {
        TutorResponse response = tutorUseCase.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tutor actualizado correctamente"));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        tutorUseCase.cambiarEstado(id, activo);
        return ResponseEntity.ok(ApiResponse.success(null, 
                activo ? "Tutor activado correctamente" : "Tutor desactivado correctamente"));
    }
}