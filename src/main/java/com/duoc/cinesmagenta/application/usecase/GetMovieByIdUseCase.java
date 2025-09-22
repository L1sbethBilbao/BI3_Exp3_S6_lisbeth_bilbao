package com.duoc.cinesmagenta.application.usecase;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.application.mapper.MovieMapper;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;

import java.util.Optional;

public class GetMovieByIdUseCase {
    private final MovieRepository repo;

    public GetMovieByIdUseCase(MovieRepository repo) { this.repo = repo; }

    public Optional<MovieResponse> execute(int id) {
        return repo.findById(id).map(MovieMapper::toResponse);
    }
}