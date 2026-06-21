package com.inventario.model;

import java.time.LocalDateTime;

public class MovimientoInventario {

    private int id;
    private int productoId;
    private String nombreProducto; // Campo auxiliar para mostrar de forma amigable en la tabla FX
    private String tipoMovimiento; // "ENTRADA" o "SALIDA"
    private int cantidad;
    private String motivo;
    private LocalDateTime fechaMovimiento;

    // Constructor vacío
    public MovimientoInventario() {
    }

    // Constructor para insertar nuevos registros
    public MovimientoInventario(int productoId, String tipoMovimiento, int cantidad, String motivo) {
        this.productoId = productoId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    // Constructor completo para listar
    public MovimientoInventario(int id, int productoId, String nombreProducto, String tipoMovimiento, int cantidad, String motivo, LocalDateTime fechaMovimiento) {
        this.id = id;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fechaMovimiento = fechaMovimiento;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

}
