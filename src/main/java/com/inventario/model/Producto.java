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
    private final DoubleProperty stock;
    private final StringProperty estado;
    private final StringProperty codigoBarras;
    private final DoubleProperty precioCompra;
    private final DoubleProperty porcentajeGanancia;
    private final DoubleProperty stockMinimo;
    private final StringProperty descripcion;
    private final StringProperty tipoVenta; // "UNIDAD", "GRANEL", "PAQUETE"
    private final DoubleProperty precioMayoreo;

    private final ObjectProperty<Categoria> categoria;
    private final ObjectProperty<Proveedor> proveedor;

    // Constructor completo (Para cuando traes datos de la BD)
    // --- CONSTRUCTOR COMPLETO (Base de Datos) ---
    public Producto(int id, String codigoBarras, String nombre, String descripcion, double precio,
            double precioMayoreo, double precioCompra, double porcentajeGanancia,
            double stock, double stockMinimo, String tipoVenta, String estado,
            Categoria categoria, Proveedor proveedor) {
        this.id = new SimpleIntegerProperty(id);
        this.codigoBarras = new SimpleStringProperty(codigoBarras);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.precio = new SimpleDoubleProperty(precio);
        this.precioMayoreo = new SimpleDoubleProperty(precioMayoreo);
        this.precioCompra = new SimpleDoubleProperty(precioCompra);
        this.porcentajeGanancia = new SimpleDoubleProperty(porcentajeGanancia);
        this.stock = new SimpleDoubleProperty(stock);
        this.stockMinimo = new SimpleDoubleProperty(stockMinimo);
        this.tipoVenta = new SimpleStringProperty(tipoVenta != null ? tipoVenta : "UNIDAD");
        this.estado = new SimpleStringProperty(estado);
        this.categoria = new SimpleObjectProperty<>(categoria);
        this.proveedor = new SimpleObjectProperty<>(proveedor);
    }

    // --- CONSTRUCTOR SIN ID (Nuevos Registros) ---
    public Producto(String codigoBarras, String nombre, String descripcion, double precio,
            double precioMayoreo, double precioCompra, double porcentajeGanancia,
            double stock, double stockMinimo, String tipoVenta, String estado,
            Categoria categoria, Proveedor proveedor) {
        this(0, codigoBarras, nombre, descripcion, precio, precioMayoreo, precioCompra,
                porcentajeGanancia, stock, stockMinimo, tipoVenta, estado, categoria, proveedor);
    }

    // Constructor por defecto
    public Producto() {
        this(0, "", "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, "UNIDAD", "ACTIVO", null, null);
    }

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

    public double getPrecioMayoreo() {
        return precioMayoreo.get();
    }

    public void setPrecioMayoreo(double value) {
        precioMayoreo.set(value);
    }

    public double getPrecioCompra() {
        return precioCompra.get();
    }

    public void setPrecioCompra(double value) {
        precioCompra.set(value);
    }

    public double getPorcentajeGanancia() {
        return porcentajeGanancia.get();
    }

    public void setPorcentajeGanancia(double value) {
        porcentajeGanancia.set(value);
    }

    public double getStock() {
        return stock.get();
    }

    public void setStock(double value) {
        stock.set(value);
    }

    public double getStockMinimo() {
        return stockMinimo.get();
    }

    public void setStockMinimo(double value) {
        stockMinimo.set(value);
    }

    public String getTipoVenta() {
        return tipoVenta.get();
    }

    public void setTipoVenta(String value) {
        tipoVenta.set(value);
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

    public StringProperty codigoBarrasProperty() {
        return codigoBarras;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public DoubleProperty precioMayoreoProperty() {
        return precioMayoreo;
    }

    public DoubleProperty precioCompraProperty() {
        return precioCompra;
    }

    public DoubleProperty porcentajeGananciaProperty() {
        return porcentajeGanancia;
    }

    public DoubleProperty stockProperty() {
        return stock;
    }

    public DoubleProperty stockMinimoProperty() {
        return stockMinimo;
    }

    public StringProperty tipoVentaProperty() {
        return tipoVenta;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public ObjectProperty<Categoria> categoriaProperty() {
        return categoria;
    }

    public ObjectProperty<Proveedor> proveedorProperty() {
        return proveedor;
    }

}
