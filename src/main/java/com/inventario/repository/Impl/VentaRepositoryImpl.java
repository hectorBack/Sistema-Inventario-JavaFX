package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.config.ConfiguracionSistema;
import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.DetalleVentaDTO;
import com.inventario.model.DTOs.VentaDTO;
import com.inventario.repository.VentaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositoryImpl implements VentaRepository {

    @Override
    public boolean registrarVentaDTO(VentaDTO venta, List<DetalleVentaDTO> detalles) {
        List<DetalleVenta> modelos = new ArrayList<>();
        for (DetalleVentaDTO detalle : detalles) modelos.add(DTOMapper.toModel(detalle));
        return registrarVenta(DTOMapper.toModel(venta), modelos);
    }

    @Override
    public List<VentaDTO> listarTodasDTO() {
        List<VentaDTO> resultado = new ArrayList<>();
        for (Venta venta : listarTodas()) resultado.add(DTOMapper.toDTO(venta));
        return resultado;
    }

    @Override
    public List<VentaDTO> buscarPorRangoFechasDTO(LocalDate inicio, LocalDate fin) {
        List<VentaDTO> resultado = new ArrayList<>();
        for (Venta venta : buscarPorRangoFechas(inicio, fin)) resultado.add(DTOMapper.toDTO(venta));
        return resultado;
    }

    @Override
    public List<DetalleVentaDTO> listarDetallesPorVentaDTO(int ventaId) {
        List<DetalleVentaDTO> resultado = new ArrayList<>();
        for (DetalleVenta detalle : listarDetallesPorVenta(ventaId)) resultado.add(DTOMapper.toDTO(detalle));
        return resultado;
    }

    @Override
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO ventas (cliente_id, total, estado) VALUES (?, ?, ?) RETURNING id";
        String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";

        // 1. Obtener la configuración del sistema
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;
        PreparedStatement stmtStock = null;

        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // 1. Insertar Cabecera de la Venta
            stmtVenta = conn.prepareStatement(sqlVenta);

            if (venta.getCliente() != null && venta.getCliente().getId() > 0) {
                stmtVenta.setInt(1, venta.getCliente().getId());
            } else {
                stmtVenta.setNull(1, java.sql.Types.INTEGER);
            }

            stmtVenta.setDouble(2, venta.getTotal());
            stmtVenta.setString(3, venta.getEstado());

            ResultSet rsVenta = stmtVenta.executeQuery();
            int idVentaGenerado = 0;
            if (rsVenta.next()) {
                idVentaGenerado = rsVenta.getInt(1);
            } else {
                throw new SQLException("No se pudo generar el identificador de la venta.");
            }

            stmtDetalle = conn.prepareStatement(sqlDetalle);

            // Solo instanciar la consulta de stock si el control de inventario está ACTIVO
            if (usarInventario) {
                stmtStock = conn.prepareStatement(sqlActualizarStock);
            }

            // 2. Procesar detalles
            for (DetalleVenta detalle : detalles) {
                stmtDetalle.setInt(1, idVentaGenerado);

                // EVALUACIÓN DE PRODUCTO COMÚN / GENÉRICO (ID = 0 o NULL)
                boolean esProductoComun = (detalle.getProducto() == null || detalle.getProducto().getId() <= 0);

                if (esProductoComun) {
                    stmtDetalle.setNull(2, java.sql.Types.INTEGER);
                } else {
                    stmtDetalle.setInt(2, detalle.getProducto().getId());
                }

                stmtDetalle.setDouble(3, detalle.getCantidad());
                stmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                stmtDetalle.setDouble(5, detalle.getSubtotal());

                String nombreDesc = (detalle.getProducto() != null) ? detalle.getProducto().getNombre() : "Artículo Común";
                stmtDetalle.setString(6, nombreDesc);

                stmtDetalle.executeUpdate();

                // 3. Descontar stock ÚNICAMENTE si usarInventario es true
                if (usarInventario && !esProductoComun) {
                    stmtStock.setDouble(1, detalle.getCantidad());
                    stmtStock.setInt(2, detalle.getProducto().getId());
                    stmtStock.setDouble(3, detalle.getCantidad());

                    int filasAfectadasStock = stmtStock.executeUpdate();
                    if (filasAfectadasStock == 0) {
                        throw new SQLException("Stock insuficiente para el producto: " + detalle.getProducto().getNombre());
                    }
                }
            }

            conn.commit(); // CONFIRMACIÓN DE TRANSACCIÓN
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.out.println("Rollback ejecutado en Venta: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en Rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            cerrarRecursos(stmtVenta, stmtDetalle, stmtStock, conn);
        }
    }

    @Override
    public List<Venta> listarTodas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.id, v.fecha, v.total, v.estado, "
                + "c.id as cliente_id, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v INNER JOIN clientes c ON v.cliente_id = c.id ORDER BY v.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas: " + e.getMessage());
        }
        return ventas;
    }

    @Override
    public List<DetalleVenta> listarDetallesPorVenta(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.id, dv.venta_id, dv.cantidad, dv.precio_unitario, dv.subtotal, dv.descripcion, "
                + "p.id as producto_id, p.nombre as producto_nombre "
                + "FROM detalle_ventas dv LEFT JOIN productos p ON dv.producto_id = p.id "
                + "WHERE dv.venta_id = ?";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ventaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    int prodId = rs.getInt("producto_id");
                    String descripcionGuardada = rs.getString("descripcion");

                    if (!rs.wasNull() && prodId > 0) {
                        p.setId(prodId);
                        p.setNombre(rs.getString("producto_nombre"));
                    } else {
                        p.setId(0);
                        // Si es común, se asigna la descripción guardada en el detalle
                        p.setNombre((descripcionGuardada != null && !descripcionGuardada.isBlank())
                                ? descripcionGuardada
                                : "Artículo Común");
                    }

                    detalles.add(new DetalleVenta(
                            rs.getInt("id"),
                            rs.getInt("venta_id"),
                            p,
                            rs.getDouble("cantidad"),
                            rs.getDouble("precio_unitario")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener detalles de venta: " + e.getMessage());
        }
        return detalles;
    }

    @Override
    public boolean cancelarVenta(int ventaId) {
        String sqlEstadoVenta = "SELECT estado FROM ventas WHERE id = ?";
        String sqlCancelarVenta = "UPDATE ventas SET estado = 'CANCELADA' WHERE id = ?";
        String sqlReintegrarStock = "UPDATE productos SET stock = stock + ? WHERE id = ?";

        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        Connection conn = null;
        PreparedStatement stmtEstado = null;
        PreparedStatement stmtCancelar = null;
        PreparedStatement stmtReintegrar = null;

        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false); // TRANSACCIÓN DE ANULACIÓN

            // Verificar si la venta no estaba ya cancelada
            stmtEstado = conn.prepareStatement(sqlEstadoVenta);
            stmtEstado.setInt(1, ventaId);
            try (ResultSet rs = stmtEstado.executeQuery()) {
                if (rs.next()) {
                    if ("CANCELADA".equalsIgnoreCase(rs.getString("estado"))) {
                        throw new SQLException("La venta ya se encuentra cancelada.");
                    }
                } else {
                    throw new SQLException("No se encontró la venta especificada.");
                }
            }

            // 1. Devolver stock SOLO SI usarInventario es true
            if (usarInventario) {
                List<DetalleVenta> detalles = listarDetallesPorVenta(ventaId);
                stmtReintegrar = conn.prepareStatement(sqlReintegrarStock);
                for (DetalleVenta detalle : detalles) {
                    stmtReintegrar.setDouble(1, detalle.getCantidad());
                    stmtReintegrar.setInt(2, detalle.getProducto().getId());
                    stmtReintegrar.executeUpdate();
                }
            }

            // 2. Marcar la venta como CANCELADA
            stmtCancelar = conn.prepareStatement(sqlCancelarVenta);
            stmtCancelar.setInt(1, ventaId);
            stmtCancelar.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en rollback al cancelar venta: " + ex.getMessage());
                }
            }
            System.out.println("Error al cancelar la venta ID " + ventaId + ": " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(stmtEstado, stmtCancelar, stmtReintegrar, conn);
        }
    }

    @Override
    public List<Venta> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Venta> ventas = new ArrayList<>();

        // Cambiamos INNER JOIN a LEFT JOIN para no perder ventas sin cliente asignado
        // Y usamos la condición de rango exclusivo (< día siguiente a las 00:00:00)
        String sql = "SELECT v.id, v.fecha, v.total, v.estado, "
                + "c.id as cliente_id, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v LEFT JOIN clientes c ON v.cliente_id = c.id "
                + "WHERE v.fecha >= ? AND v.fecha < ? ORDER BY v.id DESC";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Inicio del día 00:00:00
            stmt.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));

            // Sumamos 1 día al límite superior y evaluamos con '<' (cubre hasta 23:59:59.999999)
            stmt.setTimestamp(2, Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al filtrar ventas por fechas: " + e.getMessage());
        }
        return ventas;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        int clienteId = rs.getInt("cliente_id");
        Cliente cliente = null;

        // Solo instanciamos el objeto Cliente si realmente existía una relación en la BD
        if (!rs.wasNull() && clienteId > 0) {
            cliente = new Cliente(
                    clienteId,
                    rs.getString("cliente_nombre"),
                    rs.getString("rfc"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion"),
                    rs.getString("cliente_estado")
            );
        } else {
            // Opcional: Instanciar un cliente genérico por defecto para evitar que la UI reciba null
            cliente = new Cliente();
            cliente.setId(0);
            cliente.setNombre("Público en General");
        }

        return new Venta(
                rs.getInt("id"),
                cliente,
                rs.getTimestamp("fecha").toLocalDateTime(),
                rs.getDouble("total"),
                rs.getString("estado")
        );
    }

    private void cerrarRecursos(AutoCloseable... recursos) {
        for (AutoCloseable r : recursos) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception e) {
                    // Cierre silencioso de conexión
                }
            }
        }
    }

    @Override
    public List buscarConFiltros(LocalDate inicio, LocalDate fin, Integer clienteId) {
        List ventas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT v.id, v.fecha, v.total, v.estado, "
                + "c.id as cliente_id, c.nombre as cliente_nombre, c.rfc, c.telefono, c.email, c.direccion, c.estado as cliente_estado "
                + "FROM ventas v INNER JOIN clientes c ON v.cliente_id = c.id WHERE 1=1 "
        );

        if (inicio != null) {
            sql.append("AND v.fecha >= ? ");
        }
        if (fin != null) {
            sql.append("AND v.fecha <= ? ");
        }
        if (clienteId != null && clienteId > 0) {
            sql.append("AND c.id = ? ");
        }
        sql.append("ORDER BY v.id DESC");

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (inicio != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(inicio.atStartOfDay()));
            }
            if (fin != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(fin.atTime(23, 59, 59)));
            }
            if (clienteId != null && clienteId > 0) {
                stmt.setInt(paramIndex++, clienteId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al filtrar historial de ventas: " + e.getMessage());
        }
        return ventas;
    }

}
