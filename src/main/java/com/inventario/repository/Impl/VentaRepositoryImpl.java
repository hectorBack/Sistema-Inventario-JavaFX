package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import com.inventario.repository.VentaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositoryImpl implements VentaRepository {

    @Override
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO ventas (cliente_id, total, estado) VALUES (?, ?, ?) RETURNING id";
        String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        PreparedStatement stmtStock = null;

        try {
            conn = ConexionDB.getConexion();
            // INICIAR TRANSACCIÓN: Desactivamos el guardado automático
            conn.setAutoCommit(false);

            // 1. Insertar Cabecera de la Venta y recuperar el ID generado
            stmtVenta = conn.prepareStatement(sqlVenta);
            stmtVenta.setInt(1, venta.getCliente().getId());
            stmtVenta.setDouble(2, venta.getTotal());
            stmtVenta.setString(3, venta.getEstado());

            ResultSet rsVenta = stmtVenta.executeQuery();
            int idVentaGenerado = 0;
            if (rsVenta.next()) {
                idVentaGenerado = rsVenta.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la venta.");
            }

            // Preparamos los statements para el lote de detalles y actualización de stock
            stmtDetalle = conn.prepareStatement(sqlDetalle);
            stmtStock = conn.prepareStatement(sqlActualizarStock);

            // 2. Recorrer el carrito de compras e insertar fila por fila
            for (DetalleVenta detalle : detalles) {
                // Registrar el detalle
                stmtDetalle.setInt(1, idVentaGenerado);
                stmtDetalle.setInt(2, detalle.getProducto().getId());
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.executeUpdate();

                // 3. Descontar Stock de forma inmediata en la misma transacción
                stmtStock.setInt(1, detalle.getCantidad());
                stmtStock.setInt(2, detalle.getProducto().getId());

                int filasAfectadasStock = stmtStock.executeUpdate();
                if (filasAfectadasStock == 0) {
                    throw new SQLException("Error al actualizar el stock del producto ID: " + detalle.getProducto().getId());
                }
            }

            // SI TODO SALIÓ BIEN: Confirmamos y asentamos los cambios en la BD
            conn.commit();
            return true;

        } catch (SQLException e) {
            // SI ALGO FALLA: Cancelamos absolutamente todo el bloque completo
            if (conn != null) {
                try {
                    System.out.println("Cerrando transacción por error: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en Rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            // Cerramos todos los recursos limpios
            try {
                if (stmtVenta != null) {
                    stmtVenta.close();
                }
                if (stmtDetalle != null) {
                    stmtDetalle.close();
                }
                if (stmtStock != null) {
                    stmtStock.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Venta> listarTodas() {
        List<Venta> ventas = new ArrayList<>();
        // Unimos con clientes para reconstruir el objeto completo mapeado
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v INNER JOIN clientes c ON v.cliente_id = c.id ORDER BY v.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("cliente_id"),
                        rs.getString("cliente_nombre"),
                        rs.getString("rfc"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getString("cliente_estado")
                );

                ventas.add(new Venta(
                        rs.getInt("id"),
                        cliente,
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas: " + e.getMessage());
        }
        return ventas;
    }

    @Override
    public List<DetalleVenta> listarDetallesPorVenta(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        // En este método simple no es necesario traer el objeto Producto completo con Categorías, 
        // basta con inicializar el ID y Nombre para renderizar el historial si se requiere.
        String sql = "SELECT dv.*, p.nombre as producto_nombre "
                + "FROM detalle_ventas dv INNER JOIN productos p ON dv.producto_id = p.id "
                + "WHERE dv.venta_id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Creamos un mock/objeto básico del producto para el detalle
                    com.inventario.model.Producto p = new com.inventario.model.Producto();
                    p.setId(rs.getInt("producto_id"));
                    p.setNombre(rs.getString("producto_nombre"));

                    detalles.add(new DetalleVenta(
                            rs.getInt("id"),
                            rs.getInt("venta_id"),
                            p,
                            rs.getInt("cantidad"),
                            rs.getDouble("precio_unitario")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener detalles de venta: " + e.getMessage());
        }
        return detalles;
    }

}
