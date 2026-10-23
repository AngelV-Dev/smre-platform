package com.tecsup.smre.interview.infrastructure.adapter.in;

import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.interview.application.dto.response.ResumenEntrevistasResponseDto;
import com.tecsup.smre.interview.application.port.in.GetResumenEntrevistasAdminUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint exclusivo para la vista del admin: lista de alumnos agrupada
 * por tutor o por carrera, con contadores de evaluados.
 * Solo ADMIN y ADMIN_TUTOR pueden acceder (TUTOR puro recibe 403).
 */
@RestController
@RequestMapping("/api/v1/admin/entrevistas")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEntrevistasResumenController {

    private final GetResumenEntrevistasAdminUseCase useCase;

    public AdminEntrevistasResumenController(GetResumenEntrevistasAdminUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * GET /api/v1/admin/entrevistas/resumen?agruparPor=tutor|carrera
     *
     * @param agruparPor "tutor" (por defecto) o "carrera"
     */
    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<ResumenEntrevistasResponseDto>> getResumen(
            @RequestParam(required = false) String agruparPor) {
        ResumenEntrevistasResponseDto resultado = useCase.execute(agruparPor);
        return ResponseEntity.ok(ApiResponse.success(resultado, "Resumen obtenido exitosamente"));
    }
}
