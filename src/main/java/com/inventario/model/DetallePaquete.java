package com.inventario.model;

public class DetallePaquete {

    private Producto producto;
    private double cantidad;

    public DetallePaquete() {
    }

    public DetallePaquete(Producto producto, double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigoBarras() {
        return producto != null ? producto.getCodigoBarras() : "";
    }

    // --- AGREGAR ESTOS GETTERS PARA REFLECTION Y TABLAS ---
    public String getNombreProducto() {
        return producto != null ? producto.getNombre() : "";
    }

    public double getSubtotalCosto() {
        return producto != null ? (producto.getPrecioCompra() * cantidad) : 0.0;
    }
}
