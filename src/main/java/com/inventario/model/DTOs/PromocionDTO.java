package com.inventario.model.DTOs;

import java.math.BigDecimal;

public final class PromocionDTO {
    private final Integer id;
    private final String nombre;
    private final String codigoBarrasProducto;
    private final BigDecimal cantidadDesde;
    private final BigDecimal cantidadHasta;
    private final BigDecimal precioPromocion;
    private final BigDecimal precioNormal;
    private final BigDecimal precioCosto;
    private final BigDecimal precioUnitario;
    private final String estado;

    public PromocionDTO(Integer id, String nombre, String codigoBarrasProducto, BigDecimal cantidadDesde,
            BigDecimal cantidadHasta, BigDecimal precioPromocion, BigDecimal precioNormal, BigDecimal precioCosto,
            BigDecimal precioUnitario, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.codigoBarrasProducto = codigoBarrasProducto;
        this.cantidadDesde = cantidadDesde;
        this.cantidadHasta = cantidadHasta;
        this.precioPromocion = precioPromocion;
        this.precioNormal = precioNormal;
        this.precioCosto = precioCosto;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCodigoBarrasProducto() { return codigoBarrasProducto; }
    public BigDecimal getCantidadDesde() { return cantidadDesde; }
    public BigDecimal getCantidadHasta() { return cantidadHasta; }
    public BigDecimal getPrecioPromocion() { return precioPromocion; }
    public BigDecimal getPrecioNormal() { return precioNormal; }
    public BigDecimal getPrecioCosto() { return precioCosto; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public String getEstado() { return estado; }
}
