package com.inventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Categoria {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty estado;

    // Constructor completo
    public Categoria(int id, String nombre, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.estado = new SimpleStringProperty(estado);
    }

    // Constructor sin ID (Para registros nuevos)
    public Categoria(String nombre, String estado) {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty(nombre);
        this.estado = new SimpleStringProperty(estado);
    }

    // --- GETTERS Y SETTERS ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String value) {
        nombre.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    // --- PROPIEDADES ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    // El método toString es vital porque usaremos un ComboBox<Categoria> 
    // en el formulario de Productos y queremos que muestre el nombre.
    @Override
    public String toString() {
        return getNombre();
    }

}
