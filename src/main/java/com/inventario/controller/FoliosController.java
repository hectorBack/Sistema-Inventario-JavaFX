package com.inventario.controller;

import com.inventario.model.Folio;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.FolioRepository;
import com.inventario.repository.Impl.FolioRepositoryImpl;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FoliosController {

    @FXML
    private TextField txtNombreFolio;

    @FXML
    private TextField txtSerie;

    @FXML
    private TextField txtFolioActual;

    @FXML
    private Label lblPrevisualizacion;

    private final FolioRepository folioRepository;
    private Folio folioActual;
    private static final String MODULO_VENTAS = "VENTAS";

    public FoliosController() {
        this.folioRepository = new FolioRepositoryImpl();
    }

    @FXML
    public void initialize() {
        // Permitir únicamente dígitos en el campo del folio
        txtFolioActual.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtFolioActual.setText(newValue.replaceAll("[^\\d]", ""));
            }
            actualizarPrevisualizacion();
        });

        // Escuchar cambios en la serie para refrescar la vista previa
        txtSerie.textProperty().addListener((observable, oldValue, newValue) -> actualizarPrevisualizacion());

        // Cargar los datos guardados actualmente en BD
        cargarFolioVentas();
    }

    private void cargarFolioVentas() {
        Optional<Folio> folioOpt = folioRepository.obtenerPorModuloDTO(MODULO_VENTAS)
            .map(DTOMapper::toModel);

        if (folioOpt.isPresent()) {
            folioActual = folioOpt.get();
        } else {
            // Si no existe el registro inicial para Ventas en la BD, creamos uno por defecto
            folioActual = new Folio("Folio Principal de Tickets", MODULO_VENTAS, "TCK", 1, 6, "ACTIVO");
        }

        txtNombreFolio.setText(folioActual.getNombre());
        txtSerie.setText(folioActual.getSerie());
        txtFolioActual.setText(String.valueOf(folioActual.getFolioActual()));

        actualizarPrevisualizacion();
    }

    private void actualizarPrevisualizacion() {
        String serie = txtSerie.getText() != null ? txtSerie.getText().trim() : "";
        String prefijo = !serie.isEmpty() ? serie + "-" : "";

        int numeroFolio = 1;
        try {
            if (txtFolioActual.getText() != null && !txtFolioActual.getText().trim().isEmpty()) {
                numeroFolio = Integer.parseInt(txtFolioActual.getText().trim());
            }
        } catch (NumberFormatException ignored) {
            numeroFolio = 1;
        }

        int ceros = folioActual != null && folioActual.getLongitudCeros() > 0 ? folioActual.getLongitudCeros() : 6;
        String formato = String.format("%s%0" + ceros + "d", prefijo, numeroFolio);

        if (lblPrevisualizacion != null) {
            lblPrevisualizacion.setText(formato);
        }
    }

    @FXML
    private void accionGuardarFolio() {
        String nombre = txtNombreFolio.getText() != null ? txtNombreFolio.getText().trim() : "";
        String serie = txtSerie.getText() != null ? txtSerie.getText().trim() : "";
        String folioTexto = txtFolioActual.getText() != null ? txtFolioActual.getText().trim() : "";

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Por favor ingresa un nombre para la configuración de folios.");
            return;
        }

        if (folioTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Por favor ingresa el número de folio inicial/siguiente.");
            return;
        }

        int numeroFolio;
        try {
            numeroFolio = Integer.parseInt(folioTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Valor Inválido", "El folio debe ser un número entero válido.");
            return;
        }

        // Actualizamos las propiedades de nuestro modelo
        folioActual.setNombre(nombre);
        folioActual.setSerie(serie);
        folioActual.setFolioActual(numeroFolio);

        boolean exito;
        if (folioActual.getId() > 0) {
            exito = folioRepository.actualizarDTO(DTOMapper.toDTO(folioActual));
        } else {
            exito = folioRepository.guardarDTO(DTOMapper.toDTO(folioActual));
        }

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado Exitoso", "La configuración del folio de tickets se actualizó correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al intentar guardar la configuración en la base de datos.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
