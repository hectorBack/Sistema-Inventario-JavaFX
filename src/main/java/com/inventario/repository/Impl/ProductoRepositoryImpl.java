package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.repository.ProductoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {

    @Override
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();

        // 1. Modificamos el SQL sumando otro LEFT JOIN para los proveedores y sus alias correspondientes
        String sql = "SELECT p.id, p.nombre, p.precio, p.stock, p.estado, p.categoria_id, p.proveedor_id, "
                + "c.nombre AS categoria_nombre, c.estado AS categoria_estado, "
                + "prov.nombre AS proveedor_nombre, prov.contacto AS proveedor_contacto, "
                + "prov.telefono AS proveedor_telefono, prov.email AS proveedor_email, prov.estado AS proveedor_estado "
                + "FROM productos p "
                + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                + "LEFT JOIN proveedores prov ON p.proveedor_id = prov.id " // Segundo JOIN
                + "ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 2. Reconstruimos la Categoría (Tu lógica existente)
                Categoria cat = null;
                int catId = rs.getInt("categoria_id");
                if (!rs.wasNull()) {
                    cat = new Categoria(
                            catId,
                            rs.getString("categoria_nombre"),
                            rs.getString("categoria_estado")
                    );
                }

                // 3. NUEVO: Reconstruimos el objeto Proveedor validando si existe en la BD
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

                // 4. Construimos el Producto pasando tanto Categoría como Proveedor al constructor nuevo
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("estado"),
                        cat, // Objeto categoría
                        prov // NUEVO: Objeto proveedor
                );
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return productos;
    }

    @Override
    public boolean guardar(Producto producto) {
        // 1. Añadimos la columna proveedor_id al final del INSERT
        String sql = "INSERT INTO productos (nombre, precio, stock, estado, categoria_id, proveedor_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getEstado());

            // Gestión de categoría existente
            if (producto.getCategoria() != null) {
                stmt.setInt(5, producto.getCategoria().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            // 2. NUEVO: Si el producto trae un proveedor, guardamos su ID; si no, mandamos NULL
            if (producto.getProveedor() != null) {
                stmt.setInt(6, producto.getProveedor().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Producto producto) {
        // 1. Añadimos la columna proveedor_id antes del WHERE en el UPDATE
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ?, estado = ?, categoria_id = ?, proveedor_id = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getEstado());

            // Gestión de categoría
            if (producto.getCategoria() != null) {
                stmt.setInt(5, producto.getCategoria().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            // 2. NUEVO: Gestión de proveedor para la actualización
            if (producto.getProveedor() != null) {
                stmt.setInt(6, producto.getProveedor().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            // El ID del producto pasa a ser el parámetro número 7
            stmt.setInt(7, producto.getId());

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

}
