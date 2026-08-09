package com.inventario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Producto {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final DoubleProperty precio; // Precio Venta
    private final IntegerProperty stock;
    private final StringProperty estado;
    private final StringProperty codigoBarras;
    private final DoubleProperty precioCompra;
    private final IntegerProperty stockMinimo;
    private final StringProperty descripcion;

    private final ObjectProperty<Categoria> categoria;
    private final ObjectProperty<Proveedor> proveedor;

    // Constructor completo (Para cuando traes datos de la BD)
    // Constructor completo actualizado (con Categoría)
    public Producto(int id, String codigoBarras, String nombre, String descripcion, double precio, double precioCompra,
            int stock, int stockMinimo, String estado, Categoria categoria, Proveedor proveedor) {
        this.id = new SimpleIntegerProperty(id);
        this.codigoBarras = new SimpleStringProperty(codigoBarras);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.precio = new SimpleDoubleProperty(precio);
        this.precioCompra = new SimpleDoubleProperty(precioCompra);
        this.stock = new SimpleIntegerProperty(stock);
        this.stockMinimo = new SimpleIntegerProperty(stockMinimo);
        this.estado = new SimpleStringProperty(estado);
        this.categoria = new SimpleObjectProperty<>(categoria);
        this.proveedor = new SimpleObjectProperty<>(proveedor);
    }

    public Producto(IntegerProperty id, StringProperty nombre, DoubleProperty precio, IntegerProperty stock, StringProperty estado, StringProperty codigoBarras, DoubleProperty precioCompra, IntegerProperty stockMinimo, StringProperty descripcion, ObjectProperty<Categoria> categoria, ObjectProperty<Proveedor> proveedor) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
        this.codigoBarras = codigoBarras;
        this.precioCompra = precioCompra;
        this.stockMinimo = stockMinimo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.proveedor = proveedor;
    }
    

    // Constructor sin ID actualizado
    public Producto(String codigoBarras, String nombre, String descripcion, double precio, double precioCompra,
            int stock, int stockMinimo, String estado, Categoria categoria, Proveedor proveedor) {
        this(0, codigoBarras, nombre, descripcion, precio, precioCompra, stock, stockMinimo, estado, categoria, proveedor);
    }

    // Constructor por defecto
    public Producto() {
        this(0, "", "", "", 0.0, 0.0, 0, 5, "ACTIVO", null, null);
    }

    // --- GETTERS Y SETTERS ESTÁNDAR ---
    // --- GETTERS Y SETTERS ESTÁNDAR ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getCodigoBarras() {
        return codigoBarras.get();
    }

    public void setCodigoBarras(String value) {
        codigoBarras.set(value);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String value) {
        nombre.set(value);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String value) {
        descripcion.set(value);
    }

    public double getPrecio() {
        return precio.get();
    }

    public void setPrecio(double value) {
        precio.set(value);
    }

    public double getPrecioCompra() {
        return precioCompra.get();
    }

    public void setPrecioCompra(double value) {
        precioCompra.set(value);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int value) {
        stock.set(value);
    }

    public int getStockMinimo() {
        return stockMinimo.get();
    }

    public void setStockMinimo(int value) {
        stockMinimo.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    public Categoria getCategoria() {
        return categoria.get();
    }

    public void setCategoria(Categoria value) {
        categoria.set(value);
    }

    public Proveedor getProveedor() {
        return proveedor.get();
    }

    public void setProveedor(Proveedor value) {
        proveedor.set(value);
    }

    // --- PROPIEDADES (Obligatorias para el TableView de JavaFX) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public ObjectProperty<Categoria> categoriaProperty() {
        return categoria;
    }

    // NUEVO: Propiedad para enlace reactivo del Proveedor
    public ObjectProperty<Proveedor> proveedorProperty() {
        return proveedor;
    }

}
