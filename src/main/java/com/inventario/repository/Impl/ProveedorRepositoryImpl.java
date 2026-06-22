package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Proveedor;
import com.inventario.repository.ProveedorRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorRepositoryImpl implements ProveedorRepository {

    @Override
    public List<Proveedor> listarTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY id DESC";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                proveedores.add(new Proveedor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("contacto"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    @Override
    public boolean guardar(Proveedor p) {
        String sql = "INSERT INTO proveedores (nombre, contacto, telefono, email, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getContacto());
            stmt.setString(3, p.getTelefono());
            stmt.setString(4, p.getEmail());
            stmt.setString(5, p.getEstado());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedores SET nombre=?, contacto=?, telefono=?, email=?, estado=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNombre());
            stmt.setString(2, p.getContacto());
            stmt.setString(3, p.getTelefono());
            stmt.setString(4, p.getEmail());
            stmt.setString(5, p.getEstado());
            stmt.setInt(6, p.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
