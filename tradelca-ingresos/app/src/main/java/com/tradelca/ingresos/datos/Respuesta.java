package com.tradelca.ingresos.datos;

import com.tradelca.ingresos.logica.Mensajes;

import org.json.JSONException;
import org.json.JSONObject;

/** Respuesta de la API: código HTTP y cuerpo en texto. */
public final class Respuesta {

    public final int codigo;
    public final String cuerpo;

    public Respuesta(int codigo, String cuerpo) {
        this.codigo = codigo;
        this.cuerpo = cuerpo == null ? "" : cuerpo;
    }

    public boolean exitosa() {
        return codigo >= 200 && codigo < 300;
    }

    /** El cuerpo como objeto JSON (vacío si no lo es). */
    public JSONObject json() {
        try {
            return cuerpo.isEmpty() ? new JSONObject() : new JSONObject(cuerpo);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    /** Mensaje del servidor: el inicio de sesión usa la clave «detalle» y el resto de la API «detail». */
    public String mensaje() {
        JSONObject j = json();
        String m = j.optString("detalle", j.optString("detail", ""));
        return m.isEmpty() ? Mensajes.porCodigo(codigo) : m;
    }
}
