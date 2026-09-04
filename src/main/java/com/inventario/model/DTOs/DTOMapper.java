package com.inventario.model.DTOs;

import com.inventario.model.Categoria;
import com.inventario.model.ArticulosPrecargados;
import com.inventario.model.Caja;
import com.inventario.model.ConexionConfig;
import com.inventario.model.Cliente;
import com.inventario.model.Empresa;
import com.inventario.model.Folio;
import com.inventario.model.MovimientoInventario;
import com.inventario.model.OpcionesHabilitadas;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.model.Promocion;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import com.inventario.model.InformacionBD;
import com.inventario.model.Cajero;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigDecimal;

public final class DTOMapper {

    private DTOMapper() {
    }

    public static CategoriaDTO toDTO(Categoria categoria) {
        return categoria == null ? null : new CategoriaDTO(
                categoria.getId(), categoria.getNombre(), categoria.getEstado());
    }

    public static Categoria toModel(CategoriaDTO dto) {
        return dto == null ? null : new Categoria(
            valueOrZero(dto.getId()), dto.getNombre(), dto.getEstado());
    }

    public static ProveedorDTO toDTO(Proveedor proveedor) {
        return proveedor == null ? null : new ProveedorDTO(
                proveedor.getId(), proveedor.getNombre(), proveedor.getContacto(), proveedor.getTelefono(),
                proveedor.getEmail(), proveedor.getEstado());
    }

    public static Proveedor toModel(ProveedorDTO dto) {
        return dto == null ? null : new Proveedor(
                valueOrZero(dto.getId()), dto.getNombre(), dto.getContacto(), dto.getTelefono(), dto.getEmail(),
                dto.getEstado());
    }

    public static ClienteDTO toDTO(Cliente cliente) {
        return cliente == null ? null : new ClienteDTO(
                cliente.getId(), cliente.getNombre(), cliente.getRfc(), cliente.getTelefono(), cliente.getEmail(),
                cliente.getDireccion(), cliente.getEstado());
    }

    public static Cliente toModel(ClienteDTO dto) {
        return dto == null ? null : new Cliente(
                valueOrZero(dto.getId()), dto.getNombre(), dto.getRfc(), dto.getTelefono(), dto.getEmail(),
                dto.getDireccion(), dto.getEstado());
    }

    public static ProductoDTO toDTO(Producto producto) {
        return producto == null ? null : new ProductoDTO(
                producto.getId(), producto.getCodigoBarras(), producto.getNombre(), producto.getDescripcion(),
                decimal(producto.getPrecio()), decimal(producto.getPrecioMayoreo()), decimal(producto.getPrecioCompra()),
                decimal(producto.getPorcentajeGanancia()), decimal(producto.getStock()), decimal(producto.getStockMinimo()),
                producto.getTipoVenta(), producto.getEstado(),
                producto.getCategoria() == null ? null : producto.getCategoria().getId(),
                producto.getProveedor() == null ? null : producto.getProveedor().getId());
    }

    public static Producto toModel(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        Categoria categoria = dto.getCategoriaId() == null ? null : new Categoria(dto.getCategoriaId(), "", "ACTIVO");
        Proveedor proveedor = dto.getProveedorId() == null ? null : new Proveedor(dto.getProveedorId(), "", "", "", "", "ACTIVO");
        return new Producto(valueOrZero(dto.getId()), dto.getCodigoBarras(), dto.getNombre(), dto.getDescripcion(),
                number(dto.getPrecio()), number(dto.getPrecioMayoreo()), number(dto.getPrecioCompra()),
                number(dto.getPorcentajeGanancia()), number(dto.getStock()), number(dto.getStockMinimo()),
                dto.getTipoVenta(), dto.getEstado(), categoria, proveedor);
    }

    public static EmpresaDTO toDTO(Empresa empresa) {
        return empresa == null ? null : new EmpresaDTO(empresa.getId(), empresa.getNombreEmpresa(), empresa.getRfc(),
                empresa.getTelefono(), empresa.getDireccion(), empresa.getLogoPath(), empresa.getFechaActualizacion());
    }

    public static Empresa toModel(EmpresaDTO dto) {
        return dto == null ? null : new Empresa(dto.getId() == null ? 0 : dto.getId(), dto.getNombreEmpresa(), dto.getRfc(),
                dto.getTelefono(), dto.getDireccion(), dto.getLogoPath(), dto.getFechaActualizacion());
    }

    public static FolioDTO toDTO(Folio folio) {
        return folio == null ? null : new FolioDTO(folio.getId(), folio.getNombre(), folio.getModulo(), folio.getSerie(),
                folio.getFolioActual(), folio.getLongitudCeros(), folio.getEstado());
    }

