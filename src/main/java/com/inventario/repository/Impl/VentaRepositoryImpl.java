package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.repository.VentaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositoryImpl implements VentaRepository {

    @Override
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO ventas (cliente_id, total, estado) VALUES (?, ?, ?) RETURNING id";
        String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        // Validación estricta de stock disponible en la misma consulta
        String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        PreparedStatement stmtStock = null;

        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // 1. Insertar Cabecera de la Venta
            stmtVenta = conn.prepareStatement(sqlVenta);
            stmtVenta.setInt(1, venta.getCliente().getId());
            stmtVenta.setDouble(2, venta.getTotal());
            stmtVenta.setString(3, venta.getEstado());

            ResultSet rsVenta = stmtVenta.executeQuery();
            int idVentaGenerado = 0;
            if (rsVenta.next()) {
                idVentaGenerado = rsVenta.getInt(1);
            } else {
                throw new SQLException("No se pudo generar el identificador de la venta.");
            }

            stmtDetalle = conn.prepareStatement(sqlDetalle);
            stmtStock = conn.prepareStatement(sqlActualizarStock);

            // 2. Procesar detalles e inspeccionar disponibilidad física
            for (DetalleVenta detalle : detalles) {
                stmtDetalle.setInt(1, idVentaGenerado);
                stmtDetalle.setInt(2, detalle.getProducto().getId());
                stmtDetalle.setDouble(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());
                stmtDetalle.executeUpdate();

                // 3. Descontar stock evaluando disponibilidad
                stmtStock.setDouble(1, detalle.getCantidad());
                stmtStock.setInt(2, detalle.getProducto().getId());
                stmtStock.setDouble(3, detalle.getCantidad()); // Condición stock >= cantidad

                int filasAfectadasStock = stmtStock.executeUpdate();
                if (filasAfectadasStock == 0) {
                    throw new SQLException("Stock insuficiente o no disponible para el producto: " + detalle.getProducto().getNombre());
                }
            }

            conn.commit(); // CONFIRMACIÓN DE TRANSACCIÓN
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.out.println("Rollback ejecutado en Venta: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en Rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            cerrarRecursos(stmtVenta, stmtDetalle, stmtStock, conn);
        }
    }

    @Override
    public List<Venta> listarTodas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, v.estado, "
                + "c.id as cliente_id, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v INNER JOIN clientes c ON v.cliente_id = c.id ORDER BY v.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas: " + e.getMessage());
        }
        return ventas;
    }

    @Override
    public List<DetalleVenta> listarDetallesPorVenta(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.id, dv.venta_id, dv.cantidad, dv.precio_unitario, dv.subtotal, "
                + "p.id as producto_id, p.nombre as producto_nombre "
                + "FROM detalle_ventas dv INNER JOIN productos p ON dv.producto_id = p.id "
                + "WHERE dv.venta_id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
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

    @Override
    public boolean cancelarVenta(int ventaId) {
        String sqlEstadoVenta = "SELECT estado FROM ventas WHERE id = ?";
        String sqlCancelarVenta = "UPDATE ventas SET estado = 'CANCELADA' WHERE id = ?";
        String sqlReintegrarStock = "UPDATE productos SET stock = stock + ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmtEstado = null;
        PreparedStatement stmtCancelar = null;
        PreparedStatement stmtReintegrar = null;

        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // TRANSACCIÓN DE ANULACIÓN

            // Verificar si la venta no estaba ya cancelada
            stmtEstado = conn.prepareStatement(sqlEstadoVenta);
            stmtEstado.setInt(1, ventaId);
            try (ResultSet rs = stmtEstado.executeQuery()) {
                if (rs.next()) {
                    if ("CANCELADA".equalsIgnoreCase(rs.getString("estado"))) {
                        throw new SQLException("La venta ya se encuentra cancelada.");
                    }
                } else {
                    throw new SQLException("No se encontró la venta especificada.");
                }
            }

            // 1. Obtener los detalles para devolver stock a productos
            List<DetalleVenta> detalles = listarDetallesPorVenta(ventaId);

            stmtReintegrar = conn.prepareStatement(sqlReintegrarStock);
            for (DetalleVenta detalle : detalles) {
                stmtReintegrar.setDouble(1, detalle.getCantidad());
                stmtReintegrar.setInt(2, detalle.getProducto().getId());
                stmtReintegrar.executeUpdate();
            }

            // 2. Marcar la venta como CANCELADA
            stmtCancelar = conn.prepareStatement(sqlCancelarVenta);
            stmtCancelar.setInt(1, ventaId);
            stmtCancelar.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en rollback al cancelar venta: " + ex.getMessage());
                }
            }
            System.out.println("Error al cancelar la venta ID " + ventaId + ": " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(stmtEstado, stmtCancelar, stmtReintegrar, conn);
        }
    }

    @Override
    public List<Venta> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, v.estado, "
                + "c.id as cliente_id, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v INNER JOIN clientes c ON v.cliente_id = c.id "
                + "WHERE v.fecha >= ? AND v.fecha <= ? ORDER BY v.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(fin.atTime(23, 59, 59)));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al filtrar ventas por fechas: " + e.getMessage());
        }
        return ventas;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("cliente_id"),
                rs.getString("cliente_nombre"),
                rs.getString("rfc"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getString("cliente_estado")
        );

        return new Venta(
                rs.getInt("id"),
                cliente,
                rs.getTimestamp("fecha").toLocalDateTime(),
                rs.getDouble("total"),
                rs.getString("estado")
        );
    }

    private void cerrarRecursos(AutoCloseable... recursos) {
        for (AutoCloseable r : recursos) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception e) {
                    // Cierre silencioso de conexión
                }
            }
        }
    }

    @Override
    public List buscarConFiltros(LocalDate inicio, LocalDate fin, Integer clienteId) {
        List ventas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT v.id, v.fecha, v.total, v.estado, "
                + "c.id as cliente_id, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v INNER JOIN clientes c ON v.cliente_id = c.id WHERE 1=1 "
        );

        if (inicio != null) {
            sql.append("AND v.fecha >= ? ");
        }
        if (fin != null) {
            sql.append("AND v.fecha <= ? ");
        }
        if (clienteId != null && clienteId > 0) {
            sql.append("AND c.id = ? ");
        }
        sql.append("ORDER BY v.id DESC");

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (inicio != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(inicio.atStartOfDay()));
            }
            if (fin != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(fin.atTime(23, 59, 59)));
            }
            if (clienteId != null && clienteId > 0) {
                stmt.setInt(paramIndex++, clienteId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al filtrar historial de ventas: " + e.getMessage());
        }
        return ventas;
    }

}
