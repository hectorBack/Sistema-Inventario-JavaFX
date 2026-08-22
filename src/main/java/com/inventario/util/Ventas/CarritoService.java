package com.inventario.util.Ventas;

import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CarritoService {

    private final ObservableList<DetalleVenta> items = FXCollections.observableArrayList();

    public ObservableList<DetalleVenta> getItems() {
        return items;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public void limpiar() {
        items.clear();
    }

    public double calcularTotal() {
        double sumaRaw = items.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        return Math.round(sumaRaw * 100.0) / 100.0;
    }

    public int calcularTotalArticulos() {
        return (int) items.stream().mapToDouble(DetalleVenta::getCantidad).sum();
    }

    public String agregarOActualizarProducto(Producto producto, double cantidadAñadir) throws IllegalArgumentException {
        if (producto.getStock() <= 0) {
            return "El producto está agotado en almacén.";
        }

        Optional<DetalleVenta> itemExistente = items.stream()
                .filter(item -> item.getProducto() != null && item.getProducto().getId() == producto.getId())
                .findFirst();

        if (itemExistente.isPresent()) {
            DetalleVenta detalle = itemExistente.get();
            double nuevaCantidadTotal = "GRANEL".equalsIgnoreCase(producto.getTipoVenta())
                    ? cantidadAñadir
                    : detalle.getCantidad() + cantidadAñadir;

            if (nuevaCantidadTotal > producto.getStock()) {
                return "Supera el stock disponible (" + producto.getStock() + ").";
            }
            detalle.setCantidad(nuevaCantidadTotal);
        } else {
            if (cantidadAñadir > producto.getStock()) {
                return "Solo quedan " + producto.getStock() + " unidades.";
            }
            items.add(new DetalleVenta(producto, cantidadAñadir, producto.getPrecio()));
        }

        return null; // Null indica que todo fue exitoso
    }

    public void decrementarOEliminar(DetalleVenta item) {
        if (item == null) {
            return;
        }
        double nuevaCantidad = item.getCantidad() - 1.0;
        if (nuevaCantidad <= 0) {
            items.remove(item);
        } else {
            item.setCantidad(nuevaCantidad);
        }
    }

    public void removerItem(DetalleVenta item) {
        items.remove(item);
    }

    public void agregarSinValidarStock(Producto producto, double cantidadAñadir) {
        Optional<DetalleVenta> itemExistente = items.stream()
                .filter(item -> item.getProducto() != null && item.getProducto().getId() == producto.getId())
                .findFirst();

        if (itemExistente.isPresent()) {
            DetalleVenta detalle = itemExistente.get();
            double nuevaCantidadTotal = "GRANEL".equalsIgnoreCase(producto.getTipoVenta())
                    ? cantidadAñadir
                    : detalle.getCantidad() + cantidadAñadir;

            detalle.setCantidad(nuevaCantidadTotal);
        } else {
            items.add(new DetalleVenta(producto, cantidadAñadir, producto.getPrecio()));
        }
    }
}
