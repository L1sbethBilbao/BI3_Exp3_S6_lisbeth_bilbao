package com.duoc.cinesmagenta.infrastructure.jdbc;

/////Lisbeth_Bilbao_Semana6

import com.duoc.cinesmagenta.domain.model.Genre;
import com.duoc.cinesmagenta.domain.model.Movie;
import com.duoc.cinesmagenta.domain.ports.MovieRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class JdbcMovieRepository implements MovieRepository {

    private final Supplier<Connection> connectionSupplier;

    /** Constructor de producción: usa ConnectionFactory.get() */
    public JdbcMovieRepository() {
        this(() -> {
            try {
                return ConnectionFactory.get();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo obtener la conexión", e);
            }
        });
    }
    /** Constructor para tests: permite inyectar un Supplier<Connection> (mocks). */
    JdbcMovieRepository(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public int create(Movie m) {
        String sql = "INSERT INTO Cartelera (titulo, director, anio, duracion, genero) VALUES (?,?,?,?,?)";
        try (Connection cn = connectionSupplier.get();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("[DAO][CREATE] " + m);

            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDirector());
            ps.setInt(3, m.getYear());
            ps.setInt(4, m.getDurationMinutes());
            ps.setString(5, m.getGenre().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("[DAO][CREATE] OK id=" + id);
                    return id;
                }
            }
            throw new IllegalStateException("No se obtuvo id generado");
        } catch (SQLException se) {
            throw sqlError("CREATE", se);
        } catch (Exception e) {
            throw new RuntimeException("Error creando película: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Movie m) {
        if (m.getId() == null) throw new IllegalArgumentException("Id requerido");
        String sql = "UPDATE Cartelera SET titulo=?, director=?, anio=?, duracion=?, genero=? WHERE id=?";
        try (Connection cn = connectionSupplier.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            System.out.println("[DAO][UPDATE] id=" + m.getId());
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDirector());
            ps.setInt(3, m.getYear());
            ps.setInt(4, m.getDurationMinutes());
            ps.setString(5, m.getGenre().name());
            ps.setInt(6, m.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException se) {
            throw sqlError("UPDATE", se);
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando película: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Cartelera WHERE id=?";
        try (Connection cn = connectionSupplier.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            System.out.println("[DAO][DELETE] id=" + id);
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException se) {
            throw sqlError("DELETE", se);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando película: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Movie> findById(int id) {
        String sql = "SELECT id, titulo, director, anio, duracion, genero FROM Cartelera WHERE id=?";
        try (Connection cn = connectionSupplier.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            System.out.println("[DAO][FIND_BY_ID] id=" + id);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException se) {
            throw sqlError("FIND_BY_ID", se);
        } catch (Exception e) {
            throw new RuntimeException("Error buscando por id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Movie> findAll() {
        String sql = "SELECT id, titulo, director, anio, duracion, genero FROM Cartelera ORDER BY titulo";
        try (Connection cn = connectionSupplier.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("[DAO][FIND_ALL]");
            List<Movie> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException se) {
            throw sqlError("FIND_ALL", se);
        } catch (Exception e) {
            throw new RuntimeException("Error listando: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Movie> findByTitleLike(String like) {
        String sql = "SELECT id, titulo, director, anio, duracion, genero FROM Cartelera WHERE titulo LIKE ? ORDER BY titulo";
        try (Connection cn = connectionSupplier.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            System.out.println("[DAO][SEARCH] like=" + like);
            ps.setString(1, "%" + like + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Movie> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException se) {
            throw sqlError("SEARCH", se);
        } catch (Exception e) {
            throw new RuntimeException("Error buscando por título: " + e.getMessage(), e);
        }
    }

    private Movie map(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("director"),
                rs.getInt("anio"),
                rs.getInt("duracion"),
                Genre.valueOf(rs.getString("genero"))
        );
    }

    // ---- helpers ----
    private RuntimeException sqlError(String op, SQLException se) {
        System.out.println("[DAO][" + op + "] SQLState=" + se.getSQLState()
                + " Code=" + se.getErrorCode()
                + " Msg=" + se.getMessage());

        if (se.getErrorCode() == 1062) {
            return new RuntimeException("Duplicado: ya existe una película con el mismo Título + Director + Año.", se);
        }
        if (se.getErrorCode() == 1146) {
            return new RuntimeException("La tabla 'Cartelera' no existe en la BD.", se);
        }
        if (se.getErrorCode() == 1045) {
            return new RuntimeException("Acceso denegado: revisa usuario/clave o permisos.", se);
        }
        if (se.getErrorCode() == 3819 || "23000".equals(se.getSQLState())) {
            return new RuntimeException("Reglas de integridad/CHK violadas: " + se.getMessage(), se);
        }
        return new RuntimeException("Error SQL (" + se.getErrorCode() + "): " + se.getMessage(), se);
    }
}
