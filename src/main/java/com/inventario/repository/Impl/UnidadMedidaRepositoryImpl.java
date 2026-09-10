package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.DTOs.UnidadMedidaDTO;
import com.inventario.repository.UnidadMedidaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnidadMedidaRepositoryImpl implements UnidadMedidaRepository {

    @Override
    public List<UnidadMedidaDTO> obtenerTodasDTO() {
        List<UnidadMedidaDTO> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, clave, activo, predeterminado FROM unidad_medida ORDER BY id ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new UnidadMedidaDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("clave"),
                        rs.getBoolean("activo"),
                        rs.getBoolean("predeterminado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<UnidadMedidaDTO> obtenerActivasDTO() {
        List<UnidadMedidaDTO> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, clave, activo, predeterminado FROM unidad_medida WHERE activo = TRUE ORDER BY id ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new UnidadMedidaDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("clave"),
                        rs.getBoolean("activo"),
                        rs.getBoolean("predeterminado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizarEstadoDTO(int id, boolean activo) {
        String sql = "UPDATE unidad_medida SET activo = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarOActualizarDTO(UnidadMedidaDTO dto) {
        String sql = "INSERT INTO unidad_medida (nombre, clave, activo, predeterminado) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT (clave) DO UPDATE SET "
                + "nombre = EXCLUDED.nombre, activo = EXCLUDED.activo, predeterminado = EXCLUDED.predeterminado";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getNombre());
            ps.setString(2, dto.getClave());
            ps.setBoolean(3, dto.isActivo());
            ps.setBoolean(4, dto.isPredeterminado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
