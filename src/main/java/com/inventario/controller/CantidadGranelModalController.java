package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.model.Promocion;
import com.inventario.repository.PromocionRepository;
import java.net.URL;
import java.util.List;
import java.util.Locale;
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
    private double precioUnitarioAplicado = 0.0;
    private boolean aceptado = false;
    private boolean editandoManualmente = false;
    private PromocionRepository promocionRepository;
    private double precioActual = 0.0;
    private Promocion promocionAplicada = null;

    public void setPromocionRepository(PromocionRepository repo) {
        this.promocionRepository = repo;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Al escribir en Cantidad -> se calcula el Importe (con promoción si aplica)
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (editandoManualmente || producto == null) {
                return;
            }
            editandoManualmente = true;
            try {
                double cant = Double.parseDouble(newVal.trim().replace(",", "."));
                verificarYAplicarPromocion(cant);
                double importe = cant * precioActual;
                txtImporte.setText(String.format("%.2f", importe));
            } catch (NumberFormatException e) {
                txtImporte.setText("0.00");
            } finally {
                editandoManualmente = false;
            }
        });

        // Al escribir en Importe ($) -> se calcula la Cantidad (Kg/Unidades) con precio actual
        txtImporte.textProperty().addListener((obs, oldVal, newVal) -> {
            if (editandoManualmente || producto == null || precioActual <= 0) {
                return;
            }
            editandoManualmente = true;
            try {
                double importe = Double.parseDouble(newVal.trim().replace(",", "."));
                double cant = importe / precioActual;
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
        this.precioActual = prod.getPrecio();
        this.lblNombreProducto.setText(prod.getNombre().toUpperCase());
        this.lblPrecioUnitario.setText(String.format("$%.2f", prod.getPrecio()));

        editandoManualmente = true;
        this.txtCantidad.setText(String.format("%.3f", cantidadInicial));
        double importeInicial = cantidadInicial * prod.getPrecio();
        this.txtImporte.setText(String.format("%.2f", importeInicial));
        verificarYAplicarPromocion(cantidadInicial);
        editandoManualmente = false;
    }

    private void verificarYAplicarPromocion(double cantidad) {
        if (producto == null || promocionRepository == null) {
            precioActual = producto != null ? producto.getPrecio() : 0.0;
            promocionAplicada = null;
            return;
        }

        // Buscar promociones activas para este producto
        List<Promocion> promociones = promocionRepository.listarActivas();
        Promocion promoActiva = null;

        for (Promocion promo : promociones) {
            if (promo.getCodigoBarrasProducto().equals(producto.getCodigoBarras())
                    && cantidad >= promo.getCantidadDesde()
                    && cantidad <= promo.getCantidadHasta()) {
                promoActiva = promo;
                break;
            }
        }

        if (promoActiva != null) {
            precioActual = promoActiva.getPrecioPromocion();
            promocionAplicada = promoActiva;
            // Actualizar el label para mostrar que hay promoción activa
            this.lblPrecioUnitario.setText(String.format("$%.2f (PROMOCIÓN)", precioActual));
            this.lblPrecioUnitario.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        } else {
            precioActual = producto.getPrecio();
            promocionAplicada = null;
            this.lblPrecioUnitario.setText(String.format("$%.2f", producto.getPrecio()));
            this.lblPrecioUnitario.setStyle("-fx-text-fill: inherit; -fx-font-weight: bold;");
        }
    }

    @FXML
    void onAceptar(ActionEvent event) {
        try {
            double cant = Double.parseDouble(txtCantidad.getText().trim().replace(",", "."));
            if (cant <= 0) {
                return;
            }
            this.cantidadIngresada = cant;
            this.precioUnitarioAplicado = this.precioActual;
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

    public double getPrecioUnitarioAplicado() {
        return precioUnitarioAplicado;
    }
}
