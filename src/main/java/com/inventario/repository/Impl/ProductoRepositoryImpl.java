package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Categoria;
import com.inventario.model.DetallePaquete;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.repository.ProductoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoRepositoryImpl implements ProductoRepository {

    @Override
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT p.*, "
                + "c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id "
                + "ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return productos;
    }

    @Override
    public boolean guardar(Producto producto) {
        String sql = "INSERT INTO productos (codigo_barras, nombre, descripcion, precio, precio_mayoreo, "
                + "precio_compra, porcentaje_ganancia, stock, stock_minimo, tipo_venta, estado, categoria_id, proveedor_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 1. Agregar Statement.RETURN_GENERATED_KEYS aquí
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, producto.getCodigoBarras());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            stmt.setDouble(4, producto.getPrecio());
            stmt.setDouble(5, producto.getPrecioMayoreo());
            stmt.setDouble(6, producto.getPrecioCompra());
            stmt.setDouble(7, producto.getPorcentajeGanancia());
            stmt.setDouble(8, producto.getStock());
            stmt.setDouble(9, producto.getStockMinimo());
            stmt.setString(10, producto.getTipoVenta());
            stmt.setString(11, producto.getEstado());

            if (producto.getCategoria() != null) {
                stmt.setInt(12, producto.getCategoria().getId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            if (producto.getProveedor() != null) {
                stmt.setInt(13, producto.getProveedor().getId());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }

            int filasAfectadas = stmt.executeUpdate();

            // 2. Recuperar el ID generado por la BD y asignarlo al objeto
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        producto.setId(rs.getInt(1)); // Asigna el ID autoincrementado
                    }
                }
                return true;
            }

            return false;
        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET codigo_barras = ?, nombre = ?, descripcion = ?, precio = ?, "
                + "precio_mayoreo = ?, precio_compra = ?, porcentaje_ganancia = ?, stock = ?, stock_minimo = ?, "
                + "tipo_venta = ?, estado = ?, categoria_id = ?, proveedor_id = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getCodigoBarras());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            stmt.setDouble(4, producto.getPrecio());
            stmt.setDouble(5, producto.getPrecioMayoreo());
            stmt.setDouble(6, producto.getPrecioCompra());
            stmt.setDouble(7, producto.getPorcentajeGanancia());
            stmt.setDouble(8, producto.getStock());
            stmt.setDouble(9, producto.getStockMinimo());
            stmt.setString(10, producto.getTipoVenta());
            stmt.setString(11, producto.getEstado());

            if (producto.getCategoria() != null) {
                stmt.setInt(12, producto.getCategoria().getId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            if (producto.getProveedor() != null) {
                stmt.setInt(13, producto.getProveedor().getId());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }

            stmt.setInt(14, producto.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Producto> listarActivos() {
        return listarTodos().stream()
                .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public Producto buscarPorId(int id) {
        String sql = "SELECT p.*, c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id "
                + "WHERE p.id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        String sql = "SELECT p.*, c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id "
                + "WHERE p.codigo_barras = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoBarras);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar por código de barras: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Producto> buscarPorNombre(String termino) {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT p.*, c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id "
                + "WHERE p.nombre ILIKE ? OR p.codigo_barras ILIKE ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String patron = "%" + termino + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en búsqueda por nombre: " + e.getMessage());
        }
        return productos;
    }

    @Override
    public List<Producto> listarPorCategoria(int categoriaId) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id "
                + "WHERE p.categoria_id = ? ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoriaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos por categoría: " + e.getMessage());
        }
        return productos;
    }

    @Override
    public List<Producto> listarPorProveedor(int proveedorId) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id "
                + "WHERE p.proveedor_id = ? ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proveedorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos por proveedor: " + e.getMessage());
        }
        return productos;
    }

    // Método auxiliar de mapeo centralizado
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Categoria cat = null;
        int catId = rs.getInt("categoria_id");
        if (!rs.wasNull()) {
            cat = new Categoria(
                    catId,
                    rs.getString("categoria_nombre"),
                    rs.getString("categoria_estado")
            );
        }

        Proveedor prov = null;
        int provId = rs.getInt("proveedor_id");
        if (!rs.wasNull()) {
            prov = new Proveedor(
                    provId,
                    rs.getString("proveedor_nombre"),
                    rs.getString("proveedor_contacto"),
                    rs.getString("proveedor_telefono"),
                    rs.getString("proveedor_email"),
                    rs.getString("proveedor_estado")
            );
        }

        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo_barras"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getDouble("precio_mayoreo"),
                rs.getDouble("precio_compra"),
                rs.getDouble("porcentaje_ganancia"),
                rs.getDouble("stock"),
                rs.getDouble("stock_minimo"),
                rs.getString("tipo_venta"),
                rs.getString("estado"),
                cat,
                prov
        );
    }

    @Override
    public List<Producto> listarProductosStockBajo(int limiteMinimo) {
        return listarTodos().stream()
                .filter(p -> p.getStock() <= p.getStockMinimo())
                .collect(Collectors.toList());
    }

    @Override
    public boolean actualizarStock(int productoId, double nuevaCantidad) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nuevaCantidad);
            stmt.setInt(2, productoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean desactivar(int id) {
        String sql = "UPDATE productos SET estado = 'INACTIVO' WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al desactivar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeCodigoBarras(String codigoBarras, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM productos WHERE codigo_barras = ? AND id != ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoBarras);
            stmt.setInt(2, idExcluir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar código de barras: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean tieneMovimientosAsociados(int productoId) {
        String sql = "SELECT COUNT(*) FROM detalle_ventas WHERE producto_id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar historial: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<DetallePaquete> obtenerDetallesPaquete(int idProductoPadre) {
        List<DetallePaquete> lista = new ArrayList<>();

        // Ajusta la consulta SQL según los nombres exactos de tus tablas y columnas
        String sql = "SELECT dp.cantidad, p.id, p.codigo_barras, p.nombre, p.precio, p.precio_compra "
                + "FROM detalle_paquete dp "
                + "INNER JOIN productos p ON dp.id_producto_hijo = p.id "
                + "WHERE dp.id_producto_padre = ?";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProductoPadre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto hijo = new Producto();
                    hijo.setId(rs.getInt("id"));
                    hijo.setCodigoBarras(rs.getString("codigo_barras"));
                    hijo.setNombre(rs.getString("nombre"));
                    hijo.setPrecio(rs.getDouble("precio"));
                    hijo.setPrecioCompra(rs.getDouble("precio_compra"));

                    double cantidad = rs.getDouble("cantidad");

                    lista.add(new DetallePaquete(hijo, cantidad));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles del paquete: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean guardarDetallesPaquete(int idProductoPadre, List<DetallePaquete> detalles) {
        String sql = "INSERT INTO detalle_paquete (id_producto_padre, id_producto_hijo, cantidad) VALUES (?, ?, ?)";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            for (DetallePaquete detalle : detalles) {
                ps.setInt(1, idProductoPadre);
                ps.setInt(2, detalle.getProducto().getId());
                ps.setDouble(3, detalle.getCantidad());
                ps.addBatch();
            }

            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al guardar detalles del paquete: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean reemplazarDetallesPaquete(int idProductoPadre, List<DetallePaquete> detalles) {
        String sqlDelete = "DELETE FROM detalle_paquete WHERE id_producto_padre = ?";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement psDelete = con.prepareStatement(sqlDelete)) {

            psDelete.setInt(1, idProductoPadre);
            psDelete.executeUpdate();

            if (detalles != null && !detalles.isEmpty()) {
                return guardarDetallesPaquete(idProductoPadre, detalles);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al reemplazar detalles del paquete: " + e.getMessage());
            return false;
        }
    }

}
