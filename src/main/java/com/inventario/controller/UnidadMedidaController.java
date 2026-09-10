package com.inventario.controller;

import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.UnidadMedidaDTO;
import com.inventario.model.UnidadMedida;
import com.inventario.repository.Impl.UnidadMedidaRepositoryImpl;
import com.inventario.repository.UnidadMedidaRepository;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

public class UnidadMedidaController implements Initializable {

    @FXML
    private VBox containerUnidades;

    private final UnidadMedidaRepository repository;
    private final List<UnidadMedida> listaUnidades = new ArrayList<>();

    public UnidadMedidaController() {
        this(new UnidadMedidaRepositoryImpl());
    }

    public UnidadMedidaController(UnidadMedidaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarUnidades();
    }

    private void cargarUnidades() {
        containerUnidades.getChildren().clear();
        listaUnidades.clear();

        List<UnidadMedidaDTO> dtos = repository.obtenerTodasDTO();
        for (UnidadMedidaDTO dto : dtos) {
            UnidadMedida model = DTOMapper.toModel(dto);
            listaUnidades.add(model);

            CheckBox checkBox = new CheckBox(model.getClave());
            checkBox.setSelected(model.isActivo());

            // Si es la unidad predeterminada (p. ej. PZA), se deshabilita para evitar desmarcarla
            if (model.isPredeterminado()) {
                checkBox.setDisable(true);
            }

            // Enlazar la propiedad del CheckBox con el modelo
            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> model.setActivo(newVal));

            containerUnidades.getChildren().add(checkBox);
        }
    }

    @FXML
    private void onGuardar() {
        boolean exito = true;
        for (UnidadMedida unidad : listaUnidades) {
            UnidadMedidaDTO dto = DTOMapper.toDTO(unidad);
            if (!repository.guardarOActualizarDTO(dto)) {
                exito = false;
            }
        }

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Las unidades de medida se guardaron correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un problema al guardar las unidades.");
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
