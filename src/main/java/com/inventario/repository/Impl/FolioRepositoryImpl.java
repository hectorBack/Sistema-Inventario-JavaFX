package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Folio;
import com.inventario.repository.FolioRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FolioRepositoryImpl implements FolioRepository {

    @Override
    public List<Folio> obtenerTodos() {
        List<Folio> folios = new ArrayList<>();
        String sql = "SELECT id, nombre, modulo, serie, folio_actual, longitud_ceros, estado FROM folios";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                folios.add(mapearFolio(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folios;
    }

    @Override
    public Optional<Folio> obtenerPorId(int id) {
        String sql = "SELECT id, nombre, modulo, serie, folio_actual, longitud_ceros, estado FROM folios WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFolio(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Folio> obtenerPorModulo(String modulo) {
        String sql = "SELECT id, nombre, modulo, serie, folio_actual, longitud_ceros, estado FROM folios WHERE modulo = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modulo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFolio(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean guardar(Folio folio) {
        String sql = "INSERT INTO folios (nombre, modulo, serie, folio_actual, longitud_ceros, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, folio.getNombre());
            stmt.setString(2, folio.getModulo());
            stmt.setString(3, folio.getSerie());
            stmt.setInt(4, folio.getFolioActual());
            stmt.setInt(5, folio.getLongitudCeros());
            stmt.setString(6, folio.getEstado());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        folio.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean actualizar(Folio folio) {
        String sql = "UPDATE folios SET nombre = ?, modulo = ?, serie = ?, folio_actual = ?, longitud_ceros = ?, estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, folio.getNombre());
            stmt.setString(2, folio.getModulo());
            stmt.setString(3, folio.getSerie());
            stmt.setInt(4, folio.getFolioActual());
            stmt.setInt(5, folio.getLongitudCeros());
            stmt.setString(6, folio.getEstado());
            stmt.setInt(7, folio.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM folios WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean incrementarFolio(String modulo) {
        String sql = "UPDATE folios SET folio_actual = folio_actual + 1 WHERE modulo = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modulo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapea la fila del ResultSet al objeto Folio utilizando sus propiedades JavaFX
    private Folio mapearFolio(ResultSet rs) throws SQLException {
        return new Folio(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("modulo"),
                rs.getString("serie"),
                rs.getInt("folio_actual"),
                rs.getInt("longitud_ceros"),
                rs.getString("estado")
        );
    }

}
