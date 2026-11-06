package com.tradelca.ingresos.datos;

import org.json.JSONException;
import org.json.JSONObject;

/** Una visita tal como la entrega GET /api/visitas (objeto de transferencia de datos). */
public final class Visita {

    public long id;
    public int version;
    public String fecha;
    public String nombre;
    public String documento;
    public String empresa;
    public String motivo;
    public long visitado;
    public String visitadoNombre;
    public String horaIngreso;
    public String horaSalida;

    public static Visita desde(JSONObject j) {
        Visita v = new Visita();
        v.id = j.optLong("id");
        v.version = j.optInt("version", 1);
        v.fecha = j.optString("fecha");
        v.nombre = j.optString("nombre");
        v.documento = j.optString("documento");
        v.empresa = j.optString("empresa");
        v.motivo = j.optString("motivo");
        v.visitado = j.optLong("visitado");
        v.visitadoNombre = j.optString("visitado_nombre");
        v.horaIngreso = j.optString("hora_ingreso");
        v.horaSalida = j.isNull("hora_salida") ? null : j.optString("hora_salida");
        return v;
    }

    /** Cuerpo para PUT /api/visitas/{id}: todos los campos, la versión vigente y la hora de salida. */
    public JSONObject paraActualizar(String salida) throws JSONException {
        JSONObject j = new JSONObject();
        j.put("fecha", fecha);
        j.put("nombre", nombre);
        j.put("documento", documento);
        j.put("empresa", empresa);
        j.put("motivo", motivo);
        j.put("visitado", visitado);
        j.put("hora_ingreso", horaIngreso);
        j.put("hora_salida", salida);
        j.put("version", version);
        return j;
    }

    /** Texto de la fila en la lista. */
    @Override
    public String toString() {
        String salida = horaSalida == null ? "adentro" : horaSalida;
        return nombre + "\nVisita a: " + visitadoNombre + " · " + horaIngreso + " → " + salida;
    }
}
