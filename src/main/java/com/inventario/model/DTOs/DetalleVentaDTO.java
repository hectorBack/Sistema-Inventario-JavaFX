package com.inventario.model.DTOs;

import java.math.BigDecimal;

public final class DetalleVentaDTO {

    private final Integer id;
    private final Integer ventaId;
    private final Integer productoId;
    private final String nombreProducto;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal subtotal;

    public DetalleVentaDTO(Integer id, Integer ventaId, Integer productoId, String nombreProducto,
            BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.id = id;
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVentaId() {
        return ventaId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getCantidadFormateada() {
        if (cantidad == null) {
            return "0";
        }
        return cantidad.stripTrailingZeros().toPlainString();
    }
}
