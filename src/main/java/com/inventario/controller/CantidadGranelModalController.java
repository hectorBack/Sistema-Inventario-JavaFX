package com.inventario.controller;

import com.inventario.model.Producto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CantidadGranelModalController implements Initializable {

    @FXML
    private Label lblNombreProducto;
    @FXML
    private Label lblPrecioUnitario;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtImporte;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancelar;

    private Producto producto;
    private double cantidadIngresada = 0.0;
    private boolean aceptado = false;
    private boolean editandoManualmente = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Al escribir en Cantidad -> se calcula el Importe
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (editandoManualmente || producto == null || producto.getPrecio() <= 0) {
                return;
            }
            editandoManualmente = true;
            try {
                double cant = Double.parseDouble(newVal.trim().replace(",", "."));
                double importe = cant * producto.getPrecio();
                txtImporte.setText(String.format("%.2f", importe));
            } catch (NumberFormatException e) {
                txtImporte.setText("0.00");
            } finally {
                editandoManualmente = false;
            }
        });

        // Al escribir en Importe ($) -> se calcula la Cantidad (Kg/Unidades)
        txtImporte.textProperty().addListener((obs, oldVal, newVal) -> {
            if (editandoManualmente || producto == null || producto.getPrecio() <= 0) {
                return;
            }
            editandoManualmente = true;
            try {
                double importe = Double.parseDouble(newVal.trim().replace(",", "."));
                double cant = importe / producto.getPrecio();
                txtCantidad.setText(String.format("%.3f", cant));
            } catch (NumberFormatException e) {
                txtCantidad.setText("0.000");
            } finally {
                editandoManualmente = false;
            }
        });

        Platform.runLater(() -> {
            txtCantidad.requestFocus();
            txtCantidad.selectAll();
        });
    }

    public void initData(Producto prod, double cantidadInicial) {
        this.producto = prod;
        this.lblNombreProducto.setText(prod.getNombre().toUpperCase());
        this.lblPrecioUnitario.setText(String.format("$%.2f", prod.getPrecio()));

        editandoManualmente = true;
        this.txtCantidad.setText(String.format("%.3f", cantidadInicial));
        double importeInicial = cantidadInicial * prod.getPrecio();
        this.txtImporte.setText(String.format("%.2f", importeInicial));
        editandoManualmente = false;
    }

    @FXML
    void onAceptar(ActionEvent event) {
        try {
            double cant = Double.parseDouble(txtCantidad.getText().trim().replace(",", "."));
            if (cant <= 0) {
                return;
            }
            this.cantidadIngresada = cant;
            this.aceptado = true;
            cerrarVentana();
        } catch (NumberFormatException e) {
            txtCantidad.requestFocus();
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        this.aceptado = false;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtCantidad.getScene().getWindow();
        stage.close();
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public double getCantidadIngresada() {
        return cantidadIngresada;
    }
}
