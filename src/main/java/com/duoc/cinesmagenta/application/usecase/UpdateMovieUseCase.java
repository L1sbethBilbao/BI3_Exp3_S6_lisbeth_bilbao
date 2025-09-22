package com.duoc.cinesmagenta.application.usecase;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.mapper.MovieMapper;
import com.duoc.cinesmagenta.domain.model.Movie;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;

public class UpdateMovieUseCase {
    private final MovieRepository repo;

    public UpdateMovieUseCase(MovieRepository repo) { this.repo = repo; }

    public boolean execute(MovieRequest request) {
        if (request.id == null) throw new IllegalArgumentException("Id requerido para actualizar");
        Movie movie = MovieMapper.toDomain(request);
        return repo.update(movie);
    }
}