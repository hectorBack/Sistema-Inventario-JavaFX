package com.inventario.model.DTOs;

import java.math.BigDecimal;

public final class DetallePaqueteDTO {
    private final Integer productoId;
    private final BigDecimal cantidad;

    public DetallePaqueteDTO(Integer productoId, BigDecimal cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Integer getProductoId() { return productoId; }
    public BigDecimal getCantidad() { return cantidad; }
}
