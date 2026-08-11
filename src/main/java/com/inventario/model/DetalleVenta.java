package com.inventario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetalleVenta {

    private final IntegerProperty id;
    private final IntegerProperty ventaId;
    private final ObjectProperty<Producto> producto;
    private final StringProperty nombreProducto; // Auxiliar para la interfaz visual
    private final DoubleProperty cantidad;
    private final DoubleProperty precioUnitario;
    private final DoubleProperty subtotal;

    public DetalleVenta() {
        this.id = new SimpleIntegerProperty(0);
        this.ventaId = new SimpleIntegerProperty(0);
        this.producto = new SimpleObjectProperty<>(null);
        this.nombreProducto = new SimpleStringProperty("");
        this.cantidad = new SimpleDoubleProperty(0);
        this.precioUnitario = new SimpleDoubleProperty(0.0);
        this.subtotal = new SimpleDoubleProperty(0.0);
    }

    // Constructor para items en carrito (sin ID ni ventaId)
    public DetalleVenta(Producto producto, double cantidad, double precioUnitario) {
        this.id = new SimpleIntegerProperty(0);
        this.ventaId = new SimpleIntegerProperty(0);
        this.producto = new SimpleObjectProperty<>(producto);
        this.nombreProducto = new SimpleStringProperty(producto != null ? producto.getNombre() : "");
        this.cantidad = new SimpleDoubleProperty(cantidad);
        this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
        this.subtotal = new SimpleDoubleProperty(cantidad * precioUnitario);
    }

    public DetalleVenta(int id, int ventaId, Producto producto, double cantidad, double precioUnitario) {
        this.id = new SimpleIntegerProperty(id);
        this.ventaId = new SimpleIntegerProperty(ventaId);
        this.producto = new SimpleObjectProperty<>(producto);
        this.nombreProducto = new SimpleStringProperty(producto != null ? producto.getNombre() : "");
        this.cantidad = new SimpleDoubleProperty(cantidad);
        this.precioUnitario = new SimpleDoubleProperty(precioUnitario);
        this.subtotal = new SimpleDoubleProperty(cantidad * precioUnitario);
    }

    // Getters y Setters estándar
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public int getVentaId() {
        return ventaId.get();
    }

    public void setVentaId(int value) {
        ventaId.set(value);
    }

    public Producto getProducto() {
        return producto.get();
    }

    public void setProducto(Producto value) {
        this.producto.set(value);
        if (value != null) {
            this.nombreProducto.set(value.getNombre());
        }
    }

    public String getNombreProducto() {
        return nombreProducto.get();
    }

    public double getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(double value) {
        this.cantidad.set(value);
        this.subtotal.set(value * getPrecioUnitario());
    }

    public double getPrecioUnitario() {
        return precioUnitario.get();
    }

    public void setPrecioUnitario(double value) {
        this.precioUnitario.set(value);
        this.subtotal.set(getCantidad() * value);
    }

    public double getSubtotal() {
        double sub = this.cantidad.get() * this.precioUnitario.get();
        return Math.round(sub * 100.0) / 100.0;
    }

    // Properties para el TableView
    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty ventaIdProperty() {
        return ventaId;
    }

    public ObjectProperty<Producto> productoProperty() {
        return producto;
    }

    public StringProperty nombreProductoProperty() {
        return nombreProducto;
    }

    public DoubleProperty cantidadProperty() {
        return cantidad;
    }

    public DoubleProperty precioUnitarioProperty() {
        return precioUnitario;
    }

    public DoubleProperty subtotalProperty() {
        return subtotal;
    }

}
