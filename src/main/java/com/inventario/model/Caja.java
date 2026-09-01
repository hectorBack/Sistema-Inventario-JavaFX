package com.inventario.model;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Caja {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty estado; // "ACTIVA" / "INACTIVA"
    private final StringProperty tipo;   // "PRINCIPAL" / "SECUNDARIA"
    private final ObjectProperty<Integer> cajaPadreId;
    private final ObjectProperty<LocalDateTime> fechaUltimoAcceso;

    // Constructor completo
    public Caja(int id, String nombre, String estado, String tipo, Integer cajaPadreId, LocalDateTime fechaUltimoAcceso) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.estado = new SimpleStringProperty(estado);
        this.tipo = new SimpleStringProperty(tipo != null ? tipo : "SECUNDARIA");
        this.cajaPadreId = new SimpleObjectProperty<>(cajaPadreId);
        this.fechaUltimoAcceso = new SimpleObjectProperty<>(fechaUltimoAcceso);
    }

    // Constructor para nueva caja
    public Caja(String nombre, String estado, String tipo, Integer cajaPadreId) {
        this(0, nombre, estado, tipo, cajaPadreId, LocalDateTime.now());
    }

    // --- GETTERS & SETTERS ---
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

    public String getTipo() {
        return tipo.get();
    }

    public void setTipo(String value) {
        tipo.set(value);
    }

    public Integer getCajaPadreId() {
        return cajaPadreId.get();
    }

    public void setCajaPadreId(Integer value) {
        cajaPadreId.set(value);
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso.get();
    }

    public void setFechaUltimoAcceso(LocalDateTime value) {
        fechaUltimoAcceso.set(value);
    }

    // --- PROPERTY METHODS ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public ObjectProperty<Integer> cajaPadreIdProperty() {
        return cajaPadreId;
    }

    public ObjectProperty<LocalDateTime> fechaUltimoAccesoProperty() {
        return fechaUltimoAcceso;
    }

    @Override
    public String toString() {
        return getNombre() + (getTipo().equals("PRINCIPAL") ? " (Principal)" : "");
    }
}
