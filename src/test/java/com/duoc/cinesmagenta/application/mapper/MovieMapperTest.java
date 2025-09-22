package com.duoc.cinesmagenta.application.mapper;

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieMapperTest {

    @Test
    void toDomain_y_toResponse_ok() {
        MovieRequest req = new MovieRequest();
        req.title = "Matrix";
        req.director = "Lana Wachowski";
        req.year = 1999;
        req.durationMinutes = 136;
        req.genre = "CIENCIA_FICCION";

        Movie m = MovieMapper.toDomain(req);
        // valida domain
        assertEquals("Matrix", m.getTitle());
        assertEquals(Genre.CIENCIA_FICCION, m.getGenre());

        MovieResponse resp = MovieMapper.toResponse(m);
        // valida response
        assertEquals("Matrix", resp.title);
        assertEquals(1999, resp.year);
        assertEquals("CIENCIA_FICCION", resp.genre);
    }
}