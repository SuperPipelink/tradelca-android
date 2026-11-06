package com.tradelca.ingresos.logica;

/** Traduce el código HTTP de la API a un mensaje para la persona cuando el servidor no trae uno. */
public final class Mensajes {

    private Mensajes() {
    }

    public static String porCodigo(int codigo) {
        switch (codigo) {
            case 400:
                return "Hay un dato mal escrito o incompleto.";
            case 403:
                return "No tienes permiso para esta acción o la sesión terminó.";
            case 404:
                return "El registro ya no existe.";
            case 409:
                return "Otra persona modificó este registro. Actualiza la lista e intenta de nuevo.";
            case 429:
                return "Demasiados intentos. Espera un minuto.";
            default:
                return codigo >= 500 ? "Error del servidor (" + codigo + ")." : "Respuesta inesperada (" + codigo + ").";
        }
    }
}
