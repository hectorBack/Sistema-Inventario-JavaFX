package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Promocion;
import com.inventario.repository.PromocionRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PromocionRepositoryImpl implements PromocionRepository {

    @Override
    public boolean guardar(Promocion promocion) {
        String sql = "INSERT INTO promociones (nombre, codigo_barras_producto, cantidad_desde, cantidad_hasta, "
                + "precio_promocion, precio_normal, precio_costo, precio_unitario, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, promocion.getNombre());
            ps.setString(2, promocion.getCodigoBarrasProducto());
            ps.setDouble(3, promocion.getCantidadDesde());
            ps.setDouble(4, promocion.getCantidadHasta());
            ps.setDouble(5, promocion.getPrecioPromocion());
            ps.setDouble(6, promocion.getPrecioNormal());
            ps.setDouble(7, promocion.getPrecioCosto());
            ps.setDouble(8, promocion.getPrecioUnitario());
            ps.setString(9, promocion.getEstado());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        promocion.setId(rs.getInt(1));
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
    public boolean actualizar(Promocion promocion) {
        String sql = "UPDATE promociones SET nombre=?, codigo_barras_producto=?, cantidad_desde=?, cantidad_hasta=?, "
                + "precio_promocion=?, precio_normal=?, precio_costo=?, precio_unitario=?, estado=? WHERE id=?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promocion.getNombre());
            ps.setString(2, promocion.getCodigoBarrasProducto());
            ps.setDouble(3, promocion.getCantidadDesde());
            ps.setDouble(4, promocion.getCantidadHasta());
            ps.setDouble(5, promocion.getPrecioPromocion());
            ps.setDouble(6, promocion.getPrecioNormal());
            ps.setDouble(7, promocion.getPrecioCosto());
            ps.setDouble(8, promocion.getPrecioUnitario());
            ps.setString(9, promocion.getEstado());
            ps.setInt(10, promocion.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM promociones WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Promocion> listarActivas() {
        return listarPorEstado("ACTIVA");
    }

    @Override
    public List<Promocion> listarTodas() {
        List<Promocion> lista = new ArrayList<>();
        String sql = "SELECT * FROM promociones ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Promocion buscarPorId(int id) {
        String sql = "SELECT * FROM promociones WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Promocion> buscarPorNombre(String nombre) {
        List<Promocion> lista = new ArrayList<>();
        String sql = "SELECT * FROM promociones WHERE nombre ILIKE ? ORDER BY nombre ASC";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private List<Promocion> listarPorEstado(String estado) {
        List<Promocion> lista = new ArrayList<>();
        String sql = "SELECT * FROM promociones WHERE estado = ? ORDER BY id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Promocion mapear(ResultSet rs) throws SQLException {
        return new Promocion(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("codigo_barras_producto"),
                rs.getDouble("cantidad_desde"),
                rs.getDouble("cantidad_hasta"),
                rs.getDouble("precio_promocion"),
                rs.getDouble("precio_normal"),
                rs.getDouble("precio_costo"),
                rs.getDouble("precio_unitario"),
                rs.getString("estado")
        );
    }

    @Override
    public String verificarConflictoDeRango(String codigoBarras, double cantidadDesde, double cantidadHasta, int idExcluir) {
        List<Promocion> promociones = listarActivas();
        
        for (Promocion promo : promociones) {
            // Excluir la promoción actual si se está actualizando
            if (promo.getId() == idExcluir) {
                continue;
            }
            
            // Verificar si es el mismo producto
            if (!promo.getCodigoBarrasProducto().equals(codigoBarras)) {
                continue;
            }
            
            // Verificar si hay superposición de rangos
            // Hay conflicto si:
            // 1. El nuevo rango comienza dentro de uno existente
            // 2. El nuevo rango termina dentro de uno existente
            // 3. El nuevo rango contiene completamente a uno existente
            // 4. Uno existente contiene completamente al nuevo
            
            double promoDesde = promo.getCantidadDesde();
            double promoHasta = promo.getCantidadHasta();
            
            // Verificar superposición
            if ((cantidadDesde >= promoDesde && cantidadDesde <= promoHasta) ||  // Comienza dentro
                (cantidadHasta >= promoDesde && cantidadHasta <= promoHasta) ||  // Termina dentro
                (cantidadDesde <= promoDesde && cantidadHasta >= promoHasta)) {  // Contiene a la existente
                
                return String.format("Promoción en conflicto: Ya existe otra promoción para este producto " +
                        "en el rango %.2f - %.2f kg/unid a $%.2f. El nuevo rango %.2f - %.2f se superpone con esta.",
                        promoDesde, promoHasta, promo.getPrecioPromocion(), cantidadDesde, cantidadHasta);
            }
        }
        
        return null;
    }
}
