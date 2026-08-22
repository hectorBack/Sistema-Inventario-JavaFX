package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OpcionesHabilitadas {

    private final IntegerProperty id;
    private final BooleanProperty usarInventario;
    private final BooleanProperty ofrecerCredito;
    private final BooleanProperty productoComun;
    private final BooleanProperty calcularPrecio;
    private final DoubleProperty margenGanancia;
    private final BooleanProperty habilitarRedondeo;
    private final StringProperty tipoRedondeo;

    // Constructor completo
    public OpcionesHabilitadas(int id, boolean usarInventario, boolean ofrecerCredito,
            boolean productoComun, boolean calcularPrecio,
            double margenGanancia, boolean habilitarRedondeo, String tipoRedondeo) {
        this.id = new SimpleIntegerProperty(id);
        this.usarInventario = new SimpleBooleanProperty(usarInventario);
        this.ofrecerCredito = new SimpleBooleanProperty(ofrecerCredito);
        this.productoComun = new SimpleBooleanProperty(productoComun);
        this.calcularPrecio = new SimpleBooleanProperty(calcularPrecio);
        this.margenGanancia = new SimpleDoubleProperty(margenGanancia);
        this.habilitarRedondeo = new SimpleBooleanProperty(habilitarRedondeo);
        this.tipoRedondeo = new SimpleStringProperty(tipoRedondeo);
    }

    // Constructor sin ID (Para inicialización o inserción)
    public OpcionesHabilitadas(boolean usarInventario, boolean ofrecerCredito,
            boolean productoComun, boolean calcularPrecio,
            double margenGanancia, boolean habilitarRedondeo, String tipoRedondeo) {
        this(1, usarInventario, ofrecerCredito, productoComun, calcularPrecio, margenGanancia, habilitarRedondeo, tipoRedondeo);
    }

    // Constructor por defecto
    public OpcionesHabilitadas() {
        this(1, true, false, false, false, 0.0, false, "");
    }

    // --- GETTERS Y SETTERS ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public boolean isUsarInventario() {
        return usarInventario.get();
    }

    public void setUsarInventario(boolean value) {
        usarInventario.set(value);
    }

    public boolean isOfrecerCredito() {
        return ofrecerCredito.get();
    }

    public void setOfrecerCredito(boolean value) {
        ofrecerCredito.set(value);
    }

    public boolean isProductoComun() {
        return productoComun.get();
    }

    public void setProductoComun(boolean value) {
        productoComun.set(value);
    }

    public boolean isCalcularPrecio() {
        return calcularPrecio.get();
    }

    public void setCalcularPrecio(boolean value) {
        calcularPrecio.set(value);
    }

    public double getMargenGanancia() {
        return margenGanancia.get();
    }

    public void setMargenGanancia(double value) {
        margenGanancia.set(value);
    }

    public boolean isHabilitarRedondeo() {
        return habilitarRedondeo.get();
    }

    public void setHabilitarRedondeo(boolean value) {
        habilitarRedondeo.set(value);
    }

    public String getTipoRedondeo() {
        return tipoRedondeo.get();
    }

    public void setTipoRedondeo(String value) {
        tipoRedondeo.set(value);
    }

    // --- PROPIEDADES (Para Binding en JavaFX) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public BooleanProperty usarInventarioProperty() {
        return usarInventario;
    }

    public BooleanProperty ofrecerCreditoProperty() {
        return ofrecerCredito;
    }

    public BooleanProperty productoComunProperty() {
        return productoComun;
    }

    public BooleanProperty calcularPrecioProperty() {
        return calcularPrecio;
    }

    public DoubleProperty margenGananciaProperty() {
        return margenGanancia;
    }

    public BooleanProperty habilitarRedondeoProperty() {
        return habilitarRedondeo;
    }

    public StringProperty tipoRedondeoProperty() {
        return tipoRedondeo;
    }
}
