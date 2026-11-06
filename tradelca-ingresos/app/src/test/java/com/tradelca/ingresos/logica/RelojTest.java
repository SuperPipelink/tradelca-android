package com.tradelca.ingresos.logica;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class RelojTest {

    @Test
    public void usaLaHoraDeBogota() {
        // 2026-11-06 03:30 UTC es 2026-11-05 22:30 en Bogotá (UTC-5)
        Date momento = new Date(1793935800000L);
        assertEquals("2026-11-05", Reloj.hoy(momento));
        assertEquals("22:30", Reloj.hora(momento));
    }
}
