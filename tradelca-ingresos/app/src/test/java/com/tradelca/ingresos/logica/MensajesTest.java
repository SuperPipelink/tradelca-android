package com.tradelca.ingresos.logica;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MensajesTest {

    @Test
    public void conflictoDeVersionPideActualizar() {
        assertTrue(Mensajes.porCodigo(409).contains("Actualiza"));
    }

    @Test
    public void erroresDelServidorMuestranElCodigo() {
        assertTrue(Mensajes.porCodigo(503).contains("503"));
    }
}
