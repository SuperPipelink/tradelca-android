package com.tradelca.hola;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SaludoTest {

    @Test
    public void nombreVacioSaludaAlAprendiz() {
        assertEquals("aprendiz", Saludo.nombreParaSaludar("   "));
        assertEquals("aprendiz", Saludo.nombreParaSaludar(null));
    }

    @Test
    public void quitaEspaciosDelNombre() {
        assertEquals("Felipe", Saludo.nombreParaSaludar("  Felipe "));
    }
}
