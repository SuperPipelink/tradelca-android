package com.tradelca.ingresos.ui;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Ejecuta el trabajo de red en segundo plano y devuelve el resultado en el hilo de la interfaz (reemplaza a AsyncTask). */
public final class Tarea {

    public interface Trabajo<T> {
        T hacer() throws Exception;
    }

    public interface AlTerminar<T> {
        void listo(T resultado, Exception error);
    }

    private static final ExecutorService HILOS = Executors.newSingleThreadExecutor();
    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());

    private Tarea() {
    }

    public static <T> void correr(Trabajo<T> trabajo, AlTerminar<T> alTerminar) {
        HILOS.execute(() -> {
            T resultado = null;
            Exception error = null;
            try {
                resultado = trabajo.hacer();
            } catch (Exception e) {
                error = e;
            }
            final T r = resultado;
            final Exception e = error;
            PRINCIPAL.post(() -> alTerminar.listo(r, e));
        });
    }
}
