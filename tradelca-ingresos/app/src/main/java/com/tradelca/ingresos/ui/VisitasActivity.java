package com.tradelca.ingresos.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tradelca.ingresos.R;
import com.tradelca.ingresos.datos.ApiCliente;
import com.tradelca.ingresos.datos.Respuesta;
import com.tradelca.ingresos.datos.Visita;
import com.tradelca.ingresos.logica.Reloj;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Lista las visitas de hoy (GET /api/visitas) y registra la hora de salida (PUT /api/visitas/{id}). */
public class VisitasActivity extends Activity {

    public static final String EXTRA_USUARIO = "usuario";
    public static final String EXTRA_ROL = "rol";

    private final List<Visita> visitas = new ArrayList<>();
    private ArrayAdapter<Visita> adaptador;
    private TextView vacio;
    private TextView encabezado;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitas);
        encabezado = findViewById(R.id.encabezado);
        vacio = findViewById(R.id.vacio);
        ListView lista = findViewById(R.id.lista);
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, visitas);
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener((padre, vista, posicion, id) -> preguntarSalida(visitas.get(posicion)));

        Button nueva = findViewById(R.id.nueva);
        nueva.setOnClickListener(v -> startActivity(new Intent(this, NuevaVisitaActivity.class)));
        Button actualizar = findViewById(R.id.actualizar);
        actualizar.setOnClickListener(v -> cargar());
        Button salir = findViewById(R.id.salir);
        salir.setOnClickListener(v -> cerrarSesion());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();   // al volver de «Nueva visita» la lista queda al día
    }

    private void cargar() {
        fecha = Reloj.hoy(new Date());
        String usuario = getIntent().getStringExtra(EXTRA_USUARIO);
        encabezado.setText(fecha + " · " + (usuario == null ? "" : usuario));
        Tarea.correr(() -> ApiCliente.get().pedir("GET", "/api/visitas?fecha=" + fecha, null), (r, error) -> {
            if (error != null) {
                Toast.makeText(this, "Sin conexión con el servidor.", Toast.LENGTH_LONG).show();
                return;
            }
            if (!r.exitosa()) {
                avisarYVolverSiHaceFalta(r);
                return;
            }
            visitas.clear();
            JSONArray filas = r.json().optJSONArray("filas");
            if (filas != null) {
                for (int i = 0; i < filas.length(); i++) {
                    visitas.add(Visita.desde(filas.optJSONObject(i)));
                }
            }
            adaptador.notifyDataSetChanged();
            vacio.setVisibility(visitas.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void preguntarSalida(Visita v) {
        if (v.horaSalida != null) {
            Toast.makeText(this, "Esta visita ya tiene salida.", Toast.LENGTH_SHORT).show();
            return;
        }
        String ahora = Reloj.hora(new Date());
        new AlertDialog.Builder(this)
                .setTitle("Registrar salida")
                .setMessage(v.nombre + " sale a las " + ahora + ".")
                .setPositiveButton("Registrar", (d, w) -> registrarSalida(v, ahora))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void registrarSalida(Visita v, String hora) {
        Tarea.correr(() -> ApiCliente.get().pedir("PUT", "/api/visitas/" + v.id, v.paraActualizar(hora)), (r, error) -> {
            if (error != null) {
                Toast.makeText(this, "Sin conexión con el servidor.", Toast.LENGTH_LONG).show();
            } else if (r.exitosa()) {
                Toast.makeText(this, "Salida registrada.", Toast.LENGTH_SHORT).show();
                cargar();
            } else {
                avisarYVolverSiHaceFalta(r);
                cargar();
            }
        });
    }

    private void avisarYVolverSiHaceFalta(Respuesta r) {
        Toast.makeText(this, r.mensaje(), Toast.LENGTH_LONG).show();
        if (r.codigo == 403 && "sin_sesion".equals(r.json().optString("codigo"))) {
            finish();   // la sesión terminó: vuelve al ingreso
        }
    }

    private void cerrarSesion() {
        Tarea.correr(() -> ApiCliente.get().pedir("POST", "/api/auth/logout", null), (r, error) -> {
            ApiCliente.get().olvidarSesion();
            finish();
        });
    }
}
