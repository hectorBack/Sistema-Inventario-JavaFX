package com.inventario.controller;

import com.inventario.model.DTOs.ConfiguracionImpresoraDTO;
import com.inventario.repository.ConfiguracionImpresoraRepository;
import com.inventario.repository.Impl.ConfiguracionImpresoraRepositoryImpl;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Font;

public class ConfiguracionImpresoraController implements Initializable {

    @FXML
    private ComboBox<String> cbImpresoras;

    @FXML
    private ComboBox<String> cbFuentes;

    @FXML
    private Spinner<Integer> spTamanoFuente;

    @FXML
    private Spinner<Integer> spColumnas;

    @FXML
    private CheckBox chkFuenteNormalTotales;

    @FXML
    private CheckBox chkTodasNegritas;

    @FXML
    private Button btnGuardar;

    private final ConfiguracionImpresoraRepository repository;

    public ConfiguracionImpresoraController() {
        this.repository = new ConfiguracionImpresoraRepositoryImpl();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarSpinners();
        cargarImpresoras();
        cargarFuentes();
        cargarConfiguracionGuardada();
    }

    private void configurarSpinners() {
        // Rango para tamaño de fuente (ej. de 6pt a 72pt, por defecto 10)
        spTamanoFuente.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 72, 10)
        );

        // Rango para número de columnas (ej. de 10 a 120 caracteres, por defecto 36)
        spColumnas.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 120, 36)
        );
    }

    private void cargarImpresoras() {
        ObservableList<String> nombresImpresoras = FXCollections.observableArrayList();
        for (Printer printer : Printer.getAllPrinters()) {
            nombresImpresoras.add(printer.getName());
        }
        cbImpresoras.setItems(nombresImpresoras);

        // Si existe impresora predeterminada en el SO, la selecciona por defecto
        Printer defaultPrinter = Printer.getDefaultPrinter();
        if (defaultPrinter != null && cbImpresoras.getSelectionModel().isEmpty()) {
            cbImpresoras.getSelectionModel().select(defaultPrinter.getName());
        }
    }

    private void cargarFuentes() {
        ObservableList<String> fuentesSistema = FXCollections.observableArrayList(Font.getFamilies());
        cbFuentes.setItems(fuentesSistema);

        // Intenta seleccionar "Courier New" por defecto si existe
        if (fuentesSistema.contains("Courier New")) {
            cbFuentes.getSelectionModel().select("Courier New");
        } else if (!fuentesSistema.isEmpty()) {
            cbFuentes.getSelectionModel().selectFirst();
        }
    }

    private void cargarConfiguracionGuardada() {
        ConfiguracionImpresoraDTO config = repository.obtenerConfiguracion();

        if (config != null) {
            if (config.getNombreImpresora() != null) {
                cbImpresoras.getSelectionModel().select(config.getNombreImpresora());
            }

            if (config.getFuente() != null) {
                cbFuentes.getSelectionModel().select(config.getFuente());
            }

            if (config.getTamanoFuente() > 0) {
                spTamanoFuente.getValueFactory().setValue(config.getTamanoFuente());
            }

            if (config.getColumnas() > 0) {
                spColumnas.getValueFactory().setValue(config.getColumnas());
            }

            chkFuenteNormalTotales.setSelected(config.isUsarFuenteNormalTotales());
            chkTodasNegritas.setSelected(config.isTodasNegritas());
        }
    }

    @FXML
    private void handleGuardar() {
        String impresoraSeleccionada = cbImpresoras.getValue();
        String fuenteSeleccionada = cbFuentes.getValue();

        if (impresoraSeleccionada == null || impresoraSeleccionada.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor selecciona una impresora.");
            return;
        }

        if (fuenteSeleccionada == null || fuenteSeleccionada.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor selecciona una fuente de impresión.");
            return;
        }

        ConfiguracionImpresoraDTO dto = new ConfiguracionImpresoraDTO(
                null,
                impresoraSeleccionada,
                fuenteSeleccionada,
                spTamanoFuente.getValue(),
                spColumnas.getValue(),
                chkFuenteNormalTotales.isSelected(),
                chkTodasNegritas.isSelected()
        );

        boolean guardadoExitoso = repository.guardarOActualizar(dto);

        if (guardadoExitoso) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Configuración de la impresora guardada correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar la configuración en la base de datos.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
