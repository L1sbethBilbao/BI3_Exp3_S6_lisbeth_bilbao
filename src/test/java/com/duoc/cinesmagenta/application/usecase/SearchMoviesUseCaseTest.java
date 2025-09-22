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
class SearchMoviesUseCaseTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private SearchMoviesUseCase useCase;

    @Test
    void search_ok() {
        when(repo.findByTitleLike("Ma")).thenReturn(List.of(
                new Movie(1,"Matrix","Lana",1999,136, Genre.CIENCIA_FICCION)
        ));
        List<MovieResponse> out = useCase.execute("Ma");
        assertEquals(1, out.size());
        assertEquals("Matrix", out.get(0).title);
    }
}