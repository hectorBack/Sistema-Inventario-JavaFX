package com.inventario.controller;

import com.inventario.model.ConfiguracionMoneda;
import com.inventario.model.DTOs.ConfiguracionMonedaDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.ConfiguracionMonedaRepository;
import com.inventario.repository.Impl.ConfiguracionMonedaRepositoryImpl;
import com.inventario.util.FormatoMonedaUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ConfiguracionMonedaController implements Initializable {

    @FXML
    private TextField txtSimboloMoneda;
    @FXML
    private TextField txtSeparadorMiles;
    @FXML
    private TextField txtSeparadorDecimal;

    private final ConfiguracionMonedaRepository repository;
    private ConfiguracionMoneda configuracionActual;

    public ConfiguracionMonedaController() {
        this(new ConfiguracionMonedaRepositoryImpl());
    }

    public ConfiguracionMonedaController(ConfiguracionMonedaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }

    private void cargarDatos() {
        ConfiguracionMonedaDTO dto = repository.obtenerConfiguracion();
        configuracionActual = DTOMapper.toModel(dto);

        if (configuracionActual != null) {
            txtSimboloMoneda.setText(configuracionActual.getSimboloMoneda());
            txtSeparadorMiles.setText(configuracionActual.getSeparadorMiles());
            txtSeparadorDecimal.setText(configuracionActual.getSeparadorDecimal());
        }
    }

    @FXML
    private void onGuardar() {
        String simbolo = txtSimboloMoneda.getText().trim();
        String miles = txtSeparadorMiles.getText().trim();
        String decimal = txtSeparadorDecimal.getText().trim();

        if (simbolo.isEmpty() || miles.isEmpty() || decimal.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos de configuración de moneda son obligatorios.");
            return;
        }

        if (miles.equals(decimal)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Configuración Inválida", "El separador de miles y el separador decimal no pueden ser iguales.");
            return;
        }

        configuracionActual.setSimboloMoneda(simbolo);
        configuracionActual.setSeparadorMiles(miles);
        configuracionActual.setSeparadorDecimal(decimal);

        ConfiguracionMonedaDTO dto = DTOMapper.toDTO(configuracionActual);

        if (repository.guardarOActualizarDTO(dto)) {
            FormatoMonedaUtil.cargarConfiguracion();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La configuración de moneda se guardó correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la configuración de moneda.");
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
