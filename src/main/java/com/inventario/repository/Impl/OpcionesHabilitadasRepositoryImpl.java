package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.OpcionesHabilitadas;
import com.inventario.repository.OpcionesHabilitadasRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OpcionesHabilitadasRepositoryImpl implements OpcionesHabilitadasRepository {

    @Override
    public OpcionesHabilitadas obtenerOpciones() {
        String sql = "SELECT * FROM opciones_habilitadas WHERE id = 1";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new OpcionesHabilitadas(
                        rs.getInt("id"),
                        rs.getBoolean("usar_inventario"),
                        rs.getBoolean("ofrecer_credito"),
                        rs.getBoolean("producto_comun"),
                        rs.getBoolean("calcular_precio"),
                        rs.getDouble("margen_ganancia"),
                        rs.getBoolean("habilitar_redondeo"),
                        rs.getString("tipo_redondeo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar opciones_habilitadas: " + e.getMessage());
            e.printStackTrace();
        }

        return new OpcionesHabilitadas(); // Si está vacía, devuelve valores por defecto
    }

    @Override
    public boolean guardarOActualizar(OpcionesHabilitadas opciones) {
        String sql = "INSERT INTO opciones_habilitadas (id, usar_inventario, ofrecer_credito, producto_comun, calcular_precio, margen_ganancia, habilitar_redondeo, tipo_redondeo) "
                + "VALUES (1, ?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (id) DO UPDATE SET "
                + "usar_inventario = EXCLUDED.usar_inventario, "
                + "ofrecer_credito = EXCLUDED.ofrecer_credito, "
                + "producto_comun = EXCLUDED.producto_comun, "
                + "calcular_precio = EXCLUDED.calcular_precio, "
                + "margen_ganancia = EXCLUDED.margen_ganancia, "
                + "habilitar_redondeo = EXCLUDED.habilitar_redondeo, "
                + "tipo_redondeo = EXCLUDED.tipo_redondeo;";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, opciones.isUsarInventario());
            ps.setBoolean(2, opciones.isOfrecerCredito());
            ps.setBoolean(3, opciones.isProductoComun());
            ps.setBoolean(4, opciones.isCalcularPrecio());
            ps.setDouble(5, opciones.getMargenGanancia());
            ps.setBoolean(6, opciones.isHabilitarRedondeo());
            ps.setString(7, opciones.getTipoRedondeo());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar opciones_habilitadas en PostgreSQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
