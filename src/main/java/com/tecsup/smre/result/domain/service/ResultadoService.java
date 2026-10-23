package com.tecsup.smre.result.domain.service;

import com.tecsup.smre.auth.domain.model.Role;
import com.tecsup.smre.auth.domain.model.Usuario;
import com.tecsup.smre.exception.ResourceNotFoundException;
import com.tecsup.smre.exception.UnauthorizedException;
import com.tecsup.smre.interview.domain.model.Entrevista;
import com.tecsup.smre.result.application.dto.response.ResultadoResponseDto;
import com.tecsup.smre.result.domain.port.in.ExportarResultadoUseCase;
import com.tecsup.smre.result.domain.port.in.GetHistorialEntrevistasUseCase;
import com.tecsup.smre.result.domain.port.in.GetResultadoEntrevistaUseCase;
import com.tecsup.smre.result.domain.port.out.ResultadoRepositoryPort;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResultadoService implements
        GetResultadoEntrevistaUseCase,
        GetHistorialEntrevistasUseCase,
        ExportarResultadoUseCase {

    // Mismo orden que EntrevistaService.getCriterios() — usado para nombrar cada respuesta en el export
    private static final String[] PREGUNTAS = {
            "Rendimiento académico",
            "Bienestar emocional",
            "Trabajo en equipo",
            "Comunicación efectiva",
            "Trabajo / Economía",
            "Estrés - estado emocional"
    };

    private final ResultadoRepositoryPort resultadoRepositoryPort;

    public ResultadoService(ResultadoRepositoryPort resultadoRepositoryPort) {
        this.resultadoRepositoryPort = resultadoRepositoryPort;
    }

    @Override
    public ResultadoResponseDto getResultado(Long entrevistaId, Usuario solicitante) {
        Entrevista entrevista = resultadoRepositoryPort.findById(entrevistaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe la entrevista con id: " + entrevistaId));

        validarAcceso(entrevista, solicitante);
        return toResponse(entrevista);
    }

    @Override
    public List<ResultadoResponseDto> getHistorial(Long alumnoId, Usuario solicitante) {
        List<Entrevista> entrevistas = resultadoRepositoryPort.findHistorialByAlumnoId(alumnoId);

        // El historial es por alumno: si hay al menos una entrevista, basta validar acceso con la primera
        // (todas pertenecen al mismo alumno/tutor en este flujo).
        if (!entrevistas.isEmpty()) {
            validarAcceso(entrevistas.get(0), solicitante);
        }

        return entrevistas.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public byte[] exportar(Long entrevistaId, Usuario solicitante) {
        ResultadoResponseDto resultado = getResultado(entrevistaId, solicitante);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Entrevista");

            // Estilos
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle boldLabelStyle = workbook.createCellStyle();
            boldLabelStyle.setFont(boldFont);

            CellStyle sectionHeaderStyle = workbook.createCellStyle();
            Font sectionFont = workbook.createFont();
            sectionFont.setBold(true);
            sectionFont.setColor(IndexedColors.WHITE.getIndex());
            sectionHeaderStyle.setFont(sectionFont);
            sectionHeaderStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            sectionHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIndex = 0;

            // Encabezado principal
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE ENTREVISTA - SMRE");
            titleCell.setCellStyle(sectionHeaderStyle);
            titleRow.createCell(1).setCellStyle(sectionHeaderStyle);

            rowIndex++; // Línea en blanco

            // Encabezados de tabla
            Row headerRow = sheet.createRow(rowIndex++);
            Cell h1 = headerRow.createCell(0);
            h1.setCellValue("Campo");
            h1.setCellStyle(headerStyle);
            Cell h2 = headerRow.createCell(1);
            h2.setCellValue("Valor");
            h2.setCellStyle(headerStyle);

            // Datos del alumno y entrevista
            addRow(sheet, rowIndex++, boldLabelStyle, "ID Alumno", resultado.getAlumnoId() != null ? resultado.getAlumnoId().toString() : "");
            addRow(sheet, rowIndex++, boldLabelStyle, "Nombre del Alumno", resultado.getAlumnoNombre() + " " + resultado.getAlumnoApellido());
            addRow(sheet, rowIndex++, boldLabelStyle, "Tutor", resultado.getTutorNombre() != null ? resultado.getTutorNombre() : "");
            addRow(sheet, rowIndex++, boldLabelStyle, "Fecha", resultado.getFecha() != null ? resultado.getFecha().toString() : "");
            addRow(sheet, rowIndex++, boldLabelStyle, "Puntaje Total", resultado.getPuntajeTotal() != null ? resultado.getPuntajeTotal().toString() : "0");
            addRow(sheet, rowIndex++, boldLabelStyle, "Nivel de Riesgo", resultado.getNivelRiesgo() != null ? resultado.getNivelRiesgo() : "");
            addRow(sheet, rowIndex++, boldLabelStyle, "Recomendación", resultado.getRecomendacion() != null ? resultado.getRecomendacion() : "");
            addRow(sheet, rowIndex++, boldLabelStyle, "Observaciones", resultado.getObservaciones() != null ? resultado.getObservaciones() : "");

            rowIndex++; // Línea en blanco

            // Sección: Respuestas por Criterio
            Row secRow = sheet.createRow(rowIndex++);
            Cell secCell1 = secRow.createCell(0);
            secCell1.setCellValue("Criterio");
            secCell1.setCellStyle(headerStyle);
            Cell secCell2 = secRow.createCell(1);
            secCell2.setCellValue("Nivel Evaluado");
            secCell2.setCellStyle(headerStyle);

            if (resultado.getRespuestas() != null) {
                List<String> respuestas = resultado.getRespuestas();
                for (int i = 0; i < respuestas.size() && i < PREGUNTAS.length; i++) {
                    Row r = sheet.createRow(rowIndex++);
                    r.createCell(0).setCellValue(PREGUNTAS[i]);
                    r.createCell(1).setCellValue(respuestas.get(i));
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    private void addRow(Sheet sheet, int rowIndex, CellStyle labelStyle, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
    }

    private void validarAcceso(Entrevista entrevista, Usuario solicitante) {
        boolean esAdmin = solicitante.getRol() == Role.ADMIN || solicitante.getRol() == Role.ADMIN_TUTOR;
        boolean esTutorAsignado = (solicitante.getRol() == Role.TUTOR || solicitante.getRol() == Role.ADMIN_TUTOR)
                && entrevista.getTutorId() != null
                && entrevista.getTutorId().equals(solicitante.getId().toString());

        if (!esAdmin && !esTutorAsignado) {
            throw new UnauthorizedException(
                    "No tienes permiso para ver esta entrevista");
        }
    }

    private ResultadoResponseDto toResponse(Entrevista entrevista) {
        return ResultadoResponseDto.builder()
                .entrevistaId(entrevista.getId())
                .alumnoId(entrevista.getAlumnoId() != null ? Long.valueOf(entrevista.getAlumnoId()) : null)
                .alumnoNombre(entrevista.getAlumnoNombre())
                .alumnoApellido(entrevista.getAlumnoApellido())
                .tutorNombre(entrevista.getTutorNombre())
                .puntajeTotal(entrevista.getPuntajeTotal())
                .nivelRiesgo(entrevista.getNivelRiesgo() != null ? entrevista.getNivelRiesgo().name() : null)
                .recomendacion(entrevista.getRecomendacion())
                .observaciones(entrevista.getObservaciones())
                .fecha(entrevista.getFecha())
                .respuestas(entrevista.getRespuestas() != null
                        ? Arrays.asList(entrevista.getRespuestas().split(","))
                        : List.of())
                .build();
    }
}
