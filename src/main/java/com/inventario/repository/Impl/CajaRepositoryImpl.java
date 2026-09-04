package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Caja;
import com.inventario.model.DTOs.CajaDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.CajaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CajaRepositoryImpl implements CajaRepository {

    @Override
    public List<CajaDTO> obtenerTodasDTO() {
        List<CajaDTO> resultado = new ArrayList<>();
        for (Caja caja : obtenerTodas()) {
            resultado.add(DTOMapper.toDTO(caja));
        }
        return resultado;
    }

    @Override
    public Optional<CajaDTO> obtenerPorIdDTO(int id) {
        return obtenerPorId(id).map(DTOMapper::toDTO);
    }

    @Override
    public boolean guardarDTO(CajaDTO caja) {
        return guardar(DTOMapper.toModel(caja));
    }

    @Override
    public boolean actualizarDTO(CajaDTO caja) {
        return actualizar(DTOMapper.toModel(caja));
    }

    @Override
    public List<Caja> obtenerTodas() {
        List<Caja> cajas = new ArrayList<>();
        String sql = "SELECT id, nombre, estado, tipo, caja_padre_id, fecha_ultimo_acceso FROM cajas ORDER BY id ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cajas.add(mapearCaja(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cajas;
    }

    @Override
    public Optional<Caja> obtenerPorId(int id) {
        String sql = "SELECT id, nombre, estado, tipo, caja_padre_id, fecha_ultimo_acceso FROM cajas ORDER BY id ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCaja(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean guardar(Caja caja) {
        String sql = "INSERT INTO cajas (nombre, estado, tipo, caja_padre_id, fecha_ultimo_acceso) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, caja.getNombre());
            stmt.setString(2, caja.getEstado());
            stmt.setString(3, caja.getTipo());

            if (caja.getCajaPadreId() != null) {
                stmt.setInt(4, caja.getCajaPadreId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setTimestamp(5, caja.getFechaUltimoAcceso() != null ? Timestamp.valueOf(caja.getFechaUltimoAcceso()) : Timestamp.valueOf(java.time.LocalDateTime.now()));

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        caja.setId(keys.getInt(1));
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
    public boolean actualizar(Caja caja) {
        String sql = "UPDATE cajas SET nombre = ?, estado = ?, tipo = ?, caja_padre_id = ?, fecha_ultimo_acceso = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caja.getNombre());
            stmt.setString(2, caja.getEstado());
            stmt.setString(3, caja.getTipo());

            if (caja.getCajaPadreId() != null) {
                stmt.setInt(4, caja.getCajaPadreId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setTimestamp(5, caja.getFechaUltimoAcceso() != null ? Timestamp.valueOf(caja.getFechaUltimoAcceso()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(6, caja.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM cajas WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Caja mapearCaja(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fecha_ultimo_acceso");

        // Si caja_padre_id es NULL en la BD, wasNull() ayuda a manejarlo correctamente como Integer
        int padreIdRaw = rs.getInt("caja_padre_id");
        Integer cajaPadreId = rs.wasNull() ? null : padreIdRaw;

        return new Caja(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("estado"),
                rs.getString("tipo"),
                cajaPadreId,
                ts != null ? ts.toLocalDateTime() : null
        );
    }

}
