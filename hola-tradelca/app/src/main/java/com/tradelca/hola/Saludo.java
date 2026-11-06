package com.tradelca.hola;

/** Lógica pura (sin Android) para poder probarla con JUnit: arma el nombre que se saluda. */
public final class Saludo {

    private Saludo() {
    }

    /** Devuelve el nombre limpio o «aprendiz» si viene vacío. */
    public static String nombreParaSaludar(String escrito) {
        if (escrito == null || escrito.trim().isEmpty()) {
            return "aprendiz";
        }
        return escrito.trim();
    }
}
