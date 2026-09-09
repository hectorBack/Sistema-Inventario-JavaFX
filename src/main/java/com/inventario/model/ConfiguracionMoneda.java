package com.inventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfiguracionMoneda {

    private final IntegerProperty id;
    private final StringProperty simboloMoneda;
    private final StringProperty separadorMiles;
    private final StringProperty separadorDecimal;

    public ConfiguracionMoneda(Integer id, String simboloMoneda, String separadorMiles, String separadorDecimal) {
        this.id = new SimpleIntegerProperty(id != null ? id : 1);
        this.simboloMoneda = new SimpleStringProperty(simboloMoneda);
        this.separadorMiles = new SimpleStringProperty(separadorMiles);
        this.separadorDecimal = new SimpleStringProperty(separadorDecimal);
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

    public String getSimboloMoneda() {
        return simboloMoneda.get();
    }

    public StringProperty simboloMonedaProperty() {
        return simboloMoneda;
    }

    public void setSimboloMoneda(String simboloMoneda) {
        this.simboloMoneda.set(simboloMoneda);
    }

    public String getSeparadorMiles() {
        return separadorMiles.get();
    }

    public StringProperty separadorMilesProperty() {
        return separadorMiles;
    }

    public void setSeparadorMiles(String separadorMiles) {
        this.separadorMiles.set(separadorMiles);
    }

    public String getSeparadorDecimal() {
        return separadorDecimal.get();
    }

    public StringProperty separadorDecimalProperty() {
        return separadorDecimal;
    }

    public void setSeparadorDecimal(String separadorDecimal) {
        this.separadorDecimal.set(separadorDecimal);
    }

}
