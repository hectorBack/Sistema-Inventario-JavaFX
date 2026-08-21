package com.inventario.controller;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class OpcionesHabilitadasController {

    @FXML
    private CheckBox chkUsarInventario;
    @FXML
    private CheckBox chkOfrecerCredito;
    @FXML
    private CheckBox chkProductoComun;

    @FXML
    private CheckBox chkCalcularPrecio;
    @FXML
    private TextField txtMargenGanancia;

    @FXML
    private CheckBox chkHabilitarRedondeo;
    @FXML
    private ComboBox<String> cmbTipoRedondeo;

    @FXML
    public void initialize() {
        // Cargar opciones del ComboBox de redondeo
        cmbTipoRedondeo.setItems(FXCollections.observableArrayList(
                "Al peso más cercano",
                "Hacia arriba (Favor del negocio)",
                "Hacia abajo (Favor del cliente)"
        ));

        // Habilitar / deshabilitar inputs dinámicamente según el CheckBox
        txtMargenGanancia.disableProperty().bind(chkCalcularPrecio.selectedProperty().not());
        cmbTipoRedondeo.disableProperty().bind(chkHabilitarRedondeo.selectedProperty().not());
    }

    @FXML
    private void onGuardar() {
        // Aquí lees los valores y llamas a tu servicio/utilidad de configuración
        boolean usarInventario = chkUsarInventario.isSelected();
        boolean ofrecerCredito = chkOfrecerCredito.isSelected();
        boolean productoComun = chkProductoComun.isSelected();

        boolean calcularPrecio = chkCalcularPrecio.isSelected();
        String margenText = txtMargenGanancia.getText();

        boolean redondear = chkHabilitarRedondeo.isSelected();
        String tipoRedondeo = cmbTipoRedondeo.getValue();

        // TODO: Enviar a tu backend o clase de utilidades
    }

    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventario/view/configuracion.fxml"));
            Parent vistaConfiguracion = loader.load();

            Node node = (Node) event.getSource();
            BorderPane mainLayout = (BorderPane) node.getScene().lookup("#mainLayout");

            if (mainLayout != null) {
                mainLayout.setCenter(vistaConfiguracion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
