package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ArticulosPrecargados {

    private final StringProperty codigoBarras;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final DoubleProperty precioCompra;
    private final DoubleProperty precioVenta;
    private final IntegerProperty stockInicial;
    private final StringProperty categoria;
    private final BooleanProperty valido;
    private final StringProperty mensajeError;

    public ArticulosPrecargados() {
        this("", "", "", 0.0, 0.0, 0, "General");
    }

    public ArticulosPrecargados(String codigoBarras, String nombre, String descripcion,
            double precioCompra, double precioVenta, int stockInicial, String categoria) {
        this.codigoBarras = new SimpleStringProperty(codigoBarras);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.precioCompra = new SimpleDoubleProperty(precioCompra);
        this.precioVenta = new SimpleDoubleProperty(precioVenta);
        this.stockInicial = new SimpleIntegerProperty(stockInicial);
        this.categoria = new SimpleStringProperty(categoria);
        this.valido = new SimpleBooleanProperty(true);
        this.mensajeError = new SimpleStringProperty("");
    }

    // --- GETTERS, SETTERS Y PROPERTIES ---
    public String getCodigoBarras() {
        return codigoBarras.get();
    }

    public void setCodigoBarras(String value) {
        codigoBarras.set(value);
    }

    public StringProperty codigoBarrasProperty() {
        return codigoBarras;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String value) {
        nombre.set(value);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String value) {
        descripcion.set(value);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public double getPrecioCompra() {
        return precioCompra.get();
    }

    public void setPrecioCompra(double value) {
        precioCompra.set(value);
    }

    public DoubleProperty precioCompraProperty() {
        return precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta.get();
    }

    public void setPrecioVenta(double value) {
        precioVenta.set(value);
    }

    public DoubleProperty precioVentaProperty() {
        return precioVenta;
    }

    public int getStockInicial() {
        return stockInicial.get();
    }

    public void setStockInicial(int value) {
        stockInicial.set(value);
    }

    public IntegerProperty stockInicialProperty() {
        return stockInicial;
    }

    public String getCategoria() {
        return categoria.get();
    }

    public void setCategoria(String value) {
        categoria.set(value);
    }

    public StringProperty categoriaProperty() {
        return categoria;
    }

    public boolean isValido() {
        return valido.get();
    }

    public void setValido(boolean value) {
        valido.set(value);
    }

    public BooleanProperty validoProperty() {
        return valido;
    }

    public String getMensajeError() {
        return mensajeError.get();
    }

    public void setMensajeError(String value) {
        mensajeError.set(value);
    }

    public StringProperty mensajeErrorProperty() {
        return mensajeError;
    }

}
