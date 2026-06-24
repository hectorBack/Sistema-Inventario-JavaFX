package com.inventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty rfc;
    private final StringProperty telefono;
    private final StringProperty email;
    private final StringProperty direccion;
    private final StringProperty estado;

    // Constructor vacío (Valores por defecto)
    public Cliente() {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty("");
        this.rfc = new SimpleStringProperty("");
        this.telefono = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.direccion = new SimpleStringProperty("");
        this.estado = new SimpleStringProperty("ACTIVO");
    }

    // Constructor completo para mapear desde JDBC / Base de Datos
    public Cliente(int id, String nombre, String rfc, String telefono, String email, String direccion, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.rfc = new SimpleStringProperty(rfc);
        this.telefono = new SimpleStringProperty(telefono);
        this.email = new SimpleStringProperty(email);
        this.direccion = new SimpleStringProperty(direccion);
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

    public String getRfc() {
        return rfc.get();
    }

    public void setRfc(String value) {
        rfc.set(value);
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

    public String getDireccion() {
        return direccion.get();
    }

    public void setDireccion(String value) {
        direccion.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    // --- PROPIEDADES (Para el TableView de JavaFX) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty rfcProperty() {
        return rfc;
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty direccionProperty() {
        return direccion;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

}
