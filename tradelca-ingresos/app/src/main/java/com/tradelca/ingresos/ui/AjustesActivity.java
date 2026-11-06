package com.tradelca.ingresos.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tradelca.ingresos.R;
import com.tradelca.ingresos.datos.Ajustes;

/** Pantalla para escribir la dirección del servidor (emulador, celular en la red o servidor de la empresa). */
public class AjustesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        EditText direccion = findViewById(R.id.direccion);
        direccion.setText(Ajustes.servidor(this));
        Button guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(v -> {
            String d = direccion.getText().toString().trim();
            if (!d.startsWith("http://") && !d.startsWith("https://")) {
                direccion.setError("Debe empezar por http:// o https://");
                return;
            }
            Ajustes.guardarServidor(this, d);
            Toast.makeText(this, "Servidor guardado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
