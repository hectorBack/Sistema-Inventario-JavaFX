package com.inventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Proveedor {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty contacto;
    private final StringProperty telefono;
    private final StringProperty email;
    private final StringProperty estado;

    // Constructor vacío (Inicializando propiedades por defecto)
    public Proveedor() {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty("");
        this.contacto = new SimpleStringProperty("");
        this.telefono = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.estado = new SimpleStringProperty("ACTIVO");
    }

    // Constructor completo actualizado con Properties
    public Proveedor(int id, String nombre, String contacto, String telefono, String email, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.contacto = new SimpleStringProperty(contacto);
        this.telefono = new SimpleStringProperty(telefono);
        this.email = new SimpleStringProperty(email);
        this.estado = new SimpleStringProperty(estado);
    }

    // --- GETTERS Y SETTERS ESTÁNDAR ---
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

    public String getContacto() {
        return contacto.get();
    }

    public void setContacto(String value) {
        contacto.set(value);
    }

    public String getTelefono() {
        return telefono.get();
    }

    public void setTelefono(String value) {
        telefono.set(value);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    // --- PROPIEDADES (Obligatorias para JavaFX) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty contactoProperty() {
        return contacto;
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty estadoProperty() {
        return estado;
    }
}
