package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.DTOs.ConfiguracionMonedaDTO;
import com.inventario.repository.ConfiguracionMonedaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionMonedaRepositoryImpl implements ConfiguracionMonedaRepository {

    @Override
    public ConfiguracionMonedaDTO obtenerConfiguracion() {
        String sql = "SELECT id, simbolo_moneda, separador_miles, separador_decimal FROM configuracion_moneda LIMIT 1";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new ConfiguracionMonedaDTO(
                        rs.getInt("id"),
                        rs.getString("simbolo_moneda"),
                        rs.getString("separador_miles"),
                        rs.getString("separador_decimal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Valor por defecto si la base de datos está vacía
        return new ConfiguracionMonedaDTO(1, "$", ",", ".");
    }

    @Override
    public boolean guardarOActualizarDTO(ConfiguracionMonedaDTO dto) {
        String sql = "INSERT INTO configuracion_moneda (id, simbolo_moneda, separador_miles, separador_decimal) "
                + "VALUES (1, ?, ?, ?) "
                + "ON CONFLICT (id) DO UPDATE SET "
                + "simbolo_moneda = EXCLUDED.simbolo_moneda, "
                + "separador_miles = EXCLUDED.separador_miles, "
                + "separador_decimal = EXCLUDED.separador_decimal";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getSimboloMoneda());
            stmt.setString(2, dto.getSeparadorMiles());
            stmt.setString(3, dto.getSeparadorDecimal());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
