package com.duoc.cinesmagenta.application.usecase;

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateMovieUseCaseTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private UpdateMovieUseCase useCase;

    @Test
    void update_ok() {
        MovieRequest req = new MovieRequest();
        req.id = 10;
        req.title = "Matrix (Editada)";
        req.director = "Lana Wachowski";
        req.year = 1999;
        req.durationMinutes = 140;
        req.genre = "CIENCIA_FICCION";

        when(repo.update(org.mockito.ArgumentMatchers.any())).thenReturn(true);

        boolean ok = useCase.execute(req);
        assertTrue(ok);
    }
}