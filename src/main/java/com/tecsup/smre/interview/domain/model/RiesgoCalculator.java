package com.tecsup.smre.interview.domain.model;

import java.util.List;

public class RiesgoCalculator {

    public static int calcularPuntajeTotal(List<Severidad> respuestas) {
        if (respuestas == null || respuestas.size() != 6) {
            throw new IllegalArgumentException("Se requieren exactamente 6 respuestas para calcular el puntaje.");
        }
        return respuestas.stream()
                .mapToInt(Severidad::getPuntos)
                .sum();
    }

    public static NivelRiesgo determinarNivelRiesgo(int puntajeTotal) {
        if (puntajeTotal >= 14) {
            return NivelRiesgo.ALTO;
        } else if (puntajeTotal >= 8) {
            return NivelRiesgo.MEDIO;
        } else {
            return NivelRiesgo.BAJO;
        }
    }
}
