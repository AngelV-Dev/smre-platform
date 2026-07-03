package com.tecsup.smre.interview.domain.model;

public enum NivelRiesgo {
    ALTO,
    MEDIO,
    BAJO;

    public String getRecomendacion() {
        switch (this) {
            case ALTO:
                return "El estudiante enfrenta una situación que puede afectar seriamente su continuidad. Debe ser derivado a la Oficina de Bienestar Estudiantil.";
            case MEDIO:
                return "El estudiante presenta algunas dificultades moderadas. Se recomienda que el tutor le brinde acompañamiento cercano...";
            case BAJO:
                return "El estudiante muestra una buena adaptación académica, emocional y social. No se requiere ninguna acción adicional...";
            default:
                throw new IllegalStateException("Nivel de riesgo no válido");
        }
    }
}
