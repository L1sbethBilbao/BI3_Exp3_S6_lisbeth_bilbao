package com.duoc.cinesmagenta.domain.model;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovieTest {

    @Test
    void crea_valida_ok() {
        assertDoesNotThrow(() ->
                new Movie(null, "Matrix", "Lana Wachowski", 1999, 136, Genre.CIENCIA_FICCION)
        );
    }

    @Test
    void titulo_vacio_lanza() {
        assertThrows(IllegalArgumentException.class, () ->
                new Movie(null, " ", "Lana Wachowski", 1999, 136, Genre.ACCION)
        );
    }

    @Test
    void anio_futuro_lanza() {
        int next = java.time.Year.now().getValue() + 1;
        assertThrows(IllegalArgumentException.class, () ->
                new Movie(null, "Futuro", "Ana Perez", next, 100, Genre.DRAMA)
        );
    }

    @Test
    void duracion_cero_lanza() {
        assertThrows(IllegalArgumentException.class, () ->
                new Movie(null, "Corto", "N Du", 2020, 0, Genre.ANIMACION)
        );
    }

    @Test
    void genero_null_lanza() {
        assertThrows(IllegalArgumentException.class, () ->
                new Movie(null, "Sin genero", "Pepe", 2000, 90, null)
        );
    }
}