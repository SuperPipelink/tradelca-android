package com.tradelca.ingresos.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tradelca.ingresos.R;
import com.tradelca.ingresos.datos.ApiCliente;
import com.tradelca.ingresos.datos.Persona;
import com.tradelca.ingresos.logica.Reloj;
import com.tradelca.ingresos.logica.ValidadorVisita;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Formulario de una visita nueva (POST /api/visitas). La lista de personas sale de visitado_opciones. */
public class NuevaVisitaActivity extends Activity {

    private final List<Persona> personas = new ArrayList<>();
    private ArrayAdapter<Persona> adaptador;
    private EditText nombre;
    private EditText documento;
    private EditText empresa;
    private EditText motivo;
    private EditText horaIngreso;
    private Spinner visitado;
    private TextView mensaje;
    private Button registrar;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_visita);
        nombre = findViewById(R.id.nombre);
        documento = findViewById(R.id.documento);
        empresa = findViewById(R.id.empresa);
        motivo = findViewById(R.id.motivo);
        horaIngreso = findViewById(R.id.hora_ingreso);
        visitado = findViewById(R.id.visitado);
        mensaje = findViewById(R.id.mensaje);
        registrar = findViewById(R.id.registrar);

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, personas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitado.setAdapter(adaptador);

        Date ahora = new Date();
        fecha = Reloj.hoy(ahora);
        horaIngreso.setText(Reloj.hora(ahora));
        registrar.setOnClickListener(v -> enviar());
        cargarPersonas();
    }

    private void cargarPersonas() {
        Tarea.correr(() -> ApiCliente.get().pedir("GET", "/api/visitas?fecha=" + fecha, null), (r, error) -> {
            if (error != null || !r.exitosa()) {
                mensaje.setText(error != null ? "Sin conexión con el servidor." : r.mensaje());
                return;
            }
            personas.clear();
            JSONArray opciones = r.json().optJSONArray("visitado_opciones");
            if (opciones != null) {
                for (int i = 0; i < opciones.length(); i++) {
                    JSONObject o = opciones.optJSONObject(i);
                    personas.add(new Persona(o.optLong("id"), o.optString("nombre")));
                }
            }
            adaptador.notifyDataSetChanged();
        });
    }

    private void enviar() {
        Persona elegida = (Persona) visitado.getSelectedItem();
        Long idVisitado = elegida == null ? null : elegida.id;
        String n = nombre.getText().toString().trim();
        String h = horaIngreso.getText().toString().trim();
        List<String> errores = ValidadorVisita.validar(n, idVisitado, h, null);
        if (!errores.isEmpty()) {
            mensaje.setText(TextUtils.join("\n", errores));   // se valida antes de enviar; el servidor vuelve a validar
            return;
        }
        registrar.setEnabled(false);
        mensaje.setText("Guardando…");
        Tarea.correr(() -> {
            JSONObject cuerpo = new JSONObject();
            cuerpo.put("fecha", fecha);
            cuerpo.put("nombre", n);
            cuerpo.put("documento", documento.getText().toString().trim());
            cuerpo.put("empresa", empresa.getText().toString().trim());
            cuerpo.put("motivo", motivo.getText().toString().trim());
            cuerpo.put("visitado", idVisitado);
            cuerpo.put("hora_ingreso", h);
            return ApiCliente.get().pedir("POST", "/api/visitas", cuerpo);
        }, (r, error) -> {
            registrar.setEnabled(true);
            if (error != null) {
                mensaje.setText("Sin conexión con el servidor.");
            } else if (r.exitosa()) {
                Toast.makeText(this, "Visita registrada.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mensaje.setText(r.mensaje());
            }
        });
    }
}
