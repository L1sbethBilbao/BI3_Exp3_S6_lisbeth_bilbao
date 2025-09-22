package com.duoc.cinesmagenta.domain.ports;

import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Contract test de MovieRepository usando una implementación en memoria local al test.
 * Sirve para validar el “contrato” de la interface (crear/actualizar/borrar/listar/buscar + unicidad).
 */
class MovieRepositoryContractTest {

    private MovieRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryMovieRepository(); // implementación solo para tests
    }

    @Test
    void create_y_findById_ok() {
        int id = repo.create(new Movie(null, "Matrix", "Lana Wachowski", 1999, 136, Genre.CIENCIA_FICCION));
        Optional<Movie> loaded = repo.findById(id);
        assertTrue(loaded.isPresent());
        assertEquals("Matrix", loaded.get().getTitle());
        assertEquals(136, loaded.get().getDurationMinutes());
    }

    @Test
    void create_duplicado_misma_clave_lanza() {
        repo.create(new Movie(null, "Matrix", "Lana Wachowski", 1999, 136, Genre.CIENCIA_FICCION));
        var dup = new Movie(null, "Matrix", "Lana Wachowski", 1999, 140, Genre.CIENCIA_FICCION);
        assertThrows(RuntimeException.class, () -> repo.create(dup), "Debe rechazar duplicados por (titulo+director+anio)");
    }

    @Test
    void update_ok_y_refleja_cambios() {
        int id = repo.create(new Movie(null, "Bosque", "A. Gomez", 2020, 90, Genre.DRAMA));
        boolean ok = repo.update(new Movie(id, "Bosque Azul", "A. Gomez", 2020, 95, Genre.DRAMA));
        assertTrue(ok);
        assertEquals("Bosque Azul", repo.findById(id).orElseThrow().getTitle());
        assertEquals(95, repo.findById(id).orElseThrow().getDurationMinutes());
    }

    @Test
    void update_a_duplicado_lanza() {
        int id1 = repo.create(new Movie(null, "A", "X", 2000, 90, Genre.DRAMA));
        int id2 = repo.create(new Movie(null, "B", "Y", 2001, 91, Genre.ACCION));
        // intentar que id2 pase a tener misma clave (titulo+director+anio) que id1
        var dup = new Movie(id2, "A", "X", 2000, 100, Genre.ACCION);
        assertThrows(RuntimeException.class, () -> repo.update(dup));
    }

    @Test
    void delete_ok_y_desaparece() {
        int id = repo.create(new Movie(null, "Temp", "Dir", 2010, 80, Genre.COMEDIA));
        assertTrue(repo.delete(id));
        assertTrue(repo.findById(id).isEmpty());
    }

    @Test
    void findAll_y_findByTitleLike_ok() {
        repo.create(new Movie(null, "Misión Polar", "C. Ruiz", 2023, 129, Genre.ACCION));
        repo.create(new Movie(null, "Bosque", "A. Gómez", 2020, 90, Genre.DRAMA));
        repo.create(new Movie(null, "Mi bosque azul", "Z. Z", 2018, 88, Genre.DRAMA));

        List<Movie> all = repo.findAll();
        assertEquals(3, all.size());

        List<Movie> like1 = repo.findByTitleLike("Misi");
        assertEquals(1, like1.size());
        assertEquals("Misión Polar", like1.get(0).getTitle());

        List<Movie> like2 = repo.findByTitleLike("bosque");
        assertEquals(2, like2.size());
    }

    // ---------------------------------------------------------
    // Implementación en memoria SOLO para este test (no de producción)
    // ---------------------------------------------------------
    private static class InMemoryMovieRepository implements MovieRepository {
        private final Map<Integer, Movie> store = new HashMap<>();
        private final AtomicInteger seq = new AtomicInteger(0);

        @Override
        public int create(Movie movie) {
            // regla de unicidad: titulo+director+anio
            ensureUnique(movie.getTitle(), movie.getDirector(), movie.getYear(), null);
            int id = seq.incrementAndGet();
            movie.setId(id);
            store.put(id, movie);
            return id;
        }

        @Override
        public boolean update(Movie movie) {
            if (movie.getId() == null || !store.containsKey(movie.getId())) return false;
            // no pisar con una clave igual a otra fila
            ensureUnique(movie.getTitle(), movie.getDirector(), movie.getYear(), movie.getId());
            store.put(movie.getId(), movie);
            return true;
        }

        @Override
        public boolean delete(int id) {
            return store.remove(id) != null;
        }

        @Override
        public Optional<Movie> findById(int id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Movie> findAll() {
            List<Movie> list = new ArrayList<>(store.values());
            list.sort(Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER));
            return list;
        }

        @Override
        public List<Movie> findByTitleLike(String like) {
            String needle = like == null ? "" : like.toLowerCase(Locale.ROOT);
            List<Movie> out = new ArrayList<>();
            for (Movie m : store.values()) {
                if (m.getTitle().toLowerCase(Locale.ROOT).contains(needle)) out.add(m);
            }
            out.sort(Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER));
            return out;
        }

        private void ensureUnique(String title, String director, int year, Integer selfId) {
            for (Movie m : store.values()) {
                boolean sameKey =
                        m.getTitle().equals(title) &&
                                m.getDirector().equals(director) &&
                                m.getYear() == year;
                if (sameKey && (selfId == null || !m.getId().equals(selfId))) {
                    throw new RuntimeException("Duplicado por (titulo, director, anio)");
                }
            }
        }
    }
}