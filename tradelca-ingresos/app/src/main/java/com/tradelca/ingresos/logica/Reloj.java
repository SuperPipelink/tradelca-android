package com.tradelca.ingresos.logica;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Fecha y hora en la zona de Tradelca (America/Bogota), con el formato que espera la API. */
public final class Reloj {

    private static final TimeZone BOGOTA = TimeZone.getTimeZone("America/Bogota");

    private Reloj() {
    }

    public static String hoy(Date ahora) {
        return formato("yyyy-MM-dd", ahora);
    }

    public static String hora(Date ahora) {
        return formato("HH:mm", ahora);
    }

    private static String formato(String patron, Date fecha) {
        SimpleDateFormat f = new SimpleDateFormat(patron, Locale.US);
        f.setTimeZone(BOGOTA);
        return f.format(fecha);
    }
}
