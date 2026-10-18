package com.tecsup.smre.auth.infrastructure.adapter.in.web;

import com.tecsup.smre.auth.application.dto.request.EmailMatriculadoRequest;
import com.tecsup.smre.auth.domain.model.EmailMatriculado;
import com.tecsup.smre.auth.domain.port.out.EmailMatriculadoRepositoryPort;
import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.exception.BadRequestException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/emails-matriculados")
@PreAuthorize("hasRole('ADMIN')")
public class EmailMatriculadoController {

    private final EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort;

    public EmailMatriculadoController(EmailMatriculadoRepositoryPort emailMatriculadoRepositoryPort) {
        this.emailMatriculadoRepositoryPort = emailMatriculadoRepositoryPort;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmailMatriculado>>> listar() {
        List<EmailMatriculado> emails = emailMatriculadoRepositoryPort.findAll();
        return ResponseEntity.ok(ApiResponse.success(emails, "Correos matriculados obtenidos correctamente"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmailMatriculado>> matricular(@Valid @RequestBody EmailMatriculadoRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        if (!email.endsWith("@tecsup.edu.pe")) {
            throw new BadRequestException("Solo se permiten correos de Tecsup (@tecsup.edu.pe)");
        }

        if (emailMatriculadoRepositoryPort.existsByEmail(email)) {
            throw new BadRequestException("El correo " + email + " ya se encuentra matriculado en el sistema");
        }

        EmailMatriculado nuevo = EmailMatriculado.builder()
                .email(email)
                .fechaRegistro(LocalDateTime.now())
                .build();

        EmailMatriculado guardado = emailMatriculadoRepositoryPort.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(guardado, "Correo matriculado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desmatricular(@PathVariable Long id) {
        emailMatriculadoRepositoryPort.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Correo eliminado de la matrícula correctamente"));
    }
}
