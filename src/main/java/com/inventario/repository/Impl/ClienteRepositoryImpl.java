package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Cliente;
import com.inventario.repository.ClienteRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepositoryImpl implements ClienteRepository {

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, rfc, telefono, email, direccion, estado FROM clientes ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public boolean guardar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, rfc, telefono, email, direccion, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNombre().trim());
            stmt.setString(2, c.getRfc().trim().toUpperCase());
            stmt.setString(3, c.getTelefono().trim());
            stmt.setString(4, c.getEmail().trim().toLowerCase());
            stmt.setString(5, c.getDireccion().trim());
            stmt.setString(6, c.getEstado().toUpperCase());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre = ?, rfc = ?, telefono = ?, email = ?, direccion = ?, estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNombre().trim());
            stmt.setString(2, c.getRfc().trim().toUpperCase());
            stmt.setString(3, c.getTelefono().trim());
            stmt.setString(4, c.getEmail().trim().toLowerCase());
            stmt.setString(5, c.getDireccion().trim());
            stmt.setString(6, c.getEstado().toUpperCase());
            stmt.setInt(7, c.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente de la BD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Cliente> listarActivos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, rfc, telefono, email, direccion, estado FROM clientes WHERE estado = 'ACTIVO' ORDER BY nombre ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes activos: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT id, nombre, rfc, telefono, email, direccion, estado FROM clientes WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean desactivar(int id) {
        String sql = "UPDATE clientes SET estado = 'INACTIVO' WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al desactivar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeRfc(String rfc, int idExcluir) {
        if (rfc == null || rfc.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM clientes WHERE UPPER(rfc) = UPPER(?) AND id != ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rfc.trim());
            stmt.setInt(2, idExcluir);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar RFC del cliente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean existeEmail(String email, int idExcluir) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM clientes WHERE LOWER(email) = LOWER(?) AND id != ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            stmt.setInt(2, idExcluir);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar Email del cliente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean tieneVentasAsociadas(int clienteId) {
        String sql = "SELECT COUNT(*) FROM ventas WHERE cliente_id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar ventas asociadas al cliente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Cliente> buscarConFiltro(String criterio) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, rfc, telefono, email, direccion, estado FROM clientes "
                + "WHERE nombre ILIKE ? OR rfc ILIKE ? OR telefono ILIKE ? OR email ILIKE ? "
                + "ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String patron = "%" + criterio.trim() + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);
            stmt.setString(4, patron);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en búsqueda filtrada de clientes: " + e.getMessage());
        }
        return clientes;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rfc"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getString("estado")
        );
    }

}
