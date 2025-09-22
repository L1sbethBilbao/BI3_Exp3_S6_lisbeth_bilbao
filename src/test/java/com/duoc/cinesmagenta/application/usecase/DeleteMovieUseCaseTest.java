package com.duoc.cinesmagenta.application.usecase;

import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteMovieUseCaseTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private DeleteMovieUseCase useCase;

    @Test
    void delete_ok_devuelveTrue_y_llamaRepositorioUnaVez() {
        when(repo.delete(5)).thenReturn(true);

        boolean result = useCase.execute(5);

        assertTrue(result);
        verify(repo, times(1)).delete(5);
    }

    @Test
    void delete_noExiste_devuelveFalse() {
        when(repo.delete(999)).thenReturn(false);

        boolean result = useCase.execute(999);

        assertFalse(result);
        verify(repo, times(1)).delete(999);
    }
}