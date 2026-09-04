package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.config.ConfiguracionSistema;
import com.inventario.model.MovimientoInventario;
import com.inventario.model.Producto;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.MovimientoInventarioDTO;
import com.inventario.model.DTOs.ProductoDTO;
import com.inventario.repository.InventarioRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventarioRepositoryImpl implements InventarioRepository {

    @Override
    public List<ProductoDTO> obtenerProductosStockBajoDTO() {
        List<ProductoDTO> resultado = new ArrayList<>();
        for (Producto producto : obtenerProductosStockBajo()) {
            resultado.add(DTOMapper.toDTO(producto));
        }
        return resultado;
    }

    @Override
    public List<MovimientoInventarioDTO> obtenerHistorialMovimientosDTO() {
        List<MovimientoInventarioDTO> resultado = new ArrayList<>();
        for (MovimientoInventario movimiento : obtenerHistorialMovimientos()) {
            resultado.add(DTOMapper.toDTO(movimiento));
        }
        return resultado;
    }

    @Override
    public boolean agregarStock(int idProducto, double cantidad) {
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        // Si el inventario está desactivado, se interrumpe el registro manual de entradas
        if (!usarInventario) {
            return false;
        }

        String sqlUpdate = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        String sqlMovimiento = "INSERT INTO movimientos_inventario (producto_id, tipo_movimiento, cantidad, fecha_movimiento) "
                + "VALUES (?, 'ENTRADA', ?, NOW())";

        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // Transacción

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate); PreparedStatement psMov = conn.prepareStatement(sqlMovimiento)) {

                // 1. Actualizar Stock
                psUpdate.setDouble(1, cantidad);
                psUpdate.setInt(2, idProducto);
                psUpdate.executeUpdate();

                // 2. Insertar Movimiento en Kardex
                psMov.setInt(1, idProducto);
                psMov.setDouble(2, cantidad);
                psMov.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return false;
    }

    @Override
    public List<Producto> obtenerProductosStockBajo() {
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        // Si el módulo está apagado, no se reportan alertas de reabastecimiento
        if (!usarInventario) {
            return Collections.emptyList();
        }

        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock <= stock_minimo AND estado = 'Activo' ORDER BY stock ASC";
        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNombre(rs.getString("nombre"));
                p.setStock(rs.getDouble("stock"));
                p.setStockMinimo(rs.getDouble("stock_minimo"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<MovimientoInventario> obtenerHistorialMovimientos() {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT m.id, m.tipo_movimiento, m.cantidad, m.fecha_movimiento, p.codigo_barras, p.nombre "
                + "FROM movimientos_inventario m "
                + "JOIN productos p ON m.producto_id = p.id "
                + "ORDER BY m.fecha_movimiento DESC";
        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setId(rs.getInt("id"));
                m.setCodigoProducto(rs.getString("codigo_barras"));
                m.setNombreProducto(rs.getString("nombre"));
                m.setTipoMovimiento(rs.getString("tipo_movimiento"));
                m.setCantidad(rs.getDouble("cantidad"));
                m.setFechaMovimiento(rs.getTimestamp("fecha_movimiento").toLocalDateTime());
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
