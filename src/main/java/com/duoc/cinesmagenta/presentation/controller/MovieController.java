package com.duoc.cinesmagenta.presentation.controller;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.application.usecase.CreateMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.DeleteMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.GetMovieByIdUseCase;
import com.duoc.cinesmagenta.application.usecase.ListMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.SearchMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.UpdateMovieUseCase;

import java.util.List;

public class MovieController {
    private final CreateMovieUseCase createUC;
    private final UpdateMovieUseCase updateUC;
    private final DeleteMovieUseCase deleteUC;
    private final GetMovieByIdUseCase getByIdUC;
    private final ListMoviesUseCase listUC;
    private final SearchMoviesUseCase searchUC;

    public MovieController(CreateMovieUseCase c, UpdateMovieUseCase u, DeleteMovieUseCase d,
                           GetMovieByIdUseCase g, ListMoviesUseCase l, SearchMoviesUseCase s) {
        this.createUC = c; this.updateUC = u; this.deleteUC = d;
        this.getByIdUC = g; this.listUC = l; this.searchUC = s;
    }

    public MovieResponse create(MovieRequest req) { return createUC.execute(req); }
    public boolean update(MovieRequest req) { return updateUC.execute(req); }
    public boolean delete(int id) { return deleteUC.execute(id); }
    public MovieResponse getById(int id) { return getByIdUC.execute(id).orElse(null); }
    public List<MovieResponse> listAll() { return listUC.execute(); }
    public List<MovieResponse> search(String like) { return searchUC.execute(like); }
}