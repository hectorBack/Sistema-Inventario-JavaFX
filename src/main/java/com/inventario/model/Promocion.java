package com.inventario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Promocion {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty codigoBarrasProducto = new SimpleStringProperty();
    private final DoubleProperty cantidadDesde = new SimpleDoubleProperty();
    private final DoubleProperty cantidadHasta = new SimpleDoubleProperty();
    private final DoubleProperty precioPromocion = new SimpleDoubleProperty();
    private final DoubleProperty precioNormal = new SimpleDoubleProperty();
    private final DoubleProperty precioCosto = new SimpleDoubleProperty();
    private final DoubleProperty precioUnitario = new SimpleDoubleProperty();
    private final StringProperty estado = new SimpleStringProperty("ACTIVA");

    public Promocion() {
        this(0, "", "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "ACTIVA");
    }

    public Promocion(int id, String nombre, String codigoBarrasProducto, double cantidadDesde,
            double cantidadHasta, double precioPromocion, double precioNormal,
            double precioCosto, double precioUnitario, String estado) {
        this.id.set(id);
        this.nombre.set(nombre);
        this.codigoBarrasProducto.set(codigoBarrasProducto);
        this.cantidadDesde.set(cantidadDesde);
        this.cantidadHasta.set(cantidadHasta);
        this.precioPromocion.set(precioPromocion);
        this.precioNormal.set(precioNormal);
        this.precioCosto.set(precioCosto);
        this.precioUnitario.set(precioUnitario);
        this.estado.set(estado != null ? estado : "ACTIVA");
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getCodigoBarrasProducto() {
        return codigoBarrasProducto.get();
    }

    public void setCodigoBarrasProducto(String codigoBarrasProducto) {
        this.codigoBarrasProducto.set(codigoBarrasProducto);
    }

    public double getCantidadDesde() {
        return cantidadDesde.get();
    }

    public void setCantidadDesde(double cantidadDesde) {
        this.cantidadDesde.set(cantidadDesde);
    }

    public double getCantidadHasta() {
        return cantidadHasta.get();
    }

    public void setCantidadHasta(double cantidadHasta) {
        this.cantidadHasta.set(cantidadHasta);
    }

    public double getPrecioPromocion() {
        return precioPromocion.get();
    }

    public void setPrecioPromocion(double precioPromocion) {
        this.precioPromocion.set(precioPromocion);
    }

    public double getPrecioNormal() {
        return precioNormal.get();
    }

    public void setPrecioNormal(double precioNormal) {
        this.precioNormal.set(precioNormal);
    }

    public double getPrecioCosto() {
        return precioCosto.get();
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto.set(precioCosto);
    }

    public double getPrecioUnitario() {
        return precioUnitario.get();
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario.set(precioUnitario);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado != null ? estado : "ACTIVA");
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty codigoBarrasProductoProperty() {
        return codigoBarrasProducto;
    }

    public DoubleProperty cantidadDesdeProperty() {
        return cantidadDesde;
    }

    public DoubleProperty cantidadHastaProperty() {
        return cantidadHasta;
    }

    public DoubleProperty precioPromocionProperty() {
        return precioPromocion;
    }

    public DoubleProperty precioNormalProperty() {
        return precioNormal;
    }

    public DoubleProperty precioCostoProperty() {
        return precioCosto;
    }

    public DoubleProperty precioUnitarioProperty() {
        return precioUnitario;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
