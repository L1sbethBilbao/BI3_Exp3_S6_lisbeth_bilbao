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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListMoviesUseCaseTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private ListMoviesUseCase useCase;

    @Test
    void list_ok() {
        when(repo.findAll()).thenReturn(List.of(
                new Movie(1,"A","X",2000,90, Genre.DRAMA),
                new Movie(2,"B","Y",2010,95, Genre.ACCION)
        ));
        List<MovieResponse> out = useCase.execute();
        assertEquals(2, out.size());
        assertEquals("A", out.get(0).title);
    }
}