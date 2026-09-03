package com.inventario.model;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Empresa {

    private final IntegerProperty id;
    private final StringProperty nombreEmpresa;
    private final StringProperty rfc;
    private final StringProperty telefono;
    private final StringProperty direccion;
    private final StringProperty logoPath;
    private final ObjectProperty<LocalDateTime> fechaActualizacion;

    // Constructor completo
    public Empresa(int id, String nombreEmpresa, String rfc, String telefono, String direccion, String logoPath, LocalDateTime fechaActualizacion) {
        this.id = new SimpleIntegerProperty(id);
        this.nombreEmpresa = new SimpleStringProperty(nombreEmpresa);
        this.rfc = new SimpleStringProperty(rfc);
        this.telefono = new SimpleStringProperty(telefono);
        this.direccion = new SimpleStringProperty(direccion);
        this.logoPath = new SimpleStringProperty(logoPath);
        this.fechaActualizacion = new SimpleObjectProperty<>(fechaActualizacion);
    }

    // Constructor sin ID (Para registros nuevos o valores por defecto)
    public Empresa(String nombreEmpresa, String rfc, String telefono, String direccion, String logoPath) {
        this.id = new SimpleIntegerProperty(1);
        this.nombreEmpresa = new SimpleStringProperty(nombreEmpresa);
        this.rfc = new SimpleStringProperty(rfc);
        this.telefono = new SimpleStringProperty(telefono);
        this.direccion = new SimpleStringProperty(direccion);
        this.logoPath = new SimpleStringProperty(logoPath);
        this.fechaActualizacion = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    // --- GETTERS Y SETTERS ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getNombreEmpresa() {
        return nombreEmpresa.get();
    }

    public void setNombreEmpresa(String value) {
        nombreEmpresa.set(value);
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

    public String getDireccion() {
        return direccion.get();
    }

    public void setDireccion(String value) {
        direccion.set(value);
    }

    public String getLogoPath() {
        return logoPath.get();
    }

    public void setLogoPath(String value) {
        logoPath.set(value);
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion.get();
    }

    public void setFechaActualizacion(LocalDateTime value) {
        fechaActualizacion.set(value);
    }

    // --- PROPIEDADES ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreEmpresaProperty() {
        return nombreEmpresa;
    }

    public StringProperty rfcProperty() {
        return rfc;
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public StringProperty direccionProperty() {
        return direccion;
    }

    public StringProperty logoPathProperty() {
        return logoPath;
    }

    public ObjectProperty<LocalDateTime> fechaActualizacionProperty() {
        return fechaActualizacion;
    }

    @Override
    public String toString() {
        return getNombreEmpresa();
    }

}
