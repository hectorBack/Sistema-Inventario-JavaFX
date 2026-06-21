package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Categoria;
import com.inventario.model.Producto;
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
        // Modificamos el SQL usando un LEFT JOIN para traernos los datos de la categoría asignada
        String sql = "SELECT p.id, p.nombre, p.precio, p.stock, p.estado, p.categoria_id, "
                   + "c.nombre AS categoria_nombre, c.estado AS categoria_estado "
                   + "FROM productos p "
                   + "LEFT JOIN categorias c ON p.categoria_id = c.id "
                   + "ORDER BY p.id DESC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Reconstruimos el objeto Categoria (si existe en la BD)
                Categoria cat = null;
                int catId = rs.getInt("categoria_id");
                if (!rs.wasNull()) { 
                    cat = new Categoria(
                        catId,
                        rs.getString("categoria_nombre"),
                        rs.getString("categoria_estado")
                    );
                }

                // 2. Construimos el Producto pasando la Categoría al final
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("estado"),
                        cat // Pasamos el objeto vinculación
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
        // Añadimos la columna categoria_id al INSERT
        String sql = "INSERT INTO productos (nombre, precio, stock, estado, categoria_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getEstado());
            
            // Si el producto tiene una categoría asignada, guardamos su ID; si no, mandamos NULL
            if (producto.getCategoria() != null) {
                stmt.setInt(5, producto.getCategoria().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean actualizar(Producto producto) {
        // Añadimos la actualización de categoria_id al UPDATE
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ?, estado = ?, categoria_id = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.setString(4, producto.getEstado());
            
            if (producto.getCategoria() != null) {
                stmt.setInt(5, producto.getCategoria().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setInt(6, producto.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
    
}
