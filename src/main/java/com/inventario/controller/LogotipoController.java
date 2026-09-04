package com.inventario.controller;

import com.inventario.model.Empresa;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.EmpresaRepository;
import com.inventario.repository.Impl.EmpresaRepositoryImpl;
import com.inventario.util.ImageStorageService;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LogotipoController {

    @FXML
    private ImageView imgLogoPreview;
    @FXML
    private Button btnSeleccionar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnEliminar;

    private final EmpresaRepository empresaRepository = new EmpresaRepositoryImpl();
    private File archivoSeleccionado;
    private Empresa empresaActual;

    @FXML
    public void initialize() {
        cargarLogotipoActual();
    }

    private void cargarLogotipoActual() {
        empresaActual = DTOMapper.toModel(empresaRepository.obtenerConfiguracionDTO());
        if (empresaActual != null && empresaActual.getLogoPath() != null && !empresaActual.getLogoPath().trim().isEmpty()) {
            File fileLogo = new File(empresaActual.getLogoPath());
            if (fileLogo.exists()) {
                imgLogoPreview.setImage(new Image(fileLogo.toURI().toString()));
            }
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Logotipo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) btnSeleccionar.getScene().getWindow();
        File fileTemp = fileChooser.showOpenDialog(stage);

        if (fileTemp != null) {
            archivoSeleccionado = fileTemp;
            imgLogoPreview.setImage(new Image(archivoSeleccionado.toURI().toString()));
        }
    }

    @FXML
    private void guardarLogotipo() {
        if (archivoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Debe seleccionar una nueva imagen antes de guardar.");
            return;
        }

        try {
            String rutaGuardada = ImageStorageService.guardarLogoLocalmente(archivoSeleccionado);
            if (rutaGuardada != null && empresaRepository.actualizarLogoPath(rutaGuardada)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Logotipo guardado correctamente.");
                archivoSeleccionado = null;
                cargarLogotipoActual();

                // Notifica al MainLayoutController para refrescar el Sidebar en vivo
                if (MainLayoutController.getInstancia() != null) {
                    MainLayoutController.getInstancia().cargarLogotipo();
                }

            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la ruta en la base de datos.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "Error al copiar la imagen al almacenamiento local: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarLogotipo() {
        if (empresaActual == null || empresaActual.getLogoPath() == null || empresaActual.getLogoPath().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "No hay ningún logotipo guardado.");
            return;
        }

        ImageStorageService.eliminarLogoLocal(empresaActual.getLogoPath());
        if (empresaRepository.actualizarLogoPath("")) {
            imgLogoPreview.setImage(null);
            archivoSeleccionado = null;
            cargarLogotipoActual();

            // Notifica al MainLayoutController para refrescar el Sidebar en vivo
            if (MainLayoutController.getInstancia() != null) {
                MainLayoutController.getInstancia().cargarLogotipo();
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Logotipo eliminado correctamente.");
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
