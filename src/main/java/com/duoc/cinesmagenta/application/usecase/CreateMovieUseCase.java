package com.duoc.cinesmagenta.application.usecase;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.application.mapper.MovieMapper;
import com.duoc.cinesmagenta.domain.model.Movie;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;

public class CreateMovieUseCase {
    private final MovieRepository repo;

    public CreateMovieUseCase(MovieRepository repo) { this.repo = repo; }

    public MovieResponse execute(MovieRequest request) {
        Movie movie = MovieMapper.toDomain(request);
        int id = repo.create(movie);
        movie.setId(id);
        return MovieMapper.toResponse(movie);
    }
}