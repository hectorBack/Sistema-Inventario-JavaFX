/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.util.Inventario;

import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author azulc
 */
public class InventarioUIUtil {

    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static Producto extraerProductoDeFormulario(
            Producto target,
            TextField txtCodigo,
            TextField txtNombre,
            TextArea txtDesc,
            TextField txtPrecio,
            TextField txtCosto,
            TextField txtPorcentaje,
            TextField txtMayoreo,
            TextField txtStock,
            TextField txtStockMin,
            ComboBox<String> cmbEstado,
            ComboBox<String> cmbTipoVenta,
            ComboBox<Categoria> cmbCategoria,
            ComboBox<Proveedor> cmbProveedor) {

        Producto p = (target != null) ? target : new Producto();

        p.setCodigoBarras(txtCodigo != null ? txtCodigo.getText().trim() : "");
        p.setNombre(txtNombre.getText().trim());
        p.setDescripcion(txtDesc != null ? txtDesc.getText().trim() : "");

        p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        p.setPrecioCompra(txtCosto != null && !txtCosto.getText().trim().isEmpty() ? Double.parseDouble(txtCosto.getText().trim()) : 0.0);
        p.setPorcentajeGanancia(txtPorcentaje != null && !txtPorcentaje.getText().trim().isEmpty() ? Double.parseDouble(txtPorcentaje.getText().trim()) : 0.0);
        p.setPrecioMayoreo(txtMayoreo != null && !txtMayoreo.getText().trim().isEmpty() ? Double.parseDouble(txtMayoreo.getText().trim()) : 0.0);

        p.setStock(Double.parseDouble(txtStock.getText().trim()));
        p.setStockMinimo(txtStockMin != null && !txtStockMin.getText().trim().isEmpty() ? Double.parseDouble(txtStockMin.getText().trim()) : 5.0);

        p.setEstado(cmbEstado.getValue() != null ? cmbEstado.getValue() : "Activo");
        p.setTipoVenta(cmbTipoVenta.getValue() != null ? cmbTipoVenta.getValue() : "UNIDAD");

        p.setCategoria(cmbCategoria.getValue());
        p.setProveedor(cmbProveedor.getValue());

        return p;
    }

    public static void cargarProductoEnFormulario(
            Producto p,
            TextField txtCodigo,
            TextField txtNombre,
            TextArea txtDesc,
            TextField txtPrecio,
            TextField txtCosto,
            TextField txtPorcentaje,
            TextField txtMayoreo,
            TextField txtStock,
            TextField txtStockMin,
            ComboBox<String> cmbEstado,
            ComboBox<String> cmbTipoVenta,
            ComboBox<Categoria> cmbCategoria,
            ComboBox<Proveedor> cmbProveedor) {

        if (p == null) {
            return;
        }

        txtCodigo.setText(p.getCodigoBarras() != null ? p.getCodigoBarras() : "");
        txtNombre.setText(p.getNombre());
        txtDesc.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtCosto.setText(String.valueOf(p.getPrecioCompra()));
        txtPorcentaje.setText(String.valueOf(p.getPorcentajeGanancia()));
        txtMayoreo.setText(String.valueOf(p.getPrecioMayoreo()));
        txtStock.setText(String.valueOf(p.getStock()));
        txtStockMin.setText(String.valueOf(p.getStockMinimo()));

        cmbEstado.setValue(p.getEstado());
        cmbTipoVenta.setValue(p.getTipoVenta() != null ? p.getTipoVenta() : "UNIDAD");
        cmbCategoria.setValue(p.getCategoria());
        cmbProveedor.setValue(p.getProveedor());
    }
}
