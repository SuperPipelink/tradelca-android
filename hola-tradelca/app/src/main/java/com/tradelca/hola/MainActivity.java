package com.tradelca.hola;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** Actividad principal: muestra un saludo y responde al botón con un mensaje. */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText nombre = findViewById(R.id.nombre);
        TextView resultado = findViewById(R.id.resultado);
        Button saludar = findViewById(R.id.saludar);

        // Evento (listener) del botón
        saludar.setOnClickListener(v -> {
            String texto = getString(R.string.mensaje, Saludo.nombreParaSaludar(nombre.getText().toString()));
            resultado.setText(texto);
            Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
        });
    }
}
