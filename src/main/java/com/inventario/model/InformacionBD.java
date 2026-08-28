package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InformacionBD {

    private final StringProperty motorVersion;     // ej. "PostgreSQL 15.2"
    private final StringProperty tamanioBD;         // ej. "42 MB"
    private final StringProperty totalProductos;    // Cantidad total de registros en productos
    private final StringProperty totalVentas;       // Cantidad total de tickets/ventas
    private final StringProperty totalCajeros;      // Cantidad de usuarios/cajeros
    private final BooleanProperty estadoConexion;    // true = Conectado, false = Sin conexión

    public InformacionBD() {
        this.motorVersion = new SimpleStringProperty("Desconocido");
        this.tamanioBD = new SimpleStringProperty("0 MB");
        this.totalProductos = new SimpleStringProperty("0");
        this.totalVentas = new SimpleStringProperty("0");
        this.totalCajeros = new SimpleStringProperty("0");
        this.estadoConexion = new SimpleBooleanProperty(false);
    }

    // --- GETTERS, SETTERS Y PROPERTIES ---
    public String getMotorVersion() {
        return motorVersion.get();
    }

    public void setMotorVersion(String value) {
        motorVersion.set(value);
    }

    public StringProperty motorVersionProperty() {
        return motorVersion;
    }

    public String getTamanioBD() {
        return tamanioBD.get();
    }

    public void setTamanioBD(String value) {
        tamanioBD.set(value);
    }

    public StringProperty tamanioBDProperty() {
        return tamanioBD;
    }

    public String getTotalProductos() {
        return totalProductos.get();
    }

    public void setTotalProductos(String value) {
        totalProductos.set(value);
    }

    public StringProperty totalProductosProperty() {
        return totalProductos;
    }

    public String getTotalVentas() {
        return totalVentas.get();
    }

    public void setTotalVentas(String value) {
        totalVentas.set(value);
    }

    public StringProperty totalVentasProperty() {
        return totalVentas;
    }

    public String getTotalCajeros() {
        return totalCajeros.get();
    }

    public void setTotalCajeros(String value) {
        totalCajeros.set(value);
    }

    public StringProperty totalCajerosProperty() {
        return totalCajeros;
    }

    public boolean isEstadoConexion() {
        return estadoConexion.get();
    }

    public void setEstadoConexion(boolean value) {
        estadoConexion.set(value);
    }

    public BooleanProperty estadoConexionProperty() {
        return estadoConexion;
    }
}