    public static Folio toModel(FolioDTO dto) {
        return dto == null ? null : new Folio(valueOrZero(dto.getId()), dto.getNombre(), dto.getModulo(), dto.getSerie(),
                dto.getFolioActual(), dto.getLongitudCeros(), dto.getEstado());
    }

    public static PromocionDTO toDTO(Promocion promocion) {
        return promocion == null ? null : new PromocionDTO(promocion.getId(), promocion.getNombre(),
                promocion.getCodigoBarrasProducto(), decimal(promocion.getCantidadDesde()), decimal(promocion.getCantidadHasta()),
                decimal(promocion.getPrecioPromocion()), decimal(promocion.getPrecioNormal()), decimal(promocion.getPrecioCosto()),
                decimal(promocion.getPrecioUnitario()), promocion.getEstado());
    }

    public static Promocion toModel(PromocionDTO dto) {
        return dto == null ? null : new Promocion(valueOrZero(dto.getId()), dto.getNombre(), dto.getCodigoBarrasProducto(),
                number(dto.getCantidadDesde()), number(dto.getCantidadHasta()), number(dto.getPrecioPromocion()),
                number(dto.getPrecioNormal()), number(dto.getPrecioCosto()), number(dto.getPrecioUnitario()), dto.getEstado());
    }

    public static OpcionesHabilitadasDTO toDTO(OpcionesHabilitadas opciones) {
        return opciones == null ? null : new OpcionesHabilitadasDTO(opciones.getId(), opciones.isUsarInventario(),
                opciones.isOfrecerCredito(), opciones.isProductoComun(), opciones.isCalcularPrecio(),
                decimal(opciones.getMargenGanancia()), opciones.isHabilitarRedondeo(), opciones.getTipoRedondeo());
    }

    public static OpcionesHabilitadas toModel(OpcionesHabilitadasDTO dto) {
        return dto == null ? null : new OpcionesHabilitadas(valueOrZero(dto.getId()), dto.isUsarInventario(),
                dto.isOfrecerCredito(), dto.isProductoComun(), dto.isCalcularPrecio(), number(dto.getMargenGanancia()),
                dto.isHabilitarRedondeo(), dto.getTipoRedondeo());
    }

    public static ArticulosPrecargadosDTO toDTO(ArticulosPrecargados articulo) {
        return articulo == null ? null : new ArticulosPrecargadosDTO(articulo.getCodigoBarras(), articulo.getNombre(),
                articulo.getDescripcion(), decimal(articulo.getPrecioCompra()), decimal(articulo.getPrecioVenta()),
                decimal(articulo.getStockInicial()), articulo.getCategoria(), articulo.isValido(), articulo.getMensajeError());
    }

    public static ArticulosPrecargados toModel(ArticulosPrecargadosDTO dto) {
        if (dto == null) {
            return null;
        }
        ArticulosPrecargados articulo = new ArticulosPrecargados(dto.getCodigoBarras(), dto.getNombre(), dto.getDescripcion(),
                number(dto.getPrecioCompra()), number(dto.getPrecioVenta()), (int) number(dto.getStockInicial()), dto.getCategoria());
        articulo.setValido(dto.isValido());
        articulo.setMensajeError(dto.getMensajeError());
        return articulo;
    }

    public static CajaDTO toDTO(Caja caja) {
        return caja == null ? null : new CajaDTO(caja.getId(), caja.getNombre(), caja.getEstado(), caja.getTipo(),
                caja.getCajaPadreId(), caja.getFechaUltimoAcceso());
    }

    public static Caja toModel(CajaDTO dto) {
        return dto == null ? null : new Caja(valueOrZero(dto.getId()), dto.getNombre(), dto.getEstado(), dto.getTipo(),
                dto.getCajaPadreId(), dto.getFechaUltimoAcceso());
    }

    public static MovimientoInventarioDTO toDTO(MovimientoInventario movimiento) {
        return movimiento == null ? null : new MovimientoInventarioDTO(movimiento.getId(), movimiento.getProductoId(),
                movimiento.getCodigoProducto(), movimiento.getNombreProducto(), movimiento.getTipoMovimiento(),
                decimal(movimiento.getCantidad()), movimiento.getMotivo(), movimiento.getFechaMovimiento());
    }

    public static MovimientoInventario toModel(MovimientoInventarioDTO dto) {
        if (dto == null) {
            return null;
        }
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setId(valueOrZero(dto.getId()));
        movimiento.setProductoId(valueOrZero(dto.getProductoId()));
        movimiento.setCodigoProducto(dto.getCodigoProducto());
        movimiento.setNombreProducto(dto.getNombreProducto());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setCantidad(number(dto.getCantidad()));
        movimiento.setMotivo(dto.getMotivo());
        movimiento.setFechaMovimiento(dto.getFechaMovimiento());
        return movimiento;
    }

