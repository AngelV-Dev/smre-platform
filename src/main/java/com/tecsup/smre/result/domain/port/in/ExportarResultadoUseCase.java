package com.tecsup.smre.result.domain.port.in;

import com.tecsup.smre.auth.domain.model.Usuario;

public interface ExportarResultadoUseCase {
    // Devuelve los bytes del PDF generado. Implementación pendiente (prioridad Media).
    byte[] exportar(Long entrevistaId, Usuario solicitante);
}
