package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfiguracionImpresora {

    private final IntegerProperty id;
    private final StringProperty nombreImpresora;
    private final StringProperty fuente;
    private final IntegerProperty tamanoFuente;
    private final IntegerProperty columnas;
    private final BooleanProperty usarFuenteNormalTotales;
    private final BooleanProperty todasNegritas;

    // Constructor completo
    public ConfiguracionImpresora(int id, String nombreImpresora, String fuente, int tamanoFuente,
            int columnas, boolean usarFuenteNormalTotales, boolean todasNegritas) {
        this.id = new SimpleIntegerProperty(id);
        this.nombreImpresora = new SimpleStringProperty(nombreImpresora);
        this.fuente = new SimpleStringProperty(fuente);
        this.tamanoFuente = new SimpleIntegerProperty(tamanoFuente);
        this.columnas = new SimpleIntegerProperty(columnas);
        this.usarFuenteNormalTotales = new SimpleBooleanProperty(usarFuenteNormalTotales);
        this.todasNegritas = new SimpleBooleanProperty(todasNegritas);
    }

    // Constructor sin ID (para registros nuevos)
    public ConfiguracionImpresora(String nombreImpresora, String fuente, int tamanoFuente,
            int columnas, boolean usarFuenteNormalTotales, boolean todasNegritas) {
        this.id = new SimpleIntegerProperty(0);
        this.nombreImpresora = new SimpleStringProperty(nombreImpresora);
        this.fuente = new SimpleStringProperty(fuente);
        this.tamanoFuente = new SimpleIntegerProperty(tamanoFuente);
        this.columnas = new SimpleIntegerProperty(columnas);
        this.usarFuenteNormalTotales = new SimpleBooleanProperty(usarFuenteNormalTotales);
        this.todasNegritas = new SimpleBooleanProperty(todasNegritas);
    }

    // --- GETTERS Y SETTERS ---
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getNombreImpresora() {
        return nombreImpresora.get();
    }

    public void setNombreImpresora(String value) {
        nombreImpresora.set(value);
    }

    public String getFuente() {
        return fuente.get();
    }

    public void setFuente(String value) {
        fuente.set(value);
    }

    public int getTamanoFuente() {
        return tamanoFuente.get();
    }

    public void setTamanoFuente(int value) {
        tamanoFuente.set(value);
    }

    public int getColumnas() {
        return columnas.get();
    }

    public void setColumnas(int value) {
        columnas.set(value);
    }

    public boolean isUsarFuenteNormalTotales() {
        return usarFuenteNormalTotales.get();
    }

    public void setUsarFuenteNormalTotales(boolean value) {
        usarFuenteNormalTotales.set(value);
    }

    public boolean isTodasNegritas() {
        return todasNegritas.get();
    }

    public void setTodasNegritas(boolean value) {
        todasNegritas.set(value);
    }

    // --- PROPIEDADES ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreImpresoraProperty() {
        return nombreImpresora;
    }

    public StringProperty fuenteProperty() {
        return fuente;
    }

    public IntegerProperty tamanoFuenteProperty() {
        return tamanoFuente;
    }

    public IntegerProperty columnasProperty() {
        return columnas;
    }

    public BooleanProperty usarFuenteNormalTotalesProperty() {
        return usarFuenteNormalTotales;
    }

    public BooleanProperty todasNegritasProperty() {
        return todasNegritas;
    }

    @Override
    public String toString() {
        return getNombreImpresora();
    }
}
