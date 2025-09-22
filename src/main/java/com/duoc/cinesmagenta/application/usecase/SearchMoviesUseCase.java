package com.duoc.cinesmagenta.application.usecase;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.application.mapper.MovieMapper;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;

import java.util.List;
import java.util.stream.Collectors;

public class SearchMoviesUseCase {
    private final MovieRepository repo;

    public SearchMoviesUseCase(MovieRepository repo) { this.repo = repo; }

    public List<MovieResponse> execute(String like) {
        return repo.findByTitleLike(like).stream()
                .map(MovieMapper::toResponse)
                .collect(Collectors.toList());
    }
}