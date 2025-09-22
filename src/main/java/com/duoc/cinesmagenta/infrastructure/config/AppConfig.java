package com.duoc.cinesmagenta.infrastructure.config;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.usecase.CreateMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.DeleteMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.GetMovieByIdUseCase;
import com.duoc.cinesmagenta.application.usecase.ListMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.SearchMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.UpdateMovieUseCase;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import com.duoc.cinesmagenta.infrastructure.jdbc.JdbcMovieRepository;

public class AppConfig {
    private final MovieRepository repo = new JdbcMovieRepository();

    public CreateMovieUseCase createMovie() { return new CreateMovieUseCase(repo); }
    public UpdateMovieUseCase updateMovie() { return new UpdateMovieUseCase(repo); }
    public DeleteMovieUseCase deleteMovie() { return new DeleteMovieUseCase(repo); }
    public GetMovieByIdUseCase getMovieById() { return new GetMovieByIdUseCase(repo); }
    public ListMoviesUseCase listMovies() { return new ListMoviesUseCase(repo); }
    public SearchMoviesUseCase searchMovies() { return new SearchMoviesUseCase(repo); }
}