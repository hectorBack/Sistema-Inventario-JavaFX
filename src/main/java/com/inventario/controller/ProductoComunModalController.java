package com.inventario.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ProductoComunModalController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNombreProducto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancelar;

    private boolean aceptado = false;
    private String nombreProducto;
    private double cantidad = 1.0;
    private double precio = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtCantidad.setText("1.000");
        txtPrecio.setText("0.00");

        // Foco inicial automático al campo del nombre del artículo
        Platform.runLater(() -> {
            if (txtNombreProducto != null) {
                txtNombreProducto.requestFocus();
            }
        });
    }

    @FXML
    void onAceptar(ActionEvent event) {
        String nombreStr = txtNombreProducto.getText() != null ? txtNombreProducto.getText().trim() : "";
        String cantStr = txtCantidad.getText() != null ? txtCantidad.getText().trim() : "";
        String precioStr = txtPrecio.getText() != null ? txtPrecio.getText().trim() : "";

        if (nombreStr.isEmpty()) {
            mostrarAlerta("Nombre Requerido", "Debes ingresar una descripción o nombre para el artículo.", Alert.AlertType.WARNING);
            txtNombreProducto.requestFocus();
            return;
        }

        try {
            double cantParsed = Double.parseDouble(cantStr);
            if (cantParsed <= 0) {
                mostrarAlerta("Cantidad Inválida", "La cantidad debe ser mayor a 0.", Alert.AlertType.WARNING);
                txtCantidad.requestFocus();
                return;
            }
            this.cantidad = cantParsed;
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Inválido", "Ingresa una cantidad numérica válida.", Alert.AlertType.WARNING);
            txtCantidad.requestFocus();
            return;
        }

        try {
            double precioParsed = Double.parseDouble(precioStr);
            if (precioParsed < 0) {
                mostrarAlerta("Precio Inválido", "El precio no puede ser negativo.", Alert.AlertType.WARNING);
                txtPrecio.requestFocus();
                return;
            }
            this.precio = precioParsed;
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Inválido", "Ingresa un precio numérico válido.", Alert.AlertType.WARNING);
            txtPrecio.requestFocus();
            return;
        }

        this.nombreProducto = nombreStr;
        this.aceptado = true;
        cerrarVentana();
    }

    @FXML
    void onCancelar(ActionEvent event) {
        this.aceptado = false;
        cerrarVentana();
    }

    @FXML
    void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onAceptar(null);
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            onCancelar(null);
            event.consume();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnAceptar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Getters para consultar el resultado desde el controlador invocador (VentasController)
    public boolean isAceptado() {
        return aceptado;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }
}
