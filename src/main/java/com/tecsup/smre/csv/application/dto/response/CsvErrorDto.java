package com.tecsup.smre.csv.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvErrorDto {
    private int fila;
    private String motivo;
    private String datos;
}