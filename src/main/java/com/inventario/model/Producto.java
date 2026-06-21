package com.inventario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Producto {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final DoubleProperty precio;
    private final IntegerProperty stock;
    private final StringProperty estado;

    private final ObjectProperty<Categoria> categoria;

    // Constructor completo (Para cuando traes datos de la BD)
    // Constructor completo actualizado (con Categoría)
    public Producto(int id, String nombre, double precio, int stock, String estado, Categoria categoria) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.precio = new SimpleDoubleProperty(precio);
        this.stock = new SimpleIntegerProperty(stock);
        this.estado = new SimpleStringProperty(estado);
        this.categoria = new SimpleObjectProperty<>(categoria);
    }

    // Constructor sin ID actualizado
    public Producto(String nombre, double precio, int stock, String estado, Categoria categoria) {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty(nombre);
        this.precio = new SimpleDoubleProperty(precio);
        this.stock = new SimpleIntegerProperty(stock);
        this.estado = new SimpleStringProperty(estado);
        this.categoria = new SimpleObjectProperty<>(categoria);
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

    public double getPrecio() {
        return precio.get();
    }

    public void setPrecio(double value) {
        precio.set(value);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int value) {
        stock.set(value);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String value) {
        estado.set(value);
    }

    // --- PROPIEDADES (Obligatorias para el TableView de JavaFX) ---
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public Categoria getCategoria() {
        return categoria.get();
    }

    public void setCategoria(Categoria value) {
        categoria.set(value);
    }

    public ObjectProperty<Categoria> categoriaProperty() {
        return categoria;
    }

}
