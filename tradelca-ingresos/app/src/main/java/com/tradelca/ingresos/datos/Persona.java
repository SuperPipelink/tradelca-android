package com.tradelca.ingresos.datos;

/** Persona de Tradelca que se puede visitar (visitado_opciones de la API). */
public final class Persona {

    public final long id;
    public final String nombre;

    public Persona(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;   // lo que muestra el Spinner
    }
}
