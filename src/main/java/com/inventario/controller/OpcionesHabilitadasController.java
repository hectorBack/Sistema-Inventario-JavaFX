package com.inventario.controller;

import com.inventario.config.ConfiguracionSistema;
import com.inventario.model.OpcionesHabilitadas;
import com.inventario.repository.Impl.OpcionesHabilitadasRepositoryImpl;
import com.inventario.repository.OpcionesHabilitadasRepository;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

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

    private final OpcionesHabilitadasRepository opcionesRepository = new OpcionesHabilitadasRepositoryImpl();

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

        cargarDatosDesdeBD();
    }

    private void cargarDatosDesdeBD() {
        OpcionesHabilitadas opciones = opcionesRepository.obtenerOpciones();

        if (opciones != null) {
            chkUsarInventario.setSelected(opciones.isUsarInventario());
            chkOfrecerCredito.setSelected(opciones.isOfrecerCredito());
            chkProductoComun.setSelected(opciones.isProductoComun());
            chkCalcularPrecio.setSelected(opciones.isCalcularPrecio());
            txtMargenGanancia.setText(String.valueOf(opciones.getMargenGanancia()));
            chkHabilitarRedondeo.setSelected(opciones.isHabilitarRedondeo());
            cmbTipoRedondeo.setValue(opciones.getTipoRedondeo());
        }
    }

    @FXML
    private void onGuardar() {
        double margenGanancia = 0.0;
        try {
            if (!txtMargenGanancia.getText().trim().isEmpty()) {
                margenGanancia = Double.parseDouble(txtMargenGanancia.getText().trim());
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de entrada", "El margen de ganancia debe ser un número válido.", Alert.AlertType.ERROR);
            return;
        }

        OpcionesHabilitadas opciones = new OpcionesHabilitadas(
                chkUsarInventario.isSelected(),
                chkOfrecerCredito.isSelected(),
                chkProductoComun.isSelected(),
                chkCalcularPrecio.isSelected(),
                margenGanancia,
                chkHabilitarRedondeo.isSelected(),
                cmbTipoRedondeo.getValue() != null ? cmbTipoRedondeo.getValue() : ""
        );

        boolean exito = opcionesRepository.guardarOActualizar(opciones);

        if (exito) {
            ConfiguracionSistema.getInstancia().cargarOpciones();

            // 1. REFRESCO INMEDIATO EN EL SIDEBAR AL GUARDAR
            if (MainLayoutController.getInstancia() != null) {
                MainLayoutController.getInstancia().actualizarEstadoModulos();
            }

            mostrarAlerta("Éxito", "Configuración guardada correctamente en la base de datos.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Error", "No se pudo guardar la configuración.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onVolver(ActionEvent event) {
        // 2. ASEGURA QUE AL REGRESAR EL SIDEBAR TENGA EL ESTADO FRESCO
        if (MainLayoutController.getInstancia() != null) {
            MainLayoutController.getInstancia().actualizarEstadoModulos();
        }

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

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

}
