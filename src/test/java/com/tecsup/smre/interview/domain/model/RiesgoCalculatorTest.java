package com.tecsup.smre.interview.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiesgoCalculatorTest {

    @Test
    void testCalcularPuntajeTotal_CantidadRespuestasIncorrecta_LanzaExcepcion() {
        List<Severidad> respuestas = Arrays.asList(Severidad.ALTO, Severidad.MEDIO);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            RiesgoCalculator.calcularPuntajeTotal(respuestas);
        });

        assertEquals("Se requieren exactamente 6 respuestas para calcular el puntaje.", exception.getMessage());
    }

    @Test
    void testDeterminarNivelRiesgo_Alto() {
        // 5 ALTO (3) + 1 BAJO (1) = 16 puntos
        List<Severidad> respuestas = Arrays.asList(Severidad.ALTO, Severidad.ALTO, Severidad.ALTO, Severidad.ALTO, Severidad.ALTO, Severidad.BAJO);
        int puntaje = RiesgoCalculator.calcularPuntajeTotal(respuestas);
        assertEquals(NivelRiesgo.ALTO, RiesgoCalculator.determinarNivelRiesgo(puntaje));
    }

    @Test
    void testDeterminarNivelRiesgo_Medio() {
        // 6 MEDIO (2) = 12 puntos
        List<Severidad> respuestas = Arrays.asList(Severidad.MEDIO, Severidad.MEDIO, Severidad.MEDIO, Severidad.MEDIO, Severidad.MEDIO, Severidad.MEDIO);
        int puntaje = RiesgoCalculator.calcularPuntajeTotal(respuestas);
        assertEquals(NivelRiesgo.MEDIO, RiesgoCalculator.determinarNivelRiesgo(puntaje));
    }

    @Test
    void testDeterminarNivelRiesgo_Bajo() {
        // 6 BAJO (1) = 6 puntos
        List<Severidad> respuestas = Arrays.asList(Severidad.BAJO, Severidad.BAJO, Severidad.BAJO, Severidad.BAJO, Severidad.BAJO, Severidad.BAJO);
        int puntaje = RiesgoCalculator.calcularPuntajeTotal(respuestas);
        assertEquals(NivelRiesgo.BAJO, RiesgoCalculator.determinarNivelRiesgo(puntaje));
    }
}
