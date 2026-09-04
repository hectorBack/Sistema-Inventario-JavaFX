package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.MovimientoInventario;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.MovimientoInventarioDTO;
import com.inventario.repository.MovimientoRepository;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovimientoRepositoryImpl implements MovimientoRepository {

    @Override
    public List<MovimientoInventarioDTO> listarTodosDTO() {
        return listarTodos().stream().map(DTOMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioDTO> listarPorProductoDTO(int productoId) {
        return listarPorProducto(productoId).stream().map(DTOMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioDTO> listarPorTipoDTO(String tipoMovimiento) {
        return listarPorTipo(tipoMovimiento).stream().map(DTOMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioDTO> listarPorRangoFechasDTO(LocalDate inicio, LocalDate fin) {
        return listarPorRangoFechas(inicio, fin).stream().map(DTOMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<MovimientoInventarioDTO> buscarConFiltrosDTO(String termino, String tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        return buscarConFiltros(termino, tipo, fechaInicio, fechaFin).stream()
                .map(DTOMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean registrarMovimientoDTO(MovimientoInventarioDTO movimiento) {
        return registrarMovimiento(DTOMapper.toModel(movimiento));
    }

    @Override
    public List<MovimientoInventario> listarTodos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT m.id, m.producto_id, p.nombre as producto_nombre, m.tipo_movimiento, "
                + "m.cantidad, m.motivo, m.fecha_movimiento "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar movimientos: " + e.getMessage());
        }
        return movimientos;
    }

    @Override
    public boolean registrarMovimiento(MovimientoInventario movimiento) {
        String sqlInsert = "INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, motivo) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "ENTRADA".equalsIgnoreCase(movimiento.getTipoMovimiento())
                ? "UPDATE productos SET stock = stock + ? WHERE id = ?"
                : "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?"; // Validación a nivel de query para evitar stock negativo

        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // Transacción Atómica

            // 1. Insertar el movimiento en la bitácora
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setInt(1, movimiento.getProductoId());
                stmtInsert.setString(2, movimiento.getTipoMovimiento().toUpperCase());
                stmtInsert.setDouble(3, movimiento.getCantidad());
                stmtInsert.setString(4, movimiento.getMotivo());
                stmtInsert.executeUpdate();
            }

            // 2. Actualizar stock del producto
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateStock)) {
                stmtUpdate.setDouble(1, movimiento.getCantidad());
                stmtUpdate.setInt(2, movimiento.getProductoId());

                // Si es SALIDA, se asigna el 3er parámetro para validar stock suficiente
                if (!"ENTRADA".equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                    stmtUpdate.setDouble(3, movimiento.getCantidad());
                }

                int filasAfectadas = stmtUpdate.executeUpdate();

                // Si la actualización no afectó filas en una SALIDA, significa que el stock cayó por debajo de la cantidad solicitada
                if (filasAfectadas == 0) {
                    conn.rollback();
                    System.out.println("Transacción abortada: Stock insuficiente para realizar la salida.");
                    return false;
                }
            }

            conn.commit();
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

    @Override
    public int obtenerStockActual(int productoId) {
        String sql = "SELECT stock FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener stock actual del producto: " + e.getMessage());
        }
        return -1; // Retorna -1 si el producto no existe o falla la conexión
    }

    @Override
    public List<MovimientoInventario> listarPorProducto(int productoId) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT m.id, m.producto_id, p.nombre as producto_nombre, m.tipo_movimiento, "
                + "m.cantidad, m.motivo, m.fecha_movimiento "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "WHERE m.producto_id = ? "
                + "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar movimientos por producto: " + e.getMessage());
        }
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> listarPorTipo(String tipoMovimiento) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT m.id, m.producto_id, p.nombre as producto_nombre, m.tipo_movimiento, "
                + "m.cantidad, m.motivo, m.fecha_movimiento "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "WHERE m.tipo_movimiento = ? "
                + "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoMovimiento.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar por tipo de movimiento: " + e.getMessage());
        }
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> listarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT m.id, m.producto_id, p.nombre as producto_nombre, m.tipo_movimiento, "
                + "m.cantidad, m.motivo, m.fecha_movimiento "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "WHERE DATE(m.fecha_movimiento) BETWEEN ? AND ? "
                + "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar movimientos por fecha: " + e.getMessage());
        }
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> buscarConFiltros(String termino, String tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT m.id, m.producto_id, p.nombre as producto_nombre, m.tipo_movimiento, "
                + "m.cantidad, m.motivo, m.fecha_movimiento "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "WHERE 1=1 "
        );

        List<Object> parametros = new ArrayList<>();

        if (termino != null && !termino.trim().isEmpty()) {
            sql.append("AND (p.nombre ILIKE ? OR p.codigo_barras ILIKE ? OR m.motivo ILIKE ?) ");
            String patron = "%" + termino.trim() + "%";
            parametros.add(patron);
            parametros.add(patron);
            parametros.add(patron);
        }

        if (tipo != null && !tipo.trim().isEmpty() && !"TODOS".equalsIgnoreCase(tipo)) {
            sql.append("AND m.tipo_movimiento = ? ");
            parametros.add(tipo.toUpperCase());
        }

        if (fechaInicio != null) {
            sql.append("AND DATE(m.fecha_movimiento) >= ? ");
            parametros.add(Date.valueOf(fechaInicio));
        }

        if (fechaFin != null) {
            sql.append("AND DATE(m.fecha_movimiento) <= ? ");
            parametros.add(Date.valueOf(fechaFin));
        }

        sql.append("ORDER BY m.fecha_movimiento DESC");

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en búsqueda filtrada de movimientos: " + e.getMessage());
        }
        return movimientos;
    }

    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario m = new MovimientoInventario();
        m.setId(rs.getInt("id"));
        m.setProductoId(rs.getInt("producto_id"));
        m.setNombreProducto(rs.getString("producto_nombre"));
        m.setTipoMovimiento(rs.getString("tipo_movimiento"));
        m.setCantidad(rs.getDouble("cantidad"));
        m.setMotivo(rs.getString("motivo"));

        Timestamp ts = rs.getTimestamp("fecha_movimiento");
        if (ts != null) {
            m.setFechaMovimiento(ts.toLocalDateTime());
        }
       

        return m;
    }

}
