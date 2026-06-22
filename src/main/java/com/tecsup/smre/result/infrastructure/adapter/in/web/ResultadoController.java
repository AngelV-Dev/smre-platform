package com.tecsup.smre.result.infrastructure.adapter.in.web;

import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;
import com.tecsup.smre.result.domain.port.in.ExportarResultadoUseCase;
import com.tecsup.smre.result.domain.port.in.GetHistorialEntrevistasUseCase;
import com.tecsup.smre.result.domain.port.in.GetResultadoEntrevistaUseCase;
import com.tecsup.smre.security.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ResultadoController {

    private final GetResultadoEntrevistaUseCase getResultadoEntrevistaUseCase;
    private final GetHistorialEntrevistasUseCase getHistorialEntrevistasUseCase;
    private final ExportarResultadoUseCase exportarResultadoUseCase;

    public ResultadoController(GetResultadoEntrevistaUseCase getResultadoEntrevistaUseCase,
                                GetHistorialEntrevistasUseCase getHistorialEntrevistasUseCase,
                                ExportarResultadoUseCase exportarResultadoUseCase) {
        this.getResultadoEntrevistaUseCase = getResultadoEntrevistaUseCase;
        this.getHistorialEntrevistasUseCase = getHistorialEntrevistasUseCase;
        this.exportarResultadoUseCase = exportarResultadoUseCase;
    }

    @GetMapping("/api/entrevistas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<ApiResponse<ResultadoResponseDto>> getResultado(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        ResultadoResponseDto resultado =
                getResultadoEntrevistaUseCase.getResultado(id, principal.getUsuario());
        return ResponseEntity.ok(ApiResponse.success(resultado, "Resultado obtenido correctamente"));
    }

    @GetMapping("/api/entrevistas/historial/{alumnoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<ApiResponse<List<ResultadoResponseDto>>> getHistorial(
            @PathVariable Long alumnoId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ResultadoResponseDto> historial =
                getHistorialEntrevistasUseCase.getHistorial(alumnoId, principal.getUsuario());
        return ResponseEntity.ok(ApiResponse.success(historial, "Historial obtenido correctamente"));
    }

    @GetMapping(value = "/api/entrevistas/{id}/exportar", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<byte[]> exportar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        byte[] pdf = exportarResultadoUseCase.exportar(id, principal.getUsuario());
        return ResponseEntity.ok(pdf);
    }
}
