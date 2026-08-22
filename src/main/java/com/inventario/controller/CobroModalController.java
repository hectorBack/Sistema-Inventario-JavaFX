package com.inventario.controller;

import com.inventario.config.ConfiguracionSistema;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class CobroModalController {

    @FXML
    private Label lblTotalArticulos;
    @FXML
    private Label lblTotalPagar;
    @FXML
    private ComboBox<String> cmbFormaPago;
    @FXML
    private TextField txtPagoCon;
    @FXML
    private TextField txtCambio;
    @FXML
    private Button btnCobrarImprimir;
    @FXML
    private Button btnCobrarSolo;
    @FXML
    private Button btnCancelar;

    private double totalAPagar;
    private boolean ventaConfirmada = false;
    private boolean imprimirTicket = false;

    private BiConsumer<Double, Boolean> onPagoConfirmado;

    public void initData(double total, int cantidadArticulos, BiConsumer<Double, Boolean> onPagoConfirmado) {
        this.totalAPagar = total;
        this.onPagoConfirmado = onPagoConfirmado;

        this.lblTotalPagar.setText(String.format("$%.2f", total));
        this.lblTotalArticulos.setText(String.valueOf(cantidadArticulos));

        // 1. CARGAR FORMAS DE PAGO SEGÚN CONFIGURACIÓN
        ObservableList<String> formasPago = FXCollections.observableArrayList("Efectivo");
        boolean ofrecerCredito = ConfiguracionSistema.getInstancia().getOpciones().isOfrecerCredito();

        if (ofrecerCredito) {
            formasPago.add("A Crédito");
        }

        cmbFormaPago.setItems(formasPago);
        cmbFormaPago.getSelectionModel().selectFirst();

        // Escuchar cambios de forma de pago para ajustar el campo Pago Con
        cmbFormaPago.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("A Crédito".equalsIgnoreCase(newVal)) {
                txtPagoCon.setText("0.00");
                txtPagoCon.setDisable(true);
                txtCambio.setText("$0.00");
            } else {
                txtPagoCon.setDisable(false);
                txtPagoCon.setText(String.format(java.util.Locale.US, "%.2f", totalAPagar));
                onCalcularCambio(null);
            }
        });

        txtPagoCon.setText(String.format(java.util.Locale.US, "%.2f", total));
        onCalcularCambio(null);

        Platform.runLater(() -> {
            if (txtPagoCon.getScene() != null) {
                txtPagoCon.getScene().setOnKeyPressed(this::manejarTeclasModal);
            }
            txtPagoCon.requestFocus();
            txtPagoCon.selectAll();
        });
    }

    private void manejarTeclasModal(KeyEvent event) {
        if (event.getCode() == KeyCode.F1) {
            onCobrarEImprimir(null);
            event.consume();
        } else if (event.getCode() == KeyCode.F2) {
            onCobrarSoloRegistro(null);
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            onCancelar(null);
            event.consume();
        }
    }

    @FXML
    void onCalcularCambio(KeyEvent event) {
        try {
            String textoPago = txtPagoCon.getText().trim();
            if (textoPago.isEmpty()) {
                txtCambio.setText("$0.00");
                return;
            }

            double pago = Double.parseDouble(textoPago);
            double cambio = pago - totalAPagar;

            if (cambio >= 0) {
                txtCambio.setText(String.format("$%.2f", cambio));
            } else {
                txtCambio.setText("$0.00");
            }
        } catch (NumberFormatException e) {
            txtCambio.setText("$0.00");
        }
    }

    @FXML
    void onCobrarEImprimir(ActionEvent event) {
        if (validarPago()) {
            this.ventaConfirmada = true;
            this.imprimirTicket = true;

            double montoPago = "A Crédito".equalsIgnoreCase(cmbFormaPago.getValue()) ? 0.0 : Double.parseDouble(txtPagoCon.getText().trim());
            cerrarModal();

            if (onPagoConfirmado != null) {
                onPagoConfirmado.accept(montoPago, true);
            }
        }
    }

    @FXML
    void onCobrarSoloRegistro(ActionEvent event) {
        if (validarPago()) {
            this.ventaConfirmada = true;
            this.imprimirTicket = false;

            double montoPago = "A Crédito".equalsIgnoreCase(cmbFormaPago.getValue()) ? 0.0 : Double.parseDouble(txtPagoCon.getText().trim());
            cerrarModal();

            if (onPagoConfirmado != null) {
                onPagoConfirmado.accept(montoPago, false);
            }
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        this.ventaConfirmada = false;
        cerrarModal();
    }

    private boolean validarPago() {
        String formaPago = cmbFormaPago.getValue();

        // Si la venta es A Crédito no se requiere ingresar pago inmediato
        if ("A Crédito".equalsIgnoreCase(formaPago)) {
            return true;
        }

        try {
            double pago = Double.parseDouble(txtPagoCon.getText().trim());
            if (pago < totalAPagar) {
                mostrarAlerta("Monto Insuficiente", "El pago ingresado es menor al total a pagar.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Incorrecto", "Ingresa un monto numérico válido en el campo 'Pago con:'.");
            return false;
        }
    }

    private void cerrarModal() {
        Stage stage = (Stage) txtPagoCon.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public boolean isVentaConfirmada() {
        return ventaConfirmada;
    }

    public boolean isImprimirTicket() {
        return imprimirTicket;
    }
}