    public static VentaDTO toDTO(Venta venta) {
        return venta == null ? null : new VentaDTO(venta.getId(), venta.getCliente() == null ? null : venta.getCliente().getId(),
                venta.getCliente() == null ? null : venta.getCliente().getNombre(), venta.getFecha(),
                decimal(venta.getTotal()), venta.getEstado());
    }

    public static Venta toModel(VentaDTO dto) {
        if (dto == null) return null;
        Cliente cliente = dto.getClienteId() == null ? null : new Cliente(dto.getClienteId(),
                dto.getClienteNombre() == null ? "" : dto.getClienteNombre(), "", "", "", "", "ACTIVO");
        return new Venta(valueOrZero(dto.getId()), cliente, dto.getFecha(), number(dto.getTotal()), dto.getEstado());
    }

    public static DetalleVentaDTO toDTO(DetalleVenta detalle) {
        return detalle == null ? null : new DetalleVentaDTO(detalle.getId(), detalle.getVentaId(),
                detalle.getProducto() == null ? null : detalle.getProducto().getId(), detalle.getNombreProducto(),
                decimal(detalle.getCantidad()), decimal(detalle.getPrecioUnitario()), decimal(detalle.getSubtotal()));
    }

    public static DetalleVenta toModel(DetalleVentaDTO dto) {
        if (dto == null) return null;
        Producto producto = dto.getProductoId() == null ? null : new Producto();
        if (producto != null) {
            producto.setId(dto.getProductoId());
            producto.setNombre(dto.getNombreProducto() == null ? "" : dto.getNombreProducto());
        } else if (dto.getNombreProducto() != null && !dto.getNombreProducto().isBlank()) {
            producto = new Producto();
            producto.setNombre(dto.getNombreProducto());
        }
        DetalleVenta detalle = new DetalleVenta(valueOrZero(dto.getId()), valueOrZero(dto.getVentaId()), producto,
                number(dto.getCantidad()), number(dto.getPrecioUnitario()));
        return detalle;
    }

    public static ConexionConfigDTO toDTO(ConexionConfig config) {
        return config == null ? null : new ConexionConfigDTO(config.getHost(), config.getPuerto(), config.getBaseDatos(),
                config.getUsuario(), config.getContrasena(), config.getRutaPgDump());
    }

    public static ConexionConfig toModel(ConexionConfigDTO dto) {
        return dto == null ? null : new ConexionConfig(dto.getHost(), dto.getPuerto(), dto.getBaseDatos(), dto.getUsuario(),
                dto.getContrasena(), dto.getRutaPgDump());
    }

    public static InformacionBDDTO toDTO(InformacionBD info) {
        return info == null ? null : new InformacionBDDTO(info.getMotorVersion(), info.getTamanioBD(), info.getTotalProductos(),
                info.getTotalVentas(), info.getTotalCajeros(), info.isEstadoConexion());
    }

    public static InformacionBD toModel(InformacionBDDTO dto) {
        if (dto == null) return null;
        InformacionBD info = new InformacionBD();
        info.setMotorVersion(dto.getMotorVersion());
        info.setTamanioBD(dto.getTamanioBD());
        info.setTotalProductos(dto.getTotalProductos());
        info.setTotalVentas(dto.getTotalVentas());
        info.setTotalCajeros(dto.getTotalCajeros());
        info.setEstadoConexion(dto.isEstadoConexion());
        return info;
    }

    public static CajeroDTO toDTO(Cajero cajero) {
        if (cajero == null) return null;
        Map<String, Boolean> permisos = new LinkedHashMap<>();
        for (Method metodo : Cajero.class.getMethods()) {
            if (metodo.getName().startsWith("isPerm") && metodo.getParameterCount() == 0) {
                try {
                    permisos.put(metodo.getName().substring(2), (Boolean) metodo.invoke(cajero));
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }
        return new CajeroDTO(cajero.getId(), cajero.getUsuario(), cajero.getContrasena(), cajero.getNombreCompleto(),
                cajero.isActivo(), permisos);
    }

    public static Cajero toModel(CajeroDTO dto) {
        if (dto == null) return null;
        Cajero cajero = new Cajero(valueOrZero(dto.getId()), dto.getUsuario(), dto.getContrasena(),
                dto.getNombreCompleto(), dto.isActivo(), false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false);
        for (Map.Entry<String, Boolean> permiso : dto.getPermisos().entrySet()) {
            try {
                Method setter = Cajero.class.getMethod("set" + permiso.getKey(), boolean.class);
                setter.invoke(cajero, Boolean.TRUE.equals(permiso.getValue()));
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return cajero;
    }

    private static BigDecimal decimal(double value) {
        return BigDecimal.valueOf(value);
    }

    private static double number(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
