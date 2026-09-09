package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Impuesto {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final DoubleProperty porcentaje;
    private final StringProperty pais;
    private final StringProperty estado;
    private final BooleanProperty desglosarTicket;
    private final BooleanProperty preciosConImpuesto;

    public Impuesto(Integer id, String nombre, Double porcentaje, String pais, String estado, Boolean desglosarTicket, Boolean preciosConImpuesto) {
        this.id = new SimpleIntegerProperty(id != null ? id : 0);
        this.nombre = new SimpleStringProperty(nombre);
        this.porcentaje = new SimpleDoubleProperty(porcentaje != null ? porcentaje : 0.0);
        this.pais = new SimpleStringProperty(pais);
        this.estado = new SimpleStringProperty(estado);
        this.desglosarTicket = new SimpleBooleanProperty(desglosarTicket != null ? desglosarTicket : false);
        this.preciosConImpuesto = new SimpleBooleanProperty(preciosConImpuesto != null ? preciosConImpuesto : false);
    }

    // Getters / Setters / Properties
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public double getPorcentaje() {
        return porcentaje.get();
    }

    public DoubleProperty porcentajeProperty() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje.set(porcentaje);
    }

    public String getPais() {
        return pais.get();
    }

    public StringProperty paisProperty() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais.set(pais);
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public boolean isDesglosarTicket() {
        return desglosarTicket.get();
    }

    public BooleanProperty desglosarTicketProperty() {
        return desglosarTicket;
    }

    public void setDesglosarTicket(boolean desglosarTicket) {
        this.desglosarTicket.set(desglosarTicket);
    }

    public boolean isPreciosConImpuesto() {
        return preciosConImpuesto.get();
    }

    public BooleanProperty preciosConImpuestoProperty() {
        return preciosConImpuesto;
    }

    public void setPreciosConImpuesto(boolean preciosConImpuesto) {
        this.preciosConImpuesto.set(preciosConImpuesto);
    }

    @Override
    public String toString() {
        return getNombre() + " (" + getPorcentaje() + "%)";
    }

}
