package com.duoc.cinesmagenta.presentation.view;

import com.duoc.cinesmagenta.application.dto.MovieResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieTableModelTest {

    @Test
    void setData_y_edicion_marcan_dirty_ok() {
        MovieTableModel m = new MovieTableModel();
        MovieResponse r = new MovieResponse();
        r.id = 1; r.title = "A"; r.director = "X"; r.year = 2000; r.durationMinutes = 90; r.genre = "DRAMA";
        m.setData(List.of(r));

        assertEquals(1, m.getRowCount());
        assertEquals("A", m.getValueAt(0, 1));

        m.setValueAt("B", 0, 1); // cambia título
        assertEquals("B", m.getValueAt(0, 1));
        assertTrue(m.dirtyRows().contains(0));
    }
}