package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.MovimientoInventario;
import com.inventario.repository.MovimientoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoRepositoryImpl implements MovimientoRepository {

    @Override
    public List<MovimientoInventario> listarTodos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        // Hacemos un JOIN con productos para jalar el nombre del producto directamente
        String sql = "SELECT m.id, m.producto_id, p.nombre as producto_nombre, m.tipo_movimiento, "
                + "m.cantidad, m.motivo, m.fecha_movimiento "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MovimientoInventario mov = new MovimientoInventario(
                        rs.getInt("id"),
                        rs.getInt("producto_id"),
                        rs.getString("producto_nombre"),
                        rs.getString("tipo_movimiento"),
                        rs.getInt("cantidad"),
                        rs.getString("motivo"),
                        rs.getTimestamp("fecha_movimiento").toLocalDateTime()
                );
                movimientos.add(mov);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar movimientos: " + e.getMessage());
        }
        return movimientos;
    }

    @Override
    public boolean registrarMovimiento(MovimientoInventario movimiento) {
        String sqlInsert = "INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, motivo) VALUES (?, ?, ?, ?)";

        // Query dinámico para modificar el stock real del producto
        String sqlUpdateStock = movimiento.getTipoMovimiento().equals("ENTRADA")
                ? "UPDATE productos SET stock = stock + ? WHERE id = ?"
                : "UPDATE productos SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // Iniciamos Transacción

            // 1. Insertar el registro del movimiento
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setInt(1, movimiento.getProductoId());
                stmtInsert.setString(2, movimiento.getTipoMovimiento());
                stmtInsert.setInt(3, movimiento.getCantidad());
                stmtInsert.setString(4, movimiento.getMotivo());
                stmtInsert.executeUpdate();
            }

            // 2. Modificar el stock del producto afectado
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateStock)) {
                stmtUpdate.setInt(1, movimiento.getCantidad());
                stmtUpdate.setInt(2, movimiento.getProductoId());
                stmtUpdate.executeUpdate();
            }

            conn.commit(); // Si todo sale bien, guardamos cambios de forma atómica
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error en transacción de movimiento: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
