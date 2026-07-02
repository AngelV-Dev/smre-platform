package com.tecsup.smre.csv.infrastructure.adapter.in.web;

import com.tecsup.smre.common.dto.ApiResponse;
import com.tecsup.smre.csv.application.dto.response.CsvUploadResponseDto;
import com.tecsup.smre.csv.domain.port.in.CargaCsvUseCase;
import com.tecsup.smre.csv.domain.port.in.DescargarPlantillaCsvUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/csv")
public class CsvController {

    private final CargaCsvUseCase cargaCsvUseCase;
    private final DescargarPlantillaCsvUseCase descargarPlantillaCsvUseCase;

    public CsvController(CargaCsvUseCase cargaCsvUseCase,
                          DescargarPlantillaCsvUseCase descargarPlantillaCsvUseCase) {
        this.cargaCsvUseCase = cargaCsvUseCase;
        this.descargarPlantillaCsvUseCase = descargarPlantillaCsvUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CsvUploadResponseDto>> cargar(@RequestParam("file") MultipartFile file) {
        CsvUploadResponseDto reporte = cargaCsvUseCase.cargar(file);
        return ResponseEntity.ok(ApiResponse.success(reporte, "Archivo procesado: "
                + reporte.getGuardados() + " alumnos guardados, " + reporte.getErrores() + " errores"));
    }

    @GetMapping("/plantilla")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarPlantilla() {
        byte[] plantilla = descargarPlantillaCsvUseCase.generarPlantilla();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_alumnos.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(plantilla);
    }
}
