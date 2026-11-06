package com.tradelca.ingresos.logica;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/** Las mismas reglas que valida el servidor en nucleo/visitas.py. */
public class ValidadorVisitaTest {

    @Test
    public void visitaCompletaEsValida() {
        assertTrue(ValidadorVisita.validar("Visitante de Prueba", 114L, "09:00", null).isEmpty());
    }

    @Test
    public void nombreVacioOSoloEspaciosNoSirve() {
        assertEquals("El nombre del visitante es obligatorio.", ValidadorVisita.validar("   ", 1L, "09:00", null).get(0));
    }

    @Test
    public void nombreDeMasDe150Caracteres() {
        StringBuilder largo = new StringBuilder();
        for (int i = 0; i < 151; i++) {
            largo.append('A');
        }
        assertFalse(ValidadorVisita.validar(largo.toString(), 1L, "09:00", null).isEmpty());
    }

    @Test
    public void faltaLaPersonaVisitada() {
        List<String> e = ValidadorVisita.validar("Ana", null, "09:00", null);
        assertEquals("Elige la persona visitada.", e.get(0));
    }

    @Test
    public void horasImposiblesSeRechazan() {
        assertFalse(ValidadorVisita.horaValida("25:99"));
        assertFalse(ValidadorVisita.horaValida("24:00"));
        assertFalse(ValidadorVisita.horaValida("abc"));
        assertFalse(ValidadorVisita.horaValida("9:00"));
        assertTrue(ValidadorVisita.horaValida("23:59"));
        assertTrue(ValidadorVisita.horaValida("00:00"));
    }

    @Test
    public void salidaAntesDeLaEntrada() {
        List<String> e = ValidadorVisita.validar("Ana", 1L, "09:00", "08:00");
        assertEquals("La hora de salida no puede ser anterior a la de ingreso.", e.get(0));
    }

    @Test
    public void salidaIgualOPosteriorEsValida() {
        assertTrue(ValidadorVisita.validar("Ana", 1L, "09:00", "09:00").isEmpty());
        assertTrue(ValidadorVisita.validar("Ana", 1L, "09:00", "17:30").isEmpty());
    }

    @Test
    public void minutosDesdeMedianoche() {
        assertEquals(570, ValidadorVisita.minutos("09:30"));
    }
}
