package com.duoc.cinesmagenta.domain.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenreTest {

    @Test
    void valores_en_orden_definido() {
        Genre[] expected = {
                Genre.ACCION, Genre.DRAMA, Genre.COMEDIA, Genre.AVENTURA,
                Genre.ANIMACION, Genre.CIENCIA_FICCION, Genre.TERROR, Genre.ROMANCE
        };
        assertArrayEquals(expected, Genre.values());
    }

    @Test
    void valueOf_ok_y_toString_name_iguales() {
        assertEquals(Genre.ACCION, Genre.valueOf("ACCION"));
        // Enum.toString() por defecto devuelve name()
        assertEquals("DRAMA", Genre.DRAMA.toString());
        assertEquals("DRAMA", Genre.DRAMA.name());
    }

    @Test
    void valueOf_invalido_lanza() {
        assertThrows(IllegalArgumentException.class, () -> Genre.valueOf("SCI-FI"));
        assertThrows(IllegalArgumentException.class, () -> Genre.valueOf("ciencia_ficcion")); // case-sensitive
    }

    @Test
    void conjunto_completo_y_tamano() {
        var all = EnumSet.allOf(Genre.class);
        assertEquals(8, all.size());
        // una muestra rápida de pertenencia
        all.contains(Genre.CIENCIA_FICCION);
        all.contains(Genre.ROMANCE);
    }
}