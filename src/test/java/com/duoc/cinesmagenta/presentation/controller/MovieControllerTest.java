package com.duoc.cinesmagenta.presentation.controller;

import com.duoc.cinesmagenta.application.dto.MovieRequest;
import com.duoc.cinesmagenta.application.dto.MovieResponse;
import com.duoc.cinesmagenta.application.usecase.CreateMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.DeleteMovieUseCase;
import com.duoc.cinesmagenta.application.usecase.GetMovieByIdUseCase;
import com.duoc.cinesmagenta.application.usecase.ListMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.SearchMoviesUseCase;
import com.duoc.cinesmagenta.application.usecase.UpdateMovieUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock private CreateMovieUseCase createUC;
    @Mock private UpdateMovieUseCase updateUC;
    @Mock private DeleteMovieUseCase deleteUC;
    @Mock private GetMovieByIdUseCase getByIdUC;
    @Mock private ListMoviesUseCase listUC;
    @Mock private SearchMoviesUseCase searchUC;

    private MovieController controller;

    @BeforeEach
    void setUp() {
        controller = new MovieController(createUC, updateUC, deleteUC, getByIdUC, listUC, searchUC);
    }

    @Test
    void create_delega_y_retorna_respuesta() {
        MovieRequest req = new MovieRequest();
        req.title = "Matrix";
        MovieResponse resp = new MovieResponse();
        resp.id = 1; resp.title = "Matrix";

        when(createUC.execute(req)).thenReturn(resp);

        MovieResponse out = controller.create(req);

        assertEquals(1, out.id);
        assertEquals("Matrix", out.title);
        verify(createUC, times(1)).execute(req);
    }

    @Test
    void update_delega_y_retorna_boolean() {
        MovieRequest req = new MovieRequest();
        req.id = 10; req.title = "Editada";

        when(updateUC.execute(req)).thenReturn(true);

        boolean ok = controller.update(req);

        assertTrue(ok);
        verify(updateUC, times(1)).execute(req);
    }

    @Test
    void delete_delega_y_retorna_boolean() {
        when(deleteUC.execute(5)).thenReturn(true);

        boolean ok = controller.delete(5);

        assertTrue(ok);
        verify(deleteUC, times(1)).execute(5);
    }

    @Test
    void getById_cuando_existe_devuelve_response() {
        MovieResponse resp = new MovieResponse();
        resp.id = 7; resp.title = "Bosque";

        when(getByIdUC.execute(7)).thenReturn(Optional.of(resp));

        MovieResponse out = controller.getById(7);

        assertEquals(7, out.id);
        assertEquals("Bosque", out.title);
        verify(getByIdUC, times(1)).execute(7);
    }

    @Test
    void getById_cuando_no_existe_devuelve_null() {
        when(getByIdUC.execute(99)).thenReturn(Optional.empty());

        MovieResponse out = controller.getById(99);

        assertNull(out);
        verify(getByIdUC, times(1)).execute(99);
    }

    @Test
    void listAll_delega_y_retorna_lista() {
        MovieResponse a = new MovieResponse(); a.id = 1; a.title = "A";
        MovieResponse b = new MovieResponse(); b.id = 2; b.title = "B";
        when(listUC.execute()).thenReturn(List.of(a, b));

        List<MovieResponse> out = controller.listAll();

        assertEquals(2, out.size());
        assertEquals("A", out.get(0).title);
        verify(listUC, times(1)).execute();
    }

    @Test
    void search_delega_y_retorna_resultados() {
        MovieResponse r = new MovieResponse(); r.id = 3; r.title = "Matrix";
        when(searchUC.execute("Ma")).thenReturn(List.of(r));

        List<MovieResponse> out = controller.search("Ma");

        assertEquals(1, out.size());
        assertEquals("Matrix", out.get(0).title);
        verify(searchUC, times(1)).execute(ArgumentMatchers.eq("Ma"));
    }
}