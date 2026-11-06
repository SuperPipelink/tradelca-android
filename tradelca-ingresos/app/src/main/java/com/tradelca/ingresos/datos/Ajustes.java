package com.tradelca.ingresos.datos;

import android.content.Context;
import android.content.SharedPreferences;

/** Dirección del servidor guardada en el teléfono (persistencia local con SharedPreferences). */
public final class Ajustes {

    private static final String ARCHIVO = "ajustes";
    private static final String CLAVE = "servidor";
    public static final String POR_DEFECTO = "http://10.0.2.2:8001";   // el PC visto desde el emulador de Android

    private Ajustes() {
    }

    public static String servidor(Context c) {
        return preferencias(c).getString(CLAVE, POR_DEFECTO);
    }

    public static void guardarServidor(Context c, String direccion) {
        preferencias(c).edit().putString(CLAVE, direccion.trim()).apply();
    }

    private static SharedPreferences preferencias(Context c) {
        return c.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }
}
