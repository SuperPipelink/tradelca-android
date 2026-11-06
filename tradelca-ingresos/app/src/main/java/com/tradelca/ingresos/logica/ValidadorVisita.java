package com.tradelca.ingresos.logica;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de una visita, iguales a las del servidor (nucleo/visitas.py): se revisan antes de enviar
 * para dar el mensaje de inmediato. El servidor vuelve a validar; esta clase no lo reemplaza.
 * No depende de Android, por eso se prueba con JUnit en la JVM.
 */
public final class ValidadorVisita {

    public static final int MAX_NOMBRE = 150;
    public static final int MAX_DOCUMENTO = 30;
    public static final int MAX_EMPRESA = 150;
    public static final int MAX_MOTIVO = 250;

    /** HH:MM de 00:00 a 23:59. */
    private static final Pattern HORA = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    private ValidadorVisita() {
    }

    public static boolean horaValida(String hora) {
        return hora != null && HORA.matcher(hora.trim()).matches();
    }

    /** Minutos desde la medianoche de una hora HH:MM ya validada. */
    public static int minutos(String hora) {
        String[] partes = hora.trim().split(":");
        return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
    }

    /**
     * Devuelve la lista de problemas (vacía si la visita es válida).
     *
     * @param visitado id de la persona visitada o null si no se eligió
     * @param horaSalida puede ser null o vacía (la visita sigue adentro)
     */
    public static List<String> validar(String nombre, Long visitado, String horaIngreso, String horaSalida) {
        List<String> errores = new ArrayList<>();
        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add("El nombre del visitante es obligatorio.");
        } else if (nombre.trim().length() > MAX_NOMBRE) {
            errores.add("El nombre admite máximo " + MAX_NOMBRE + " caracteres.");
        }
        if (visitado == null) {
            errores.add("Elige la persona visitada.");
        }
        if (horaIngreso == null || horaIngreso.trim().isEmpty()) {
            errores.add("La hora de ingreso es obligatoria.");
        } else if (!horaValida(horaIngreso)) {
            errores.add("La hora de ingreso debe venir como HH:MM.");
        }
        boolean haySalida = horaSalida != null && !horaSalida.trim().isEmpty();
        if (haySalida && !horaValida(horaSalida)) {
            errores.add("La hora de salida debe venir como HH:MM.");
        } else if (haySalida && horaValida(horaIngreso) && minutos(horaSalida) < minutos(horaIngreso)) {
            errores.add("La hora de salida no puede ser anterior a la de ingreso.");
        }
        return errores;
    }
}
