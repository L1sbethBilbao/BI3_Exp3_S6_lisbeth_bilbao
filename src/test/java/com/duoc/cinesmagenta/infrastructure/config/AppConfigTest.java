package com.duoc.cinesmagenta.infrastructure.config;

import com.duoc.cinesmagenta.application.usecase.CreateMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.DeleteMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.GetMovieByIdUseCase;
import com.duoc.cinesmagenta.application.usecase.ListMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.SearchMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.UpdateMovieUseCase;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import com.duoc.cinesmagenta.infrastructure.jdbc.JdbcMovieRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

    @Test
    void wiringComparteUnSoloRepositorioJdbc() throws Exception {
        AppConfig cfg = new AppConfig();

        CreateMovieUseCase create = cfg.createMovie();
        UpdateMovieUseCase update = cfg.updateMovie();
        DeleteMovieUseCase delete = cfg.deleteMovie();
        GetMovieByIdUseCase get   = cfg.getMovieById();
        ListMoviesUseCase list    = cfg.listMovies();
        SearchMoviesUseCase search= cfg.searchMovies();

        // Instancias no nulas
        assertNotNull(create);
        assertNotNull(update);
        assertNotNull(delete);
        assertNotNull(get);
        assertNotNull(list);
        assertNotNull(search);

        // Extraer el 'repo' privado por reflexión
        MovieRepository rCreate = readRepo(create);
        MovieRepository rUpdate = readRepo(update);
        MovieRepository rDelete = readRepo(delete);
        MovieRepository rGet    = readRepo(get);
        MovieRepository rList   = readRepo(list);
        MovieRepository rSearch = readRepo(search);

        // Todos comparten el mismo repo (misma referencia)
        assertSame(rCreate, rUpdate);
        assertSame(rCreate, rDelete);
        assertSame(rCreate, rGet);
        assertSame(rCreate, rList);
        assertSame(rCreate, rSearch);

        // Y ese repo es la implementación JDBC
        assertTrue(rCreate instanceof JdbcMovieRepository);
    }

    private static MovieRepository readRepo(Object useCase) throws Exception {
        Field f = useCase.getClass().getDeclaredField("repo");
        f.setAccessible(true);
        return (MovieRepository) f.get(useCase);
    }
}