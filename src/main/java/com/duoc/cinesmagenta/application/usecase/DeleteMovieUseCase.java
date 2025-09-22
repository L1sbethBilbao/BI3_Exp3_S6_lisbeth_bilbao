package com.duoc.cinesmagenta.application.usecase;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.domain.ports.MovieRepository;

public class DeleteMovieUseCase {
    private final MovieRepository repo;

    public DeleteMovieUseCase(MovieRepository repo) { this.repo = repo; }

    public boolean execute(int id) {
        return repo.delete(id);
    }
}