package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Categoria;
import com.inventario.model.DTOs.CategoriaDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.CategoriaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepositoryImpl implements CategoriaRepository {

    @Override
    public List<CategoriaDTO> listarTodasDTO() {
        List<CategoriaDTO> resultado = new ArrayList<>();
        for (Categoria categoria : listarTodas()) {
            resultado.add(DTOMapper.toDTO(categoria));
        }
        return resultado;
    }

    @Override
    public List<CategoriaDTO> listarActivasDTO() {
        List<CategoriaDTO> resultado = new ArrayList<>();
        for (Categoria categoria : listarActivas()) {
            resultado.add(DTOMapper.toDTO(categoria));
        }
        return resultado;
    }

    @Override
    public CategoriaDTO buscarPorIdDTO(int id) {
        return DTOMapper.toDTO(buscarPorId(id));
    }

    @Override
    public boolean guardarDTO(CategoriaDTO categoria) {
        return guardar(DTOMapper.toModel(categoria));
    }

    @Override
    public boolean actualizarDTO(CategoriaDTO categoria) {
        return actualizar(DTOMapper.toModel(categoria));
    }

    @Override
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre, estado FROM categorias ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("estado")
                );
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }
        return categorias;
    }

    @Override
    public boolean guardar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre, estado) VALUES (?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNombre().trim());
            stmt.setString(2, categoria.getEstado());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ?, estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNombre().trim());
            stmt.setString(2, categoria.getEstado());
            stmt.setInt(3, categoria.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Categoria> listarActivas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre, estado FROM categorias WHERE estado = 'ACTIVO' ORDER BY nombre ASC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar categorías activas: " + e.getMessage());
        }
        return categorias;
    }

    @Override
    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, nombre, estado FROM categorias WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar categoría por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean desactivar(int id) {
        String sql = "UPDATE categorias SET estado = 'INACTIVO' WHERE id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al desactivar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeNombre(String nombre, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM categorias WHERE LOWER(nombre) = LOWER(?) AND id != ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre.trim());
            stmt.setInt(2, idExcluir);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar nombre duplicado: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean tieneProductosAsociados(int categoriaId) {
        String sql = "SELECT COUNT(*) FROM productos WHERE categoria_id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar relaciones con productos: " + e.getMessage());
        }
        return false;
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("estado")
        );
    }

}
