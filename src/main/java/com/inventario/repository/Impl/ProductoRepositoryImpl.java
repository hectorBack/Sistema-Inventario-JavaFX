package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Categoria;
import com.inventario.model.DetallePaquete;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.DetallePaqueteDTO;
import com.inventario.model.Producto;
import com.inventario.model.DTOs.ProductoDTO;
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
    public List<ProductoDTO> listarTodosDTO() {
        return listarTodos().stream().map(DTOMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarActivosDTO() {
        return listarActivos().stream().map(DTOMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProductoDTO buscarPorCodigoBarrasDTO(String codigo) {
        return DTOMapper.toDTO(buscarPorCodigoBarras(codigo));
    }

    @Override
    public List<ProductoDTO> buscarPorNombreDTO(String nombre) {
        return buscarPorNombre(nombre).stream().map(DTOMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean guardarDTO(ProductoDTO producto) {
        return guardar(DTOMapper.toModel(producto));
    }

    @Override
    public boolean actualizarDTO(ProductoDTO producto) {
        return actualizar(DTOMapper.toModel(producto));
    }

    @Override
    public boolean guardar(Producto p) {
        String sql = "INSERT INTO productos (codigo_barras, nombre, descripcion, precio, precio_compra, "
                + "porcentaje_ganancia, precio_mayoreo, stock, stock_minimo, tipo_venta, estado, id_categoria, id_proveedor) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setearParametrosProducto(ps, p);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET codigo_barras=?, nombre=?, descripcion=?, precio=?, precio_compra=?, "
                + "porcentaje_ganancia=?, precio_mayoreo=?, stock=?, stock_minimo=?, tipo_venta=?, estado=?, "
                + "id_categoria=?, id_proveedor=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            setearParametrosProducto(ps, p);
            ps.setInt(14, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.*, "
                + "c.nombre AS nombre_categoria, "
                + "pr.nombre AS nombre_proveedor "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.id_categoria = c.id "
                + "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id "
                + "ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Producto buscarPorCodigoBarras(String codigo) {
        String sql = "SELECT * FROM productos WHERE codigo_barras = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre ASC";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean existeCodigoBarras(String codigo, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM productos WHERE codigo_barras = ? AND id <> ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DetallePaquete> obtenerDetallesPaquete(int idPaquete) {
        List<DetallePaquete> detalles = new ArrayList<>();
        // SE CAMBIÓ dp.id_paquete POR dp.id_producto_padre
        String sql = "SELECT dp.cantidad, p.* FROM detalle_paquete dp "
                + "JOIN productos p ON dp.id_producto_hijo = p.id WHERE dp.id_producto_padre = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaquete);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto pHijo = new Producto();
                    pHijo.setId(rs.getInt("id"));
                    pHijo.setCodigoBarras(rs.getString("codigo_barras"));
                    pHijo.setNombre(rs.getString("nombre"));
                    pHijo.setPrecioCompra(rs.getDouble("precio_compra"));
                    pHijo.setPrecio(rs.getDouble("precio"));

                    double cantidad = rs.getDouble("cantidad");
                    DetallePaquete dp = new DetallePaquete(pHijo, cantidad);
                    detalles.add(dp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    @Override
    public List<DetallePaqueteDTO> obtenerDetallesPaqueteDTO(int idPaquete) {
        List<DetallePaqueteDTO> resultado = new ArrayList<>();
        for (DetallePaquete detalle : obtenerDetallesPaquete(idPaquete)) {
            resultado.add(new DetallePaqueteDTO(
                    detalle.getProducto() == null ? null : detalle.getProducto().getId(),
                    java.math.BigDecimal.valueOf(detalle.getCantidad())));
        }
        return resultado;
    }

    @Override
    public boolean guardarDetallesPaquete(int idPaquete, List<DetallePaquete> detalles) {
        // SE CAMBIÓ id_paquete POR id_producto_padre
        String sql = "INSERT INTO detalle_paquete (id_producto_padre, id_producto_hijo, cantidad) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DetallePaquete dp : detalles) {
                ps.setInt(1, idPaquete);
                ps.setInt(2, dp.getProducto() != null ? dp.getProducto().getId() : 0);
                ps.setDouble(3, dp.getCantidad());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean reemplazarDetallesPaquete(int idPaquete, List<DetallePaquete> detalles) {
        // SE CAMBIÓ id_paquete POR id_producto_padre
        String sqlDelete = "DELETE FROM detalle_paquete WHERE id_producto_padre = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
            psDel.setInt(1, idPaquete);
            psDel.executeUpdate();

            if (detalles != null && !detalles.isEmpty()) {
                return guardarDetallesPaquete(idPaquete, detalles);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setearParametrosProducto(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getCodigoBarras());
        ps.setString(2, p.getNombre());
        ps.setString(3, p.getDescripcion());
        ps.setDouble(4, p.getPrecio());
        ps.setDouble(5, p.getPrecioCompra());
        ps.setDouble(6, p.getPorcentajeGanancia());
        ps.setDouble(7, p.getPrecioMayoreo());
        ps.setDouble(8, p.getStock());
        ps.setDouble(9, p.getStockMinimo());
        ps.setString(10, p.getTipoVenta());
        ps.setString(11, p.getEstado());
        if (p.getCategoria() != null) {
            ps.setInt(12, p.getCategoria().getId());
        } else {
            ps.setNull(12, Types.INTEGER);
        }
        if (p.getProveedor() != null) {
            ps.setInt(13, p.getProveedor().getId());
        } else {
            ps.setNull(13, Types.INTEGER);
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setCodigoBarras(rs.getString("codigo_barras"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getDouble("precio"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPorcentajeGanancia(rs.getDouble("porcentaje_ganancia"));
        p.setPrecioMayoreo(rs.getDouble("precio_mayoreo"));
        p.setStock(rs.getDouble("stock"));
        p.setStockMinimo(rs.getDouble("stock_minimo"));
        p.setTipoVenta(rs.getString("tipo_venta"));
        p.setEstado(rs.getString("estado"));

        // --- MAPEAR CATEGORÍA ---
        int idCat = rs.getInt("id_categoria");
        if (!rs.wasNull()) {
            String nomCat = "";
            try {
                nomCat = rs.getString("nombre_categoria");
            } catch (SQLException ignored) {
            }

            // Usamos el constructor completo (id, nombre, estado)
            Categoria cat = new Categoria(idCat, nomCat != null ? nomCat : "", "ACTIVO");
            p.setCategoria(cat);
        }

        // --- MAPEAR PROVEEDOR ---
        int idProv = rs.getInt("id_proveedor");
        if (!rs.wasNull()) {
            String nomProv = "";
            try {
                nomProv = rs.getString("nombre_proveedor");
            } catch (SQLException ignored) {
            }

            // Usamos el constructor vacío y seteamos id y nombre
            Proveedor prov = new Proveedor();
            prov.setId(idProv);
            prov.setNombre(nomProv != null ? nomProv : "");
            p.setProveedor(prov);
        }

        return p;
    }

    @Override
    public boolean eliminarLogico(int id) {
        String sql = "UPDATE productos SET estado = 'INACTIVO' WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tieneAsociaciones(int id) {
        // Revisa si existe en ventas o en movimientos de inventario
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM detalle_ventas WHERE producto_id = ?) + "
                + "(SELECT COUNT(*) FROM movimientos_inventario WHERE producto_id = ?) AS total";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Producto> listarActivos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.*, "
                + "c.nombre AS nombre_categoria, "
                + "pr.nombre AS nombre_proveedor "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.id_categoria = c.id "
                + "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id "
                + "WHERE p.estado = 'ACTIVO' "
                + "ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
