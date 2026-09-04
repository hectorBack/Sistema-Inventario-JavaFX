package com.inventario.model.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class MovimientoInventarioDTO {
    private final Integer id;
    private final Integer productoId;
    private final String codigoProducto;
    private final String nombreProducto;
    private final String tipoMovimiento;
    private final BigDecimal cantidad;
    private final String motivo;
    private final LocalDateTime fechaMovimiento;

    public MovimientoInventarioDTO(Integer id, Integer productoId, String codigoProducto, String nombreProducto,
            String tipoMovimiento, BigDecimal cantidad, String motivo, LocalDateTime fechaMovimiento) {
        this.id = id;
        this.productoId = productoId;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fechaMovimiento = fechaMovimiento;
    }

    public Integer getId() { return id; }
    public Integer getProductoId() { return productoId; }
    public String getCodigoProducto() { return codigoProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public BigDecimal getCantidad() { return cantidad; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
}
