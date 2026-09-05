package com.inventario.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConfiguracionTicket {

    private final IntegerProperty id = new SimpleIntegerProperty(1);
    private final ObservableList<String> lineasEncabezado = FXCollections.observableArrayList();
    private final ObservableList<String> lineasPie = FXCollections.observableArrayList();
    private final BooleanProperty incluirPrecioUnitario = new SimpleBooleanProperty(false);
    private final BooleanProperty imprimirDescripcionCompleta = new SimpleBooleanProperty(false);

    public ConfiguracionTicket() {
    }

    public ConfiguracionTicket(int id, ObservableList<String> lineasEncabezado, ObservableList<String> lineasPie,
            boolean incluirPrecioUnitario, boolean imprimirDescripcionCompleta) {
        setId(id);
        if (lineasEncabezado != null) {
            this.lineasEncabezado.addAll(lineasEncabezado);
        }
        if (lineasPie != null) {
            this.lineasPie.addAll(lineasPie);
        }
        setIncluirPrecioUnitario(incluirPrecioUnitario);
        setImprimirDescripcionCompleta(imprimirDescripcionCompleta);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public ObservableList<String> getLineasEncabezado() {
        return lineasEncabezado;
    }

    public ObservableList<String> getLineasPie() {
        return lineasPie;
    }

    public boolean isIncluirPrecioUnitario() {
        return incluirPrecioUnitario.get();
    }

    public void setIncluirPrecioUnitario(boolean val) {
        this.incluirPrecioUnitario.set(val);
    }

    public BooleanProperty incluirPrecioUnitarioProperty() {
        return incluirPrecioUnitario;
    }

    public boolean isImprimirDescripcionCompleta() {
        return imprimirDescripcionCompleta.get();
    }

    public void setImprimirDescripcionCompleta(boolean val) {
        this.imprimirDescripcionCompleta.set(val);
    }

    public BooleanProperty imprimirDescripcionCompletaProperty() {
        return imprimirDescripcionCompleta;
    }
}
