package com.duoc.cinesmagenta.application.usecase;

import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMovieByIdUseCaseTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private GetMovieByIdUseCase useCase;

    @Test
    void get_ok() {
        when(repo.findById(7)).thenReturn(
                Optional.of(new Movie(7,"Matrix","Lana Wachowski",1999,136, Genre.CIENCIA_FICCION))
        );
        MovieResponse r = useCase.execute(7).orElseThrow();
        assertEquals(7, r.id);
        assertEquals("Matrix", r.title);
    }
}