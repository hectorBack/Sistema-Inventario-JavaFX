package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cajero {

    private final IntegerProperty id;
    private final StringProperty usuario;
    private final StringProperty contrasena;
    private final StringProperty nombreCompleto;
    private final BooleanProperty activo;

    // Permisos: Ventas
    private final BooleanProperty permUtilizarProductoComun;
    private final BooleanProperty permAplicarMayoreo;
    private final BooleanProperty permAplicarDescuento;
    private final BooleanProperty permRevisarHistorialVentas;
    private final BooleanProperty permRegistrarEntradasEfectivo;
    private final BooleanProperty permRegistrarSalidasEfectivo;
    private final BooleanProperty permCobrarTicket;
    private final BooleanProperty permCobrarCredito;
    private final BooleanProperty permCancelarTickets;
    private final BooleanProperty permEliminarArticulosVenta;
    private final BooleanProperty permFacturarVerFacturas;
    private final BooleanProperty permVenderPagoServicio;
    private final BooleanProperty permCancelarPagoServicio;
    private final BooleanProperty permVenderRecargas;

    // Permisos: Clientes
    private final BooleanProperty permCrearModificarEliminarClientes;
    private final BooleanProperty permAsignarClienteVenta;
    private final BooleanProperty permAsignarRemoverCredito;
    private final BooleanProperty permVerCuentaAbonosReportes;

    // Permisos: Productos
    private final BooleanProperty permCrearNuevosProductos;
    private final BooleanProperty permModificarProductos;
    private final BooleanProperty permEliminarProductos;
    private final BooleanProperty permVerReporteVentas;
    private final BooleanProperty permCrearPromociones;
    private final BooleanProperty permModificarVarios;

    // Permisos: Inventario
    private final BooleanProperty permAgregarMercancia;
    private final BooleanProperty permVerReportesExistencias;
    private final BooleanProperty permVerMovimientoInventarios;
    private final BooleanProperty permAjustarInventario;

    // Permisos: Otros
    private final BooleanProperty permCorteTurnoEfectivo;
    private final BooleanProperty permCorteDiaTodosTurnos;
    private final BooleanProperty permVerGananciaDia;
    private final BooleanProperty permCambiarConfiguracion;
    private final BooleanProperty permAccederReportesVentasGanancias;
    private final BooleanProperty permCrearOrdenesCompra;
    private final BooleanProperty permRecibirOrdenesCompra;

    // Constructor completo
    public Cajero(int id, String usuario, String contrasena, String nombreCompleto, boolean activo,
            boolean permUtilizarProductoComun, boolean permAplicarMayoreo, boolean permAplicarDescuento,
            boolean permRevisarHistorialVentas, boolean permRegistrarEntradasEfectivo, boolean permRegistrarSalidasEfectivo,
            boolean permCobrarTicket, boolean permCobrarCredito, boolean permCancelarTickets,
            boolean permEliminarArticulosVenta, boolean permFacturarVerFacturas, boolean permVenderPagoServicio,
            boolean permCancelarPagoServicio, boolean permVenderRecargas, boolean permCrearModificarEliminarClientes,
            boolean permAsignarClienteVenta, boolean permAsignarRemoverCredito, boolean permVerCuentaAbonosReportes,
            boolean permCrearNuevosProductos, boolean permModificarProductos, boolean permEliminarProductos,
            boolean permVerReporteVentas, boolean permCrearPromociones, boolean permModificarVarios,
            boolean permAgregarMercancia, boolean permVerReportesExistencias, boolean permVerMovimientoInventarios,
            boolean permAjustarInventario, boolean permCorteTurnoEfectivo, boolean permCorteDiaTodosTurnos,
            boolean permVerGananciaDia, boolean permCambiarConfiguracion, boolean permAccederReportesVentasGanancias,
            boolean permCrearOrdenesCompra, boolean permRecibirOrdenesCompra) {

        this.id = new SimpleIntegerProperty(id);
        this.usuario = new SimpleStringProperty(usuario);
        this.contrasena = new SimpleStringProperty(contrasena);
        this.nombreCompleto = new SimpleStringProperty(nombreCompleto);
        this.activo = new SimpleBooleanProperty(activo);

        // Ventas
        this.permUtilizarProductoComun = new SimpleBooleanProperty(permUtilizarProductoComun);
        this.permAplicarMayoreo = new SimpleBooleanProperty(permAplicarMayoreo);
        this.permAplicarDescuento = new SimpleBooleanProperty(permAplicarDescuento);
        this.permRevisarHistorialVentas = new SimpleBooleanProperty(permRevisarHistorialVentas);
        this.permRegistrarEntradasEfectivo = new SimpleBooleanProperty(permRegistrarEntradasEfectivo);
        this.permRegistrarSalidasEfectivo = new SimpleBooleanProperty(permRegistrarSalidasEfectivo);
        this.permCobrarTicket = new SimpleBooleanProperty(permCobrarTicket);
        this.permCobrarCredito = new SimpleBooleanProperty(permCobrarCredito);
        this.permCancelarTickets = new SimpleBooleanProperty(permCancelarTickets);
        this.permEliminarArticulosVenta = new SimpleBooleanProperty(permEliminarArticulosVenta);
        this.permFacturarVerFacturas = new SimpleBooleanProperty(permFacturarVerFacturas);
        this.permVenderPagoServicio = new SimpleBooleanProperty(permVenderPagoServicio);
        this.permCancelarPagoServicio = new SimpleBooleanProperty(permCancelarPagoServicio);
        this.permVenderRecargas = new SimpleBooleanProperty(permVenderRecargas);

        // Clientes
        this.permCrearModificarEliminarClientes = new SimpleBooleanProperty(permCrearModificarEliminarClientes);
        this.permAsignarClienteVenta = new SimpleBooleanProperty(permAsignarClienteVenta);
        this.permAsignarRemoverCredito = new SimpleBooleanProperty(permAsignarRemoverCredito);
        this.permVerCuentaAbonosReportes = new SimpleBooleanProperty(permVerCuentaAbonosReportes);

        // Productos
        this.permCrearNuevosProductos = new SimpleBooleanProperty(permCrearNuevosProductos);
        this.permModificarProductos = new SimpleBooleanProperty(permModificarProductos);
        this.permEliminarProductos = new SimpleBooleanProperty(permEliminarProductos);
        this.permVerReporteVentas = new SimpleBooleanProperty(permVerReporteVentas);
        this.permCrearPromociones = new SimpleBooleanProperty(permCrearPromociones);
        this.permModificarVarios = new SimpleBooleanProperty(permModificarVarios);

        // Inventario
        this.permAgregarMercancia = new SimpleBooleanProperty(permAgregarMercancia);
        this.permVerReportesExistencias = new SimpleBooleanProperty(permVerReportesExistencias);
        this.permVerMovimientoInventarios = new SimpleBooleanProperty(permVerMovimientoInventarios);
        this.permAjustarInventario = new SimpleBooleanProperty(permAjustarInventario);

        // Otros
        this.permCorteTurnoEfectivo = new SimpleBooleanProperty(permCorteTurnoEfectivo);
        this.permCorteDiaTodosTurnos = new SimpleBooleanProperty(permCorteDiaTodosTurnos);
        this.permVerGananciaDia = new SimpleBooleanProperty(permVerGananciaDia);
        this.permCambiarConfiguracion = new SimpleBooleanProperty(permCambiarConfiguracion);
        this.permAccederReportesVentasGanancias = new SimpleBooleanProperty(permAccederReportesVentasGanancias);
        this.permCrearOrdenesCompra = new SimpleBooleanProperty(permCrearOrdenesCompra);
        this.permRecibirOrdenesCompra = new SimpleBooleanProperty(permRecibirOrdenesCompra);
    }

    // Constructor sin ID (Para registros nuevos)
    public Cajero(String usuario, String contrasena, String nombreCompleto) {
        this(0, usuario, contrasena, nombreCompleto, true,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false,
                false, false, false, false, false, false,
                false, false, false, false,
                false, false, false, false, false, false, false);
    }

    // --- GETTERS Y SETTERS ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String value) {
        usuario.set(value);
    }

    public String getContrasena() {
        return contrasena.get();
    }

    public void setContrasena(String value) {
        contrasena.set(value);
    }

    public String getNombreCompleto() {
        return nombreCompleto.get();
    }

    public void setNombreCompleto(String value) {
        nombreCompleto.set(value);
    }

    public boolean isActivo() {
        return activo.get();
    }

    public void setActivo(boolean value) {
        activo.set(value);
    }

    // Ventas Getters & Setters
    public boolean isPermUtilizarProductoComun() {
        return permUtilizarProductoComun.get();
    }

    public void setPermUtilizarProductoComun(boolean v) {
        permUtilizarProductoComun.set(v);
    }

    public boolean isPermAplicarMayoreo() {
        return permAplicarMayoreo.get();
    }

    public void setPermAplicarMayoreo(boolean v) {
        permAplicarMayoreo.set(v);
    }

    public boolean isPermAplicarDescuento() {
        return permAplicarDescuento.get();
    }

    public void setPermAplicarDescuento(boolean v) {
        permAplicarDescuento.set(v);
    }

    public boolean isPermRevisarHistorialVentas() {
        return permRevisarHistorialVentas.get();
    }

    public void setPermRevisarHistorialVentas(boolean v) {
        permRevisarHistorialVentas.set(v);
    }

    public boolean isPermRegistrarEntradasEfectivo() {
        return permRegistrarEntradasEfectivo.get();
    }

    public void setPermRegistrarEntradasEfectivo(boolean v) {
        permRegistrarEntradasEfectivo.set(v);
    }

    public boolean isPermRegistrarSalidasEfectivo() {
        return permRegistrarSalidasEfectivo.get();
    }

    public void setPermRegistrarSalidasEfectivo(boolean v) {
        permRegistrarSalidasEfectivo.set(v);
    }

    public boolean isPermCobrarTicket() {
        return permCobrarTicket.get();
    }

    public void setPermCobrarTicket(boolean v) {
        permCobrarTicket.set(v);
    }

    public boolean isPermCobrarCredito() {
        return permCobrarCredito.get();
    }

    public void setPermCobrarCredito(boolean v) {
        permCobrarCredito.set(v);
    }

    public boolean isPermCancelarTickets() {
        return permCancelarTickets.get();
    }

    public void setPermCancelarTickets(boolean v) {
        permCancelarTickets.set(v);
    }

    public boolean isPermEliminarArticulosVenta() {
        return permEliminarArticulosVenta.get();
    }

    public void setPermEliminarArticulosVenta(boolean v) {
        permEliminarArticulosVenta.set(v);
    }

    public boolean isPermFacturarVerFacturas() {
        return permFacturarVerFacturas.get();
    }

    public void setPermFacturarVerFacturas(boolean v) {
        permFacturarVerFacturas.set(v);
    }

    public boolean isPermVenderPagoServicio() {
        return permVenderPagoServicio.get();
    }

    public void setPermVenderPagoServicio(boolean v) {
        permVenderPagoServicio.set(v);
    }

    public boolean isPermCancelarPagoServicio() {
        return permCancelarPagoServicio.get();
    }

    public void setPermCancelarPagoServicio(boolean v) {
        permCancelarPagoServicio.set(v);
    }

    public boolean isPermVenderRecargas() {
        return permVenderRecargas.get();
    }

    public void setPermVenderRecargas(boolean v) {
        permVenderRecargas.set(v);
    }

    // Clientes Getters & Setters
    public boolean isPermCrearModificarEliminarClientes() {
        return permCrearModificarEliminarClientes.get();
    }

    public void setPermCrearModificarEliminarClientes(boolean v) {
        permCrearModificarEliminarClientes.set(v);
    }

    public boolean isPermAsignarClienteVenta() {
        return permAsignarClienteVenta.get();
    }

    public void setPermAsignarClienteVenta(boolean v) {
        permAsignarClienteVenta.set(v);
    }

    public boolean isPermAsignarRemoverCredito() {
        return permAsignarRemoverCredito.get();
    }

    public void setPermAsignarRemoverCredito(boolean v) {
        permAsignarRemoverCredito.set(v);
    }

    public boolean isPermVerCuentaAbonosReportes() {
        return permVerCuentaAbonosReportes.get();
    }

    public void setPermVerCuentaAbonosReportes(boolean v) {
        permVerCuentaAbonosReportes.set(v);
    }

    // Productos Getters & Setters
    public boolean isPermCrearNuevosProductos() {
        return permCrearNuevosProductos.get();
    }

    public void setPermCrearNuevosProductos(boolean v) {
        permCrearNuevosProductos.set(v);
    }

    public boolean isPermModificarProductos() {
        return permModificarProductos.get();
    }

    public void setPermModificarProductos(boolean v) {
        permModificarProductos.set(v);
    }

    public boolean isPermEliminarProductos() {
        return permEliminarProductos.get();
    }

    public void setPermEliminarProductos(boolean v) {
        permEliminarProductos.set(v);
    }

    public boolean isPermVerReporteVentas() {
        return permVerReporteVentas.get();
    }

    public void setPermVerReporteVentas(boolean v) {
        permVerReporteVentas.set(v);
    }

    public boolean isPermCrearPromociones() {
        return permCrearPromociones.get();
    }

    public void setPermCrearPromociones(boolean v) {
        permCrearPromociones.set(v);
    }

    public boolean isPermModificarVarios() {
        return permModificarVarios.get();
    }

    public void setPermModificarVarios(boolean v) {
        permModificarVarios.set(v);
    }

    // Inventario Getters & Setters
    public boolean isPermAgregarMercancia() {
        return permAgregarMercancia.get();
    }

    public void setPermAgregarMercancia(boolean v) {
        permAgregarMercancia.set(v);
    }

    public boolean isPermVerReportesExistencias() {
        return permVerReportesExistencias.get();
    }

    public void setPermVerReportesExistencias(boolean v) {
        permVerReportesExistencias.set(v);
    }

    public boolean isPermVerMovimientoInventarios() {
        return permVerMovimientoInventarios.get();
    }

    public void setPermVerMovimientoInventarios(boolean v) {
        permVerMovimientoInventarios.set(v);
    }

    public boolean isPermAjustarInventario() {
        return permAjustarInventario.get();
    }

    public void setPermAjustarInventario(boolean v) {
        permAjustarInventario.set(v);
    }

    // Otros Getters & Setters
    public boolean isPermCorteTurnoEfectivo() {
        return permCorteTurnoEfectivo.get();
    }

    public void setPermCorteTurnoEfectivo(boolean v) {
        permCorteTurnoEfectivo.set(v);
    }

    public boolean isPermCorteDiaTodosTurnos() {
        return permCorteDiaTodosTurnos.get();
    }

    public void setPermCorteDiaTodosTurnos(boolean v) {
        permCorteDiaTodosTurnos.set(v);
    }

    public boolean isPermVerGananciaDia() {
        return permVerGananciaDia.get();
    }

    public void setPermVerGananciaDia(boolean v) {
        permVerGananciaDia.set(v);
    }

    public boolean isPermCambiarConfiguracion() {
        return permCambiarConfiguracion.get();
    }

    public void setPermCambiarConfiguracion(boolean v) {
        permCambiarConfiguracion.set(v);
    }

    public boolean isPermAccederReportesVentasGanancias() {
        return permAccederReportesVentasGanancias.get();
    }

    public void setPermAccederReportesVentasGanancias(boolean v) {
        permAccederReportesVentasGanancias.set(v);
    }

    public boolean isPermCrearOrdenesCompra() {
        return permCrearOrdenesCompra.get();
    }

    public void setPermCrearOrdenesCompra(boolean v) {
        permCrearOrdenesCompra.set(v);
    }

    public boolean isPermRecibirOrdenesCompra() {
        return permRecibirOrdenesCompra.get();
    }

    public void setPermRecibirOrdenesCompra(boolean v) {
        permRecibirOrdenesCompra.set(v);
    }

    // --- PROPIEDADES ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    public StringProperty contrasenaProperty() {
        return contrasena;
    }

    public StringProperty nombreCompletoProperty() {
        return nombreCompleto;
    }

    public BooleanProperty activoProperty() {
        return activo;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
