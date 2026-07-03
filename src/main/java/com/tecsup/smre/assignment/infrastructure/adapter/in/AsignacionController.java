package com.tecsup.smre.assignment.infrastructure.adapter.in;

import com.tecsup.smre.assignment.application.dto.request.AssignmentRequest;
import com.tecsup.smre.assignment.application.dto.response.AssignmentResponse;
import com.tecsup.smre.assignment.domain.port.in.CrearAsignacionUseCase;
import com.tecsup.smre.assignment.domain.port.in.EliminarAsignacionUseCase;
import com.tecsup.smre.assignment.domain.port.in.ListarAsignacionesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/asignaciones")
public class AsignacionController {

    private final CrearAsignacionUseCase crearAsignacionUseCase;
    private final ListarAsignacionesUseCase listarAsignacionesUseCase;
    private final EliminarAsignacionUseCase eliminarAsignacionUseCase;

    public AsignacionController(CrearAsignacionUseCase crearAsignacionUseCase,
                                ListarAsignacionesUseCase listarAsignacionesUseCase,
                                EliminarAsignacionUseCase eliminarAsignacionUseCase) {
        this.crearAsignacionUseCase = crearAsignacionUseCase;
        this.listarAsignacionesUseCase = listarAsignacionesUseCase;
        this.eliminarAsignacionUseCase = eliminarAsignacionUseCase;
    }

    @PostMapping
    public ResponseEntity<AssignmentResponse> crear(@Valid @RequestBody AssignmentRequest request) {
        AssignmentResponse response = crearAsignacionUseCase.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> listar(
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String ciclo,
            @RequestParam(required = false) String grupo
    ) {
        // Lógica básica de filtros en controller (orquestar qué UseCase usar)
        if (periodo != null && !periodo.isEmpty()) {
            return ResponseEntity.ok(listarAsignacionesUseCase.listarPorPeriodo(periodo));
        }
        return ResponseEntity.ok(listarAsignacionesUseCase.listarTodo());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarAsignacionUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}