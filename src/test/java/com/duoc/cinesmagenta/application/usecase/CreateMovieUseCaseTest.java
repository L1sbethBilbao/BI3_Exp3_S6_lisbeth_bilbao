package com.duoc.cinesmagenta.application.usecase;

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMovieUseCaseTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private CreateMovieUseCase useCase;

    @Test
    void crea_ok_mapea_y_devuelve_id() {
        MovieRequest req = new MovieRequest();
        req.title = "Matrix";
        req.director = "Lana Wachowski";
        req.year = 1999;
        req.durationMinutes = 136;
        req.genre = "CIENCIA_FICCION";

        when(repo.create(any(Movie.class))).thenReturn(42);

        MovieResponse resp = useCase.execute(req);
        assertEquals(42, resp.id);

        ArgumentCaptor<Movie> cap = ArgumentCaptor.forClass(Movie.class);
        verify(repo).create(cap.capture());
        Movie sent = cap.getValue();
        assertEquals("Matrix", sent.getTitle());
        assertEquals(Genre.CIENCIA_FICCION, sent.getGenre());
    }
}