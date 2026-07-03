package com.tecsup.smre.csv.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvUploadResponseDto {
    private int totalProcesados;
    private int guardados;
    private int errores;
    private List<String> detalleErrores;
}
