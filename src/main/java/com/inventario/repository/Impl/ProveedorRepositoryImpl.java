package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Proveedor;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.ProveedorDTO;
import com.inventario.repository.ProveedorRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorRepositoryImpl implements ProveedorRepository {

    @Override
    public List<ProveedorDTO> listarTodosDTO() {
        List<ProveedorDTO> resultado = new ArrayList<>();
        for (Proveedor proveedor : listarTodos()) {
            resultado.add(DTOMapper.toDTO(proveedor));
        }
        return resultado;
    }

    @Override
    public List<ProveedorDTO> listarActivosDTO() {
        List<ProveedorDTO> resultado = new ArrayList<>();
        for (Proveedor proveedor : listarActivos()) {
            resultado.add(DTOMapper.toDTO(proveedor));
        }
        return resultado;
    }

    @Override
    public ProveedorDTO buscarPorIdDTO(int id) {
        return DTOMapper.toDTO(buscarPorId(id));
    }

    @Override
    public List<ProveedorDTO> buscarConFiltroDTO(String criterio) {
        List<ProveedorDTO> resultado = new ArrayList<>();
        for (Proveedor proveedor : buscarConFiltro(criterio)) {
            resultado.add(DTOMapper.toDTO(proveedor));
        }
        return resultado;
    }

    @Override
    public boolean guardarDTO(ProveedorDTO proveedor) {
        return guardar(DTOMapper.toModel(proveedor));
    }

    @Override
    public boolean actualizarDTO(ProveedorDTO proveedor) {
        return actualizar(DTOMapper.toModel(proveedor));
    }

    @Override
    public List<Proveedor> listarTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, contacto, telefono, email, estado FROM proveedores ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar proveedores: " + e.getMessage());
        }
        return proveedores;
    }

    @Override
    public boolean guardar(Proveedor p) {
        String sql = "INSERT INTO proveedores (nombre, contacto, telefono, email, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNombre().trim());
            stmt.setString(2, p.getContacto().trim());
            stmt.setString(3, p.getTelefono().trim());
            stmt.setString(4, p.getEmail().trim().toLowerCase());
            stmt.setString(5, p.getEstado().toUpperCase());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar proveedor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedores SET nombre=?, contacto=?, telefono=?, email=?, estado=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNombre().trim());
            stmt.setString(2, p.getContacto().trim());
            stmt.setString(3, p.getTelefono().trim());
            stmt.setString(4, p.getEmail().trim().toLowerCase());
            stmt.setString(5, p.getEstado().toUpperCase());
            stmt.setInt(6, p.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar proveedor: " + e.getMessage());
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
            System.out.println("Error al eliminar proveedor de BD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Proveedor> listarActivos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, contacto, telefono, email, estado FROM proveedores WHERE estado = 'ACTIVO' ORDER BY nombre ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar proveedores activos: " + e.getMessage());
        }
        return proveedores;
    }

    @Override
    public Proveedor buscarPorId(int id) {
        String sql = "SELECT id, nombre, contacto, telefono, email, estado FROM proveedores WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProveedor(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar proveedor por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean desactivar(int id) {
        String sql = "UPDATE proveedores SET estado = 'INACTIVO' WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al desactivar proveedor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeNombre(String nombre, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM proveedores WHERE LOWER(nombre) = LOWER(?) AND id != ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.trim());
            stmt.setInt(2, idExcluir);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar nombre de proveedor: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean existeEmail(String email, int idExcluir) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM proveedores WHERE LOWER(email) = LOWER(?) AND id != ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            stmt.setInt(2, idExcluir);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar email de proveedor: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean tieneProductosAsociados(int proveedorId) {
        String sql = "SELECT COUNT(*) FROM productos WHERE proveedor_id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, proveedorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar productos asociados al proveedor: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Proveedor> buscarConFiltro(String criterio) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, contacto, telefono, email, estado FROM proveedores "
                + "WHERE nombre ILIKE ? OR contacto ILIKE ? OR telefono ILIKE ? OR email ILIKE ? "
                + "ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String patron = "%" + criterio.trim() + "%";
            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);
            stmt.setString(4, patron);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(mapearProveedor(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en búsqueda filtrada de proveedores: " + e.getMessage());
        }
        return proveedores;
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("contacto"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("estado")
        );
    }

}
