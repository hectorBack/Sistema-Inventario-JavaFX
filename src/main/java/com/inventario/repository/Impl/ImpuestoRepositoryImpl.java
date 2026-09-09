package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.DTOs.ImpuestoDTO;
import com.inventario.repository.ImpuestoRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImpuestoRepositoryImpl implements ImpuestoRepository {

    @Override
    public List<ImpuestoDTO> obtenerTodosDTO() {
        List<ImpuestoDTO> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, porcentaje, pais, estado, desglosar_ticket, precios_con_impuesto FROM impuesto ORDER BY id";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ImpuestoDTO dto = new ImpuestoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("porcentaje"),
                        rs.getString("pais"),
                        rs.getString("estado"),
                        rs.getBoolean("desglosar_ticket"),
                        rs.getBoolean("precios_con_impuesto")
                );
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public ImpuestoDTO obtenerPorIdDTO(int id) {
        String sql = "SELECT id, nombre, porcentaje, pais, estado, desglosar_ticket, precios_con_impuesto FROM impuesto WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ImpuestoDTO(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDouble("porcentaje"),
                            rs.getString("pais"),
                            rs.getString("estado"),
                            rs.getBoolean("desglosar_ticket"),
                            rs.getBoolean("precios_con_impuesto")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean guardarOActualizarDTO(ImpuestoDTO dto) {
        if (dto.getId() == null || dto.getId() == 0) {
            String sql = "INSERT INTO impuesto (nombre, porcentaje, pais, estado, desglosar_ticket, precios_con_impuesto) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, dto.getNombre());
                stmt.setBigDecimal(2, BigDecimal.valueOf(dto.getPorcentaje()));
                stmt.setString(3, dto.getPais());
                stmt.setString(4, dto.getEstado());
                stmt.setBoolean(5, dto.getDesglosarTicket() != null ? dto.getDesglosarTicket() : false);
                stmt.setBoolean(6, dto.getPreciosConImpuesto() != null ? dto.getPreciosConImpuesto() : false);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            dto.setId(rs.getInt(1));
                        }
                    }
                }
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "UPDATE impuesto SET nombre = ?, porcentaje = ?, pais = ?, estado = ?, desglosar_ticket = ?, precios_con_impuesto = ? WHERE id = ?";

            try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, dto.getNombre());
                stmt.setBigDecimal(2, BigDecimal.valueOf(dto.getPorcentaje()));
                stmt.setString(3, dto.getPais());
                stmt.setString(4, dto.getEstado());
                stmt.setBoolean(5, dto.getDesglosarTicket() != null ? dto.getDesglosarTicket() : false);
                stmt.setBoolean(6, dto.getPreciosConImpuesto() != null ? dto.getPreciosConImpuesto() : false);
                stmt.setInt(7, dto.getId());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public int aplicarImpuestoAProductos(double porcentaje) {
        String sql = "UPDATE productos SET precio = ROUND(CAST(precio * (1 + ? / 100) AS numeric), 2), "
            + "precio_mayoreo = ROUND(CAST(precio_mayoreo * (1 + ? / 100) AS numeric), 2) "
            + "WHERE LOWER(estado) = 'activo'";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, porcentaje);
            ps.setDouble(2, porcentaje);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean eliminarDTO(int id) {
        String sql = "UPDATE impuesto SET estado = 'INACTIVO' WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
