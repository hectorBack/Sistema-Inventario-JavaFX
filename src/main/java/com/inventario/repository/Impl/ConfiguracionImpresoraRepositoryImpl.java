package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.ConfiguracionImpresora;
import com.inventario.model.DTOs.ConfiguracionImpresoraDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.ConfiguracionImpresoraRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionImpresoraRepositoryImpl implements ConfiguracionImpresoraRepository {

    @Override
    public ConfiguracionImpresoraDTO obtenerConfiguracion() {
        String sql = "SELECT id, nombre_impresora, fuente, tamano_fuente, columnas, "
                + "usar_fuente_normal_totales, todas_negritas FROM configuracion_impresora LIMIT 1";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                // Instanciación directa enviando los parámetros al constructor definido
                ConfiguracionImpresora config = new ConfiguracionImpresora(
                        rs.getInt("id"),
                        rs.getString("nombre_impresora"),
                        rs.getString("fuente"),
                        rs.getInt("tamano_fuente"),
                        rs.getInt("columnas"),
                        rs.getBoolean("usar_fuente_normal_totales"),
                        rs.getBoolean("todas_negritas")
                );

                return DTOMapper.toDTO(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean guardarOActualizar(ConfiguracionImpresoraDTO dto) {
        ConfiguracionImpresoraDTO configExistente = obtenerConfiguracion();

        if (configExistente == null) {
            String sqlInsert = "INSERT INTO configuracion_impresora (nombre_impresora, fuente, tamano_fuente, "
                    + "columnas, usar_fuente_normal_totales, todas_negritas) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sqlInsert)) {

                ps.setString(1, dto.getNombreImpresora());
                ps.setString(2, dto.getFuente());
                ps.setInt(3, dto.getTamanoFuente());
                ps.setInt(4, dto.getColumnas());
                ps.setBoolean(5, dto.isUsarFuenteNormalTotales());
                ps.setBoolean(6, dto.isTodasNegritas());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sqlUpdate = "UPDATE configuracion_impresora SET nombre_impresora = ?, fuente = ?, "
                    + "tamano_fuente = ?, columnas = ?, usar_fuente_normal_totales = ?, "
                    + "todas_negritas = ? WHERE id = ?";
            try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

                ps.setString(1, dto.getNombreImpresora());
                ps.setString(2, dto.getFuente());
                ps.setInt(3, dto.getTamanoFuente());
                ps.setInt(4, dto.getColumnas());
                ps.setBoolean(5, dto.isUsarFuenteNormalTotales());
                ps.setBoolean(6, dto.isTodasNegritas());
                ps.setInt(7, configExistente.getId());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
