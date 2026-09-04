package com.inventario.model.DTOs;

import java.math.BigDecimal;

public final class ArticulosPrecargadosDTO {
    private final String codigoBarras;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal precioCompra;
    private final BigDecimal precioVenta;
    private final BigDecimal stockInicial;
    private final String categoria;
    private final boolean valido;
    private final String mensajeError;

    public ArticulosPrecargadosDTO(String codigoBarras, String nombre, String descripcion, BigDecimal precioCompra,
            BigDecimal precioVenta, BigDecimal stockInicial, String categoria, boolean valido, String mensajeError) {
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockInicial = stockInicial;
        this.categoria = categoria;
        this.valido = valido;
        this.mensajeError = mensajeError;
    }

    public String getCodigoBarras() { return codigoBarras; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public BigDecimal getStockInicial() { return stockInicial; }
    public String getCategoria() { return categoria; }
    public boolean isValido() { return valido; }
    public String getMensajeError() { return mensajeError; }
}
