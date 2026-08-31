package com.inventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Folio {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty modulo;
    private final StringProperty serie;
    private final IntegerProperty folioActual;
    private final IntegerProperty longitudCeros;
    private final StringProperty estado; // "ACTIVO" / "INACTIVO"

    // Constructor completo
    public Folio(int id, String nombre, String modulo, String serie, int folioActual, int longitudCeros, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.modulo = new SimpleStringProperty(modulo);
        this.serie = new SimpleStringProperty(serie);
        this.folioActual = new SimpleIntegerProperty(folioActual);
        this.longitudCeros = new SimpleIntegerProperty(longitudCeros);
        this.estado = new SimpleStringProperty(estado);
    }

    // Constructor sin ID (Para nuevos registros)
    public Folio(String nombre, String modulo, String serie, int folioActual, int longitudCeros, String estado) {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty(nombre);
        this.modulo = new SimpleStringProperty(modulo);
        this.serie = new SimpleStringProperty(serie);
        this.folioActual = new SimpleIntegerProperty(folioActual);
        this.longitudCeros = new SimpleIntegerProperty(longitudCeros);
        this.estado = new SimpleStringProperty(estado);
    }

    // Método helper para obtener la previsualización del folio formateado (Ej. TCK-000001)
    public String getFolioFormateado() {
        String prefijo = (getSerie() != null && !getSerie().trim().isEmpty()) ? getSerie().trim() + "-" : "";
        int ceros = getLongitudCeros() > 0 ? getLongitudCeros() : 6;
        return String.format("%s%0" + ceros + "d", prefijo, getFolioActual());
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

    public String getModulo() {
        return modulo.get();
    }

    public void setModulo(String value) {
        modulo.set(value);
    }

    public String getSerie() {
        return serie.get();
    }

    public void setSerie(String value) {
        serie.set(value);
    }

    public int getFolioActual() {
        return folioActual.get();
    }

    public void setFolioActual(int value) {
        folioActual.set(value);
    }

    public int getLongitudCeros() {
        return longitudCeros.get();
    }

    public void setLongitudCeros(int value) {
        longitudCeros.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    // --- PROPIEDADES (PROPERTY METHODS) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty moduloProperty() {
        return modulo;
    }

    public StringProperty serieProperty() {
        return serie;
    }

    public IntegerProperty folioActualProperty() {
        return folioActual;
    }

    public IntegerProperty longitudCerosProperty() {
        return longitudCeros;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    @Override
    public String toString() {
        return getNombre();
    }

}
