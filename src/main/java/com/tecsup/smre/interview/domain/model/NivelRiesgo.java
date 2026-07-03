package com.tecsup.smre.interview.domain.model;

public enum NivelRiesgo {
    ALTO,
    MEDIO,
    BAJO;

    public String getRecomendacion() {
        switch (this) {
            case ALTO:
                return "Derivar a soporte urgente";
            case MEDIO:
                return "Seguimiento quincenal";
            case BAJO:
                return "Próximo seguimiento en 30 días";
            default:
                throw new IllegalStateException("Nivel de riesgo no válido");
        }
    }
}
