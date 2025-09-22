package com.duoc.cinesmagenta.infrastructure.jdbc;

///// Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JdbcMovieRepositoryTest {

    // ------------------------------------------------------------
    // CONSTRUCTOR "DE PRODUCCIÓN" (cubre el lambda sin tocar BD real)
    // ------------------------------------------------------------
    @Test
    void constructor_produccion_cubre_lambda_usando_mockStatic_de_ConnectionFactory() throws Exception {
        // mockear ConnectionFactory.get() para no abrir conexión real
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement ps = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            cf.when(ConnectionFactory::get).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false); // sin filas

            JdbcMovieRepository repo = new JdbcMovieRepository(); // usa el ctor de producción
            List<Movie> out = repo.findAll(); // ejecuta el supplier.get() dentro del lambda

            assertNotNull(out);
            assertTrue(out.isEmpty());
        }
    }

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------
    @Test
    void create_devuelveId_generado() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(42);

        MovieRepository repo = new JdbcMovieRepository(() -> conn);
        int id = repo.create(new Movie(null, "Matrix", "Lana Wachowski", 1999, 136, Genre.CIENCIA_FICCION));

        assertEquals(42, id);
        verify(ps).executeUpdate();
        verify(keys).getInt(1);
    }

    @Test
    void create_sinGeneratedKey_lanzaRuntimeConCauseIllegalState() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(false); // no hay key

        MovieRepository repo = new JdbcMovieRepository(() -> conn);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.create(new Movie(null, "X", "Y", 2000, 100, Genre.ACCION))
        );
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }

    @Test
    void create_sqlErrorDuplicado_1062_traducido() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Duplicate entry", "23000", 1062));

        MovieRepository repo = new JdbcMovieRepository(() -> conn);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.create(new Movie(null, "Matrix", "Lana", 1999, 136, Genre.CIENCIA_FICCION))
        );
        assertTrue(ex.getMessage().startsWith("Duplicado"), ex.getMessage());
    }

    @Test
    void create_sqlErrorDesconocido_mapeaDefault() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("X", "99999", 9999));

        MovieRepository repo = new JdbcMovieRepository(() -> conn);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.create(new Movie(null, "A", "B", 2001, 101, Genre.DRAMA))
        );
        assertTrue(ex.getMessage().startsWith("Error SQL (9999)"));
    }

    @Test
    void create_supplierLanzaRuntime_caeEnCatchGenericoDeCreate() {
        Supplier<Connection> badSupplier = () -> { throw new RuntimeException("boom"); };
        MovieRepository repo = new JdbcMovieRepository(badSupplier);
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.create(new Movie(null, "A", "B", 2001, 101, Genre.DRAMA))
        );
        assertTrue(ex.getMessage().startsWith("Error creando película"));
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------
    @Test
    void update_ok_retornaTrue() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        assertTrue(repo.update(new Movie(5, "X", "Y", 2001, 101, Genre.DRAMA)));
    }

    @Test
    void update_idNull_lanzaIAE() {
        JdbcMovieRepository repo = new JdbcMovieRepository(() -> mock(Connection.class));
        assertThrows(IllegalArgumentException.class, () ->
                repo.update(new Movie(null, "X", "Y", 2001, 101, Genre.DRAMA))
        );
    }

    @Test
    void update_ceroFilas_retornaFalse() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        assertFalse(repo.update(new Movie(5, "X", "Y", 2001, 101, Genre.DRAMA)));
    }

    @Test
    void update_sqlError23000_chk_traducido() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("CHK violation", "23000", 3819));

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.update(new Movie(5, "X", "Y", 2001, 101, Genre.DRAMA))
        );
        assertTrue(ex.getMessage().startsWith("Reglas de integridad/CHK"));
    }

    @Test
    void update_supplierLanzaRuntime_caeEnCatchGenerico() {
        JdbcMovieRepository repo = new JdbcMovieRepository(() -> { throw new RuntimeException("boom"); });
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                repo.update(new Movie(1, "A", "B", 2000, 100, Genre.ACCION))
        );
        assertTrue(ex.getMessage().startsWith("Error actualizando película"));
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @Test
    void delete_ok_retornaTrue() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        assertTrue(repo.delete(99));
    }

    @Test
    void delete_ceroFilas_retornaFalse() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        assertFalse(repo.delete(99));
    }

    @Test
    void delete_sqlError1045_accesoDenegado_traducido() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Access denied", "28000", 1045));

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.delete(1));
        assertTrue(ex.getMessage().startsWith("Acceso denegado"));
    }

    @Test
    void delete_sqlErrorDesconocido_default() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("X", "YYYYY", 7777));

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.delete(1));
        assertTrue(ex.getMessage().startsWith("Error SQL (7777)"));
    }

    @Test
    void delete_supplierLanzaRuntime_caeEnCatchGenerico() {
        JdbcMovieRepository repo = new JdbcMovieRepository(() -> { throw new RuntimeException("boom"); });
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.delete(1));
        assertTrue(ex.getMessage().startsWith("Error eliminando película"));
    }

    // ------------------------------------------------------------
    // FIND BY ID
    // ------------------------------------------------------------
    @Test
    void findById_retornaMovie() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("id")).thenReturn(7);
        when(rs.getString("titulo")).thenReturn("Matrix");
        when(rs.getString("director")).thenReturn("Lana Wachowski");
        when(rs.getInt("anio")).thenReturn(1999);
        when(rs.getInt("duracion")).thenReturn(136);
        when(rs.getString("genero")).thenReturn(Genre.CIENCIA_FICCION.name());

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);

        Optional<Movie> found = repo.findById(7);
        assertTrue(found.isPresent());
        assertEquals("Matrix", found.get().getTitle());
        assertEquals(Genre.CIENCIA_FICCION, found.get().getGenre());
    }

    @Test
    void findById_noExiste_devuelveEmpty() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        assertTrue(repo.findById(123).isEmpty());
    }

    @Test
    void findById_generoInvalido_caeEnCatchGenerico() throws Exception {
        // valueOf lanzará IllegalArgumentException -> catch(Exception e) del método
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("titulo")).thenReturn("X");
        when(rs.getString("director")).thenReturn("Y");
        when(rs.getInt("anio")).thenReturn(2000);
        when(rs.getInt("duracion")).thenReturn(100);
        when(rs.getString("genero")).thenReturn("NO_EXISTE"); // fuerza IllegalArgumentException

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findById(1));
        assertTrue(ex.getMessage().startsWith("Error buscando por id"));
    }

    @Test
    void findById_supplierLanzaRuntime_caeEnCatchGenerico() {
        JdbcMovieRepository repo = new JdbcMovieRepository(() -> { throw new RuntimeException("boom"); });
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findById(1));
        assertTrue(ex.getMessage().startsWith("Error buscando por id"));
    }

    // ------------------------------------------------------------
    // FIND ALL
    // ------------------------------------------------------------
    @Test
    void findAll_devuelveDosFilas() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);

        when(rs.getInt("id")).thenReturn(1, 2);
        when(rs.getString("titulo")).thenReturn("A", "B");
        when(rs.getString("director")).thenReturn("Dir A", "Dir B");
        when(rs.getInt("anio")).thenReturn(2000, 2001);
        when(rs.getInt("duracion")).thenReturn(90, 100);
        when(rs.getString("genero")).thenReturn(Genre.ACCION.name(), Genre.DRAMA.name());

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        List<Movie> all = repo.findAll();

        assertEquals(2, all.size());
        assertEquals("A", all.get(0).getTitle());
        assertEquals("B", all.get(1).getTitle());
    }

    @Test
    void findAll_sqlError_1146_tablaNoExiste_traducido() throws Exception {
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Table 'Cartelera' doesn't exist", "42S02", 1146));

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);

        RuntimeException ex = assertThrows(RuntimeException.class, repo::findAll);
        assertTrue(ex.getMessage().contains("La tabla 'Cartelera' no existe"), ex.getMessage());
    }

    @Test
    void findAll_supplierLanzaRuntime_caeEnCatchGenerico() {
        JdbcMovieRepository repo = new JdbcMovieRepository(() -> { throw new RuntimeException("boom"); });
        RuntimeException ex = assertThrows(RuntimeException.class, repo::findAll);
        assertTrue(ex.getMessage().startsWith("Error listando"));
    }

    // ------------------------------------------------------------
    // FIND BY TITLE LIKE
    // ------------------------------------------------------------
    @Test
    void findByTitleLike_ok() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);

        when(rs.getInt("id")).thenReturn(10);
        when(rs.getString("titulo")).thenReturn("Misión Polar");
        when(rs.getString("director")).thenReturn("C. Ruiz");
        when(rs.getInt("anio")).thenReturn(2023);
        when(rs.getInt("duracion")).thenReturn(129);
        when(rs.getString("genero")).thenReturn(Genre.ACCION.name());

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);

        List<Movie> found = repo.findByTitleLike("Misi");
        assertEquals(1, found.size());
        assertEquals("Misión Polar", found.get(0).getTitle());
    }

    @Test
    void findByTitleLike_sinResultados_devuelveListaVacia() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        JdbcMovieRepository repo = new JdbcMovieRepository(() -> conn);
        List<Movie> found = repo.findByTitleLike("zzz");
        assertNotNull(found);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByTitleLike_supplierLanzaRuntime_caeEnCatchGenerico() {
        JdbcMovieRepository repo = new JdbcMovieRepository(() -> { throw new RuntimeException("boom"); });
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findByTitleLike("abc"));
        assertTrue(ex.getMessage().startsWith("Error buscando por título"));
    }
}
