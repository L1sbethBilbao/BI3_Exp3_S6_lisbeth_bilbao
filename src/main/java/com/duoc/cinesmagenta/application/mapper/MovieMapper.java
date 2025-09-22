package com.duoc.cinesmagenta.application.mapper;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;

public class MovieMapper {
    public static Movie toDomain(MovieRequest r) {
        return new Movie(
                r.id,
                r.title,
                r.director,
                r.year,
                r.durationMinutes,
                Genre.valueOf(r.genre) // debe venir exacto al enum
        );
    }

    public static MovieResponse toResponse(Movie m) {
        MovieResponse out = new MovieResponse();
        out.id = m.getId();
        out.title = m.getTitle();
        out.director = m.getDirector();
        out.year = m.getYear();
        out.durationMinutes = m.getDurationMinutes();
        out.genre = m.getGenre().name();
        return out;
    }
}