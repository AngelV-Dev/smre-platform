package com.tecsup.smre.interview.domain.model;

public enum Severidad {
    ALTO(3),
    MEDIO(2),
    BAJO(1);

    private final int puntos;

    Severidad(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }
}
