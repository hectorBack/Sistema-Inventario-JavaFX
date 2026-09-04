package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.Cajero;
import com.inventario.model.DTOs.CajeroDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.CajeroRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CajeroRepositoryImpl implements CajeroRepository {

    @Override
    public Optional<CajeroDTO> buscarPorIdDTO(int id) {
        return buscarPorId(id).map(DTOMapper::toDTO);
    }

    @Override
    public Optional<CajeroDTO> buscarPorUsuarioDTO(String usuario) {
        return buscarPorUsuario(usuario).map(DTOMapper::toDTO);
    }

    @Override
    public List<CajeroDTO> obtenerTodosActivosDTO() {
        List<CajeroDTO> resultado = new ArrayList<>();
        for (Cajero cajero : obtenerTodosActivos()) resultado.add(DTOMapper.toDTO(cajero));
        return resultado;
    }

    @Override
    public List<CajeroDTO> buscarPorCriterioDTO(String textoBusqueda) {
        List<CajeroDTO> resultado = new ArrayList<>();
        for (Cajero cajero : buscarPorCriterio(textoBusqueda)) resultado.add(DTOMapper.toDTO(cajero));
        return resultado;
    }

    @Override
    public boolean guardarDTO(CajeroDTO cajero) {
        return guardar(DTOMapper.toModel(cajero));
    }

    @Override
    public boolean actualizarDTO(CajeroDTO cajero) {
        return actualizar(DTOMapper.toModel(cajero));
    }

    @Override
    public boolean guardar(Cajero cajero) {
        String sql = "INSERT INTO cajeros ("
                + "usuario, contrasena, nombre_completo, activo, "
                + "perm_utilizar_producto_comun, perm_aplicar_mayoreo, perm_aplicar_descuento, perm_revisar_historial_ventas, "
                + "perm_registrar_entradas_efectivo, perm_registrar_salidas_efectivo, perm_cobrar_ticket, perm_cobrar_credito, "
                + "perm_cancelar_tickets, perm_eliminar_articulos_venta, perm_facturar_ver_facturas, perm_vender_pago_servicio, "
                + "perm_cancelar_pago_servicio, perm_vender_recargas, "
                + "perm_crear_modificar_eliminar_clientes, perm_asignar_cliente_venta, perm_asignar_remover_credito, perm_ver_cuenta_abonos_reportes, "
                + "perm_crear_nuevos_productos, perm_modificar_productos, perm_eliminar_productos, perm_ver_reporte_ventas, "
                + "perm_crear_promociones, perm_modificar_varios, "
                + "perm_agregar_mercancia, perm_ver_reportes_existencias, perm_ver_movimiento_inventarios, perm_ajustar_inventario, "
                + "perm_corte_turno_efectivo, perm_corte_dia_todos_turnos, perm_ver_ganancia_dia, perm_cambiar_configuracion, "
                + "perm_acceder_reportes_ventas_ganancias, perm_crear_ordenes_compra, perm_recibir_ordenes_compra"
                + ") VALUES ("
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                + ")";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(ps, cajero);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cajero.setId(rs.getInt(1));
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
    public boolean actualizar(Cajero cajero) {
        String sql = "UPDATE cajeros SET "
                + "usuario = ?, contrasena = ?, nombre_completo = ?, activo = ?, "
                + "perm_utilizar_producto_comun = ?, perm_aplicar_mayoreo = ?, perm_aplicar_descuento = ?, perm_revisar_historial_ventas = ?, "
                + "perm_registrar_entradas_efectivo = ?, perm_registrar_salidas_efectivo = ?, perm_cobrar_ticket = ?, perm_cobrar_credito = ?, "
                + "perm_cancelar_tickets = ?, perm_eliminar_articulos_venta = ?, perm_facturar_ver_facturas = ?, perm_vender_pago_servicio = ?, "
                + "perm_cancelar_pago_servicio = ?, perm_vender_recargas = ?, "
                + "perm_crear_modificar_eliminar_clientes = ?, perm_asignar_cliente_venta = ?, perm_asignar_remover_credito = ?, perm_ver_cuenta_abonos_reportes = ?, "
                + "perm_crear_nuevos_productos = ?, perm_modificar_productos = ?, perm_eliminar_productos = ?, perm_ver_reporte_ventas = ?, "
                + "perm_crear_promociones = ?, perm_modificar_varios = ?, "
                + "perm_agregar_mercancia = ?, perm_ver_reportes_existencias = ?, perm_ver_movimiento_inventarios = ?, perm_ajustar_inventario = ?, "
                + "perm_corte_turno_efectivo = ?, perm_corte_dia_todos_turnos = ?, perm_ver_ganancia_dia = ?, perm_cambiar_configuracion = ?, "
                + "perm_acceder_reportes_ventas_ganancias = ?, perm_crear_ordenes_compra = ?, perm_recibir_ordenes_compra = ? "
                + "WHERE id = ?";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            setParameters(ps, cajero);
            ps.setInt(40, cajero.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Cajero> buscarPorId(int id) {
        String sql = "SELECT * FROM cajeros WHERE id = ?";
        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCajero(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cajero> buscarPorUsuario(String usuario) {
        String sql = "SELECT * FROM cajeros WHERE usuario = ? AND activo = true";
        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCajero(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Cajero> obtenerTodosActivos() {
        List<Cajero> lista = new ArrayList<>();
        String sql = "SELECT * FROM cajeros WHERE activo = true ORDER BY nombre_completo ASC";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCajero(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cajero> buscarPorCriterio(String textoBusqueda) {
        List<Cajero> lista = new ArrayList<>();
        String sql = "SELECT * FROM cajeros WHERE activo = true AND (LOWER(nombre_completo) LIKE LOWER(?) OR LOWER(usuario) LIKE LOWER(?)) ORDER BY nombre_completo ASC";

        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            String patron = "%" + textoBusqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCajero(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- ELIMINACIÓN LÓGICA ---
    @Override
    public boolean darDeBajaLogica(int id) {
        String sql = "UPDATE cajeros SET activo = false WHERE id = ?";
        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- ELIMINACIÓN FÍSICA ---
    @Override
    public boolean eliminarFisico(int id) {
        String sql = "DELETE FROM cajeros WHERE id = ?";
        try (Connection con = ConexionDB.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- MÉTODOS AUXILIARES ---
    private void setParameters(PreparedStatement ps, Cajero c) throws SQLException {
        ps.setString(1, c.getUsuario());
        ps.setString(2, c.getContrasena());
        ps.setString(3, c.getNombreCompleto());
        ps.setBoolean(4, c.isActivo());

        // Ventas
        ps.setBoolean(5, c.isPermUtilizarProductoComun());
        ps.setBoolean(6, c.isPermAplicarMayoreo());
        ps.setBoolean(7, c.isPermAplicarDescuento());
        ps.setBoolean(8, c.isPermRevisarHistorialVentas());
        ps.setBoolean(9, c.isPermRegistrarEntradasEfectivo());
        ps.setBoolean(10, c.isPermRegistrarSalidasEfectivo());
        ps.setBoolean(11, c.isPermCobrarTicket());
        ps.setBoolean(12, c.isPermCobrarCredito());
        ps.setBoolean(13, c.isPermCancelarTickets());
        ps.setBoolean(14, c.isPermEliminarArticulosVenta());
        ps.setBoolean(15, c.isPermFacturarVerFacturas());
        ps.setBoolean(16, c.isPermVenderPagoServicio());
        ps.setBoolean(17, c.isPermCancelarPagoServicio());
        ps.setBoolean(18, c.isPermVenderRecargas());

        // Clientes
        ps.setBoolean(19, c.isPermCrearModificarEliminarClientes());
        ps.setBoolean(20, c.isPermAsignarClienteVenta());
        ps.setBoolean(21, c.isPermAsignarRemoverCredito());
        ps.setBoolean(22, c.isPermVerCuentaAbonosReportes());

        // Productos
        ps.setBoolean(23, c.isPermCrearNuevosProductos());
        ps.setBoolean(24, c.isPermModificarProductos());
        ps.setBoolean(25, c.isPermEliminarProductos());
        ps.setBoolean(26, c.isPermVerReporteVentas());
        ps.setBoolean(27, c.isPermCrearPromociones());
        ps.setBoolean(28, c.isPermModificarVarios());

        // Inventario
        ps.setBoolean(29, c.isPermAgregarMercancia());
        ps.setBoolean(30, c.isPermVerReportesExistencias());
        ps.setBoolean(31, c.isPermVerMovimientoInventarios());
        ps.setBoolean(32, c.isPermAjustarInventario());

        // Otros
        ps.setBoolean(33, c.isPermCorteTurnoEfectivo());
        ps.setBoolean(34, c.isPermCorteDiaTodosTurnos());
        ps.setBoolean(35, c.isPermVerGananciaDia());
        ps.setBoolean(36, c.isPermCambiarConfiguracion());
        ps.setBoolean(37, c.isPermAccederReportesVentasGanancias());
        ps.setBoolean(38, c.isPermCrearOrdenesCompra());
        ps.setBoolean(39, c.isPermRecibirOrdenesCompra());
    }

    private Cajero mapearCajero(ResultSet rs) throws SQLException {
        return new Cajero(
                rs.getInt("id"),
                rs.getString("usuario"),
                rs.getString("contrasena"),
                rs.getString("nombre_completo"),
                rs.getBoolean("activo"),
                // Ventas
                rs.getBoolean("perm_utilizar_producto_comun"),
                rs.getBoolean("perm_aplicar_mayoreo"),
                rs.getBoolean("perm_aplicar_descuento"),
                rs.getBoolean("perm_revisar_historial_ventas"),
                rs.getBoolean("perm_registrar_entradas_efectivo"),
                rs.getBoolean("perm_registrar_salidas_efectivo"),
                rs.getBoolean("perm_cobrar_ticket"),
                rs.getBoolean("perm_cobrar_credito"),
                rs.getBoolean("perm_cancelar_tickets"),
                rs.getBoolean("perm_eliminar_articulos_venta"),
                rs.getBoolean("perm_facturar_ver_facturas"),
                rs.getBoolean("perm_vender_pago_servicio"),
                rs.getBoolean("perm_cancelar_pago_servicio"),
                rs.getBoolean("perm_vender_recargas"),
                // Clientes
                rs.getBoolean("perm_crear_modificar_eliminar_clientes"),
                rs.getBoolean("perm_asignar_cliente_venta"),
                rs.getBoolean("perm_asignar_remover_credito"),
                rs.getBoolean("perm_ver_cuenta_abonos_reportes"),
                // Productos
                rs.getBoolean("perm_crear_nuevos_productos"),
                rs.getBoolean("perm_modificar_productos"),
                rs.getBoolean("perm_eliminar_productos"),
                rs.getBoolean("perm_ver_reporte_ventas"),
                rs.getBoolean("perm_crear_promociones"),
                rs.getBoolean("perm_modificar_varios"),
                // Inventario
                rs.getBoolean("perm_agregar_mercancia"),
                rs.getBoolean("perm_ver_reportes_existencias"),
                rs.getBoolean("perm_ver_movimiento_inventarios"),
                rs.getBoolean("perm_ajustar_inventario"),
                // Otros
                rs.getBoolean("perm_corte_turno_efectivo"),
                rs.getBoolean("perm_corte_dia_todos_turnos"),
                rs.getBoolean("perm_ver_ganancia_dia"),
                rs.getBoolean("perm_cambiar_configuracion"),
                rs.getBoolean("perm_acceder_reportes_ventas_ganancias"),
                rs.getBoolean("perm_crear_ordenes_compra"),
                rs.getBoolean("perm_recibir_ordenes_compra")
        );
    }

}
