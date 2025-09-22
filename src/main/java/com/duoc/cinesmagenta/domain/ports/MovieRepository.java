package com.duoc.cinesmagenta.domain.ports;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.domain.model.Movie;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    int create(Movie movie);
    boolean update(Movie movie);
    boolean delete(int id);
    Optional<Movie> findById(int id);
    List<Movie> findAll();
    List<Movie> findByTitleLike(String like);
}