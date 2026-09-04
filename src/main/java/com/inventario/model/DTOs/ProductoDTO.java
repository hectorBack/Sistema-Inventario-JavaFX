package com.inventario.model.DTOs;

import java.math.BigDecimal;

public final class ProductoDTO {

    private final Integer id;
    private final String codigoBarras;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal precio;
    private final BigDecimal precioMayoreo;
    private final BigDecimal precioCompra;
    private final BigDecimal porcentajeGanancia;
    private final BigDecimal stock;
    private final BigDecimal stockMinimo;
    private final String tipoVenta;
    private final String estado;
    private final Integer categoriaId;
    private final String categoriaNombre;
    private final Integer proveedorId;
    private final String proveedorNombre;

    public ProductoDTO(Integer id, String codigoBarras, String nombre, String descripcion, BigDecimal precio, BigDecimal precioMayoreo, BigDecimal precioCompra, BigDecimal porcentajeGanancia, BigDecimal stock, BigDecimal stockMinimo, String tipoVenta, String estado, Integer categoriaId, String categoriaNombre, Integer proveedorId, String proveedorNombre) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.precioMayoreo = precioMayoreo;
        this.precioCompra = precioCompra;
        this.porcentajeGanancia = porcentajeGanancia;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.tipoVenta = tipoVenta;
        this.estado = estado;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public BigDecimal getPrecioMayoreo() {
        return precioMayoreo;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public BigDecimal getPorcentajeGanancia() {
        return porcentajeGanancia;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public String getEstado() {
        return estado;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

}
