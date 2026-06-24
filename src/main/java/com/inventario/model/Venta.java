package com.inventario.model;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Venta {

    private final IntegerProperty id;
    private final ObjectProperty<Cliente> cliente;
    private final ObjectProperty<LocalDateTime> fecha;
    private final DoubleProperty total;
    private final StringProperty estado;

    public Venta() {
        this.id = new SimpleIntegerProperty(0);
        this.cliente = new SimpleObjectProperty<>(null);
        this.fecha = new SimpleObjectProperty<>(LocalDateTime.now());
        this.total = new SimpleDoubleProperty(0.0);
        this.estado = new SimpleStringProperty("COMPLETADA");
    }

    public Venta(int id, Cliente cliente, LocalDateTime fecha, double total, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.cliente = new SimpleObjectProperty<>(cliente);
        this.fecha = new SimpleObjectProperty<>(fecha);
        this.total = new SimpleDoubleProperty(total);
        this.estado = new SimpleStringProperty(estado);
    }

    // Getters y Setters estándar
    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public Cliente getCliente() {
        return cliente.get();
    }

    public void setCliente(Cliente value) {
        cliente.set(value);
    }

    public LocalDateTime getFecha() {
        return fecha.get();
    }

    public void setFecha(LocalDateTime value) {
        fecha.set(value);
    }

    public double getTotal() {
        return total.get();
    }

    public void setTotal(double value) {
        total.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    // Properties para JavaFX
    public IntegerProperty idProperty() {
        return id;
    }

    public ObjectProperty<Cliente> clienteProperty() {
        return cliente;
    }

    public ObjectProperty<LocalDateTime> fechaProperty() {
        return fecha;
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

}
