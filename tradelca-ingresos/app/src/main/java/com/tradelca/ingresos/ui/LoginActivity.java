package com.tradelca.ingresos.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tradelca.ingresos.R;
import com.tradelca.ingresos.datos.Ajustes;
import com.tradelca.ingresos.datos.ApiCliente;
import com.tradelca.ingresos.datos.Respuesta;

import org.json.JSONObject;

/**
 * Ingreso con usuario, clave y código del segundo factor (POST /api/auth/login).
 * La API responde estado ok, pide_token o configurar_2fa; los errores vienen con su mensaje.
 */
public class LoginActivity extends Activity {

    private EditText usuario;
    private EditText clave;
    private EditText codigo;
    private TextView mensaje;
    private Button entrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usuario = findViewById(R.id.usuario);
        clave = findViewById(R.id.clave);
        codigo = findViewById(R.id.codigo);
        mensaje = findViewById(R.id.mensaje);
        entrar = findViewById(R.id.entrar);

        entrar.setOnClickListener(v -> ingresar());
        Button servidor = findViewById(R.id.servidor);
        servidor.setOnClickListener(v -> startActivity(new Intent(this, AjustesActivity.class)));   // intent explícito
    }

    private void ingresar() {
        String u = usuario.getText().toString().trim();
        String c = clave.getText().toString();
        String t = codigo.getText().toString().trim();
        if (u.isEmpty() || c.isEmpty()) {
            mensaje.setText("Escribe el usuario y la clave.");
            return;
        }
        entrar.setEnabled(false);
        mensaje.setText("Conectando…");
        String direccion = Ajustes.servidor(this);
        Tarea.correr(() -> {
            ApiCliente api = ApiCliente.get();
            api.setBase(direccion);
            api.olvidarSesion();
            api.prepararCsrf();
            JSONObject cuerpo = new JSONObject();
            cuerpo.put("usuario", u);
            cuerpo.put("clave", c);
            if (!t.isEmpty()) {
                cuerpo.put("token", t);
            }
            return api.pedir("POST", "/api/auth/login", cuerpo);
        }, (r, error) -> {
            entrar.setEnabled(true);
            if (error != null) {
                mensaje.setText("No hay conexión con " + direccion + ". Revisa la dirección en «Servidor».");
                return;
            }
            atender(r);
        });
    }

    private void atender(Respuesta r) {
        if (!r.exitosa()) {
            mensaje.setText(r.mensaje());
            return;
        }
        JSONObject j = r.json();
        String estado = j.optString("estado");
        if ("pide_token".equals(estado)) {
            mensaje.setText("Escribe el código de tu aplicación de autenticación.");
            codigo.requestFocus();
        } else if ("configurar_2fa".equals(estado)) {
            mensaje.setText("Este usuario aún no tiene segundo factor. Entra primero desde el navegador, escanea el código QR y vuelve.");
        } else if ("ok".equals(estado)) {
            if (j.optBoolean("debe_cambiar_clave", false)) {
                mensaje.setText("Debes cambiar tu clave temporal desde el navegador antes de usar la aplicación.");
                return;
            }
            Intent i = new Intent(this, VisitasActivity.class);
            i.putExtra(VisitasActivity.EXTRA_USUARIO, j.optString("usuario"));
            i.putExtra(VisitasActivity.EXTRA_ROL, j.optString("rol"));
            startActivity(i);
            clave.setText("");
            codigo.setText("");
            mensaje.setText("");
        } else {
            mensaje.setText("Respuesta inesperada del servidor.");
        }
    }
}
