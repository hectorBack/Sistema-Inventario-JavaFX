package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Empresa;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.EmpresaDTO;
import com.inventario.repository.EmpresaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EmpresaRepositoryImpl implements EmpresaRepository {

    @Override
    public EmpresaDTO obtenerConfiguracionDTO() {
        return DTOMapper.toDTO(obtenerConfiguracion());
    }

    @Override
    public Empresa obtenerConfiguracion() {
        String sql = "SELECT id, nombre_empresa, rfc, telefono, direccion, logo_path, fecha_actualizacion FROM configuracion_empresa WHERE id = 1";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_actualizacion");
                return new Empresa(
                        rs.getInt("id"),
                        rs.getString("nombre_empresa"),
                        rs.getString("rfc"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("logo_path"),
                        ts != null ? ts.toLocalDateTime() : null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Empresa(1, "Mi Negocio", "", "", "", "", null);
    }

    @Override
    public boolean actualizarLogoPath(String logoPath) {
        String sql = "UPDATE configuracion_empresa SET logo_path = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = 1";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, logoPath);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarConfiguracion(Empresa empresa) {
        String sql = "UPDATE configuracion_empresa SET nombre_empresa = ?, rfc = ?, telefono = ?, direccion = ?, logo_path = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = 1";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNombreEmpresa());
            stmt.setString(2, empresa.getRfc());
            stmt.setString(3, empresa.getTelefono());
            stmt.setString(4, empresa.getDireccion());
            stmt.setString(5, empresa.getLogoPath());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarConfiguracionDTO(EmpresaDTO empresa) {
        return guardarConfiguracion(DTOMapper.toModel(empresa));
    }

}
