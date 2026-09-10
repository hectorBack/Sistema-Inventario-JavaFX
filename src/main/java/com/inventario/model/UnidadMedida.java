package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UnidadMedida {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty clave;
    private final BooleanProperty activo;
    private final BooleanProperty predeterminado;

    public UnidadMedida(Integer id, String nombre, String clave, Boolean activo, Boolean predeterminado) {
        this.id = new SimpleIntegerProperty(id != null ? id : 0);
        this.nombre = new SimpleStringProperty(nombre);
        this.clave = new SimpleStringProperty(clave);
        this.activo = new SimpleBooleanProperty(activo != null ? activo : false);
        this.predeterminado = new SimpleBooleanProperty(predeterminado != null ? predeterminado : false);
    }

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

    public String getClave() {
        return clave.get();
    }

    public StringProperty claveProperty() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave.set(clave);
    }

    public boolean isActivo() {
        return activo.get();
    }

    public BooleanProperty activoProperty() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo.set(activo);
    }

    public boolean isPredeterminado() {
        return predeterminado.get();
    }

    public BooleanProperty predeterminadoProperty() {
        return predeterminado;
    }

    public void setPredeterminado(boolean predeterminado) {
        this.predeterminado.set(predeterminado);
    }

    @Override
    public String toString() {
        return getClave();
    }

}
