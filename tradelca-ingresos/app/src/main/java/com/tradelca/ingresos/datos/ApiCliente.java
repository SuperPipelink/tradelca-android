package com.tradelca.ingresos.datos;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente HTTP de la API de Programación Tradelca (patrón Singleton: una sola sesión por aplicación).
 * Guarda las cookies de sesión y de CSRF como un navegador, y envía la cabecera X-CSRFToken en cada escritura.
 * Se llama siempre fuera del hilo de la interfaz.
 */
public final class ApiCliente {

    private static ApiCliente instancia;

    private final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    private String base = "";

    private ApiCliente() {
        CookieHandler.setDefault(cookies);
    }

    public static synchronized ApiCliente get() {
        if (instancia == null) {
            instancia = new ApiCliente();
        }
        return instancia;
    }

    public void setBase(String direccion) {
        String d = direccion == null ? "" : direccion.trim();
        while (d.endsWith("/")) {
            d = d.substring(0, d.length() - 1);
        }
        base = d;
    }

    /** Abre la página principal para recibir la cookie csrftoken (igual que el navegador antes de ingresar). */
    public void prepararCsrf() throws IOException {
        pedir("GET", "/", null);
    }

    public Respuesta pedir(String metodo, String ruta, JSONObject cuerpo) throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(base + ruta).openConnection();
        try {
            c.setConnectTimeout(8000);
            c.setReadTimeout(15000);
            c.setRequestMethod(metodo);
            c.setRequestProperty("Accept", "application/json");
            if (!"GET".equals(metodo)) {
                String token = csrf();
                if (token != null) {
                    c.setRequestProperty("X-CSRFToken", token);
                }
                c.setRequestProperty("Referer", base + "/");   // Django lo exige cuando el servidor va por https
            }
            if (cuerpo != null) {
                byte[] datos = cuerpo.toString().getBytes(StandardCharsets.UTF_8);
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream salida = c.getOutputStream()) {
                    salida.write(datos);
                }
            }
            int codigo = c.getResponseCode();
            InputStream entrada = codigo >= 400 ? c.getErrorStream() : c.getInputStream();
            return new Respuesta(codigo, entrada == null ? "" : leer(entrada));
        } finally {
            c.disconnect();
        }
    }

    public String csrf() {
        for (HttpCookie k : cookies.getCookieStore().getCookies()) {
            if ("csrftoken".equals(k.getName())) {
                return k.getValue();
            }
        }
        return null;
    }

    public void olvidarSesion() {
        cookies.getCookieStore().removeAll();
    }

    private static String leer(InputStream entrada) throws IOException {
        try (InputStream in = entrada; ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            byte[] trozo = new byte[4096];
            int n;
            while ((n = in.read(trozo)) != -1) {
                buf.write(trozo, 0, n);
            }
            return new String(buf.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
