package com.duoc.cinesmagenta.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MovieResponseTest {

    @Test
    void valores_por_defecto_null_o_cero() {
        MovieResponse r = new MovieResponse();

        assertNull(r.id);
        assertNull(r.title);
        assertNull(r.director);
        assertEquals(0, r.year);
        assertEquals(0, r.durationMinutes);
        assertNull(r.genre);
    }

    @Test
    void asignacion_y_lectura_de_campos_ok() {
        MovieResponse r = new MovieResponse();
        r.id = 7;
        r.title = "Matrix";
        r.director = "Lana Wachowski";
        r.year = 1999;
        r.durationMinutes = 136;
        r.genre = "CIENCIA_FICCION";

        assertEquals(7, r.id);
        assertEquals("Matrix", r.title);
        assertEquals("Lana Wachowski", r.director);
        assertEquals(1999, r.year);
        assertEquals(136, r.durationMinutes);
        assertEquals("CIENCIA_FICCION", r.genre);
    }
}