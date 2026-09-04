package com.inventario.controller;

import com.inventario.model.Caja;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.CajaRepository;
import com.inventario.repository.Impl.CajaRepositoryImpl;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CajasController {

    @FXML
    private ListView<Caja> lstCajas;
    @FXML
    private VBox panelDetalle;
    @FXML
    private Label lblTituloFormulario;
    @FXML
    private TextField txtNombreCaja;
    @FXML
    private ComboBox<String> cmbTipoCaja;
    @FXML
    private ComboBox<Caja> cmbCajaPadre;
    @FXML
    private Label lblUltimoAcceso;
    @FXML
    private Label lblEstado;
    @FXML
    private Button btnEliminar;

    private final CajaRepository cajaRepository;
    private final ObservableList<Caja> listaCajas;
    private Caja cajaSeleccionada;
    private boolean esNuevoRegistro = false;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public CajasController() {
        this.cajaRepository = new CajaRepositoryImpl();
        this.listaCajas = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        lstCajas.setItems(listaCajas);

        // Opciones de tipo de caja
        cmbTipoCaja.setItems(FXCollections.observableArrayList("PRINCIPAL", "SECUNDARIA"));

        // Habilitar/Deshabilitar el combo de caja padre según el tipo elegido
        cmbTipoCaja.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esSecundaria = "SECUNDARIA".equals(newVal);
            cmbCajaPadre.setDisable(!esSecundaria);
            if (!esSecundaria) {
                cmbCajaPadre.getSelectionModel().clearSelection();
            }
        });

        // Evento de selección en la lista
        lstCajas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                esNuevoRegistro = false;
                cajaSeleccionada = newSel;
                mostrarDetalleCaja(newSel);
            }
        });

        cargarCajas();
    }

    private void cargarCajas() {
        listaCajas.clear();
        listaCajas.addAll(cajaRepository.obtenerTodasDTO().stream()
            .map(DTOMapper::toModel)
            .collect(Collectors.toList()));
        actualizarComboCajasPadre();
    }

    private void actualizarComboCajasPadre() {
        ObservableList<Caja> cajasPrincipales = FXCollections.observableArrayList(
                listaCajas.stream()
                        .filter(c -> "PRINCIPAL".equalsIgnoreCase(c.getTipo()))
                        .filter(c -> cajaSeleccionada == null || c.getId() != cajaSeleccionada.getId())
                        .collect(Collectors.toList()) // <--- Cambiado .toList() por .collect(Collectors.toList())
        );
        cmbCajaPadre.setItems(cajasPrincipales);
    }

    @FXML
    private void accionNuevaCaja() {
        lstCajas.getSelectionModel().clearSelection();
        cajaSeleccionada = new Caja("", "ACTIVA", "SECUNDARIA", null);
        esNuevoRegistro = true;

        panelDetalle.setDisable(false);
        lblTituloFormulario.setText("Crear Nueva Caja");
        txtNombreCaja.setText("");
        cmbTipoCaja.setValue("SECUNDARIA");
        lblEstado.setText("ACTIVA (Nueva)");
        lblUltimoAcceso.setText("Sin accesos");
        btnEliminar.setDisable(true);
        txtNombreCaja.requestFocus();
    }

    private void mostrarDetalleCaja(Caja caja) {
        panelDetalle.setDisable(false);
        lblTituloFormulario.setText("Detalle de Caja: " + caja.getNombre());
        txtNombreCaja.setText(caja.getNombre());
        cmbTipoCaja.setValue(caja.getTipo());
        lblEstado.setText(caja.getEstado());
        btnEliminar.setDisable(false);

        actualizarComboCajasPadre();

        // Seleccionar la caja padre en el ComboBox
        if (caja.getCajaPadreId() != null) {
            listaCajas.stream()
                    .filter(c -> c.getId() == caja.getCajaPadreId())
                    .findFirst()
                    .ifPresent(padre -> cmbCajaPadre.setValue(padre));
        } else {
            cmbCajaPadre.getSelectionModel().clearSelection();
        }

        if (caja.getFechaUltimoAcceso() != null) {
            lblUltimoAcceso.setText(caja.getFechaUltimoAcceso().format(FORMATO_FECHA));
        } else {
            lblUltimoAcceso.setText("Sin accesos registrados");
        }
    }

    @FXML
    private void accionGuardar() {
        String nombre = txtNombreCaja.getText() != null ? txtNombreCaja.getText().trim() : "";
        String tipo = cmbTipoCaja.getValue();
        Caja cajaPadre = cmbCajaPadre.getValue();

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Ingresa un nombre para la caja.");
            return;
        }

        if ("SECUNDARIA".equals(tipo) && cajaPadre == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Vínculo Requerido", "Debes seleccionar a qué Caja Principal reporta esta caja secundaria.");
            return;
        }

        cajaSeleccionada.setNombre(nombre);
        cajaSeleccionada.setTipo(tipo);
        cajaSeleccionada.setCajaPadreId(cajaPadre != null ? cajaPadre.getId() : null);

        boolean exito;
        if (esNuevoRegistro) {
            exito = cajaRepository.guardarDTO(DTOMapper.toDTO(cajaSeleccionada));
        } else {
            exito = cajaRepository.actualizarDTO(DTOMapper.toDTO(cajaSeleccionada));
        }

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La caja se guardó correctamente.");
            cargarCajas();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la caja.");
        }
    }

    @FXML
    private void accionEliminarCaja() {
        if (cajaSeleccionada == null || esNuevoRegistro) {
            return;
        }

        // Regla 1: Debe estar inactiva
        if ("ACTIVA".equalsIgnoreCase(cajaSeleccionada.getEstado())) {
            mostrarAlerta(Alert.AlertType.WARNING, "No se puede eliminar",
                    "La caja está ACTIVA. Para eliminarla debe cambiar su estado a INACTIVA.");
            return;
        }

        // Regla 2: Más de 5 días inactiva
        LocalDateTime ultimoAcceso = cajaSeleccionada.getFechaUltimoAcceso();
        if (ultimoAcceso != null) {
            long diasInactiva = Duration.between(ultimoAcceso, LocalDateTime.now()).toDays();
            if (diasInactiva <= 5) {
                mostrarAlerta(Alert.AlertType.WARNING, "Restricción de tiempo",
                        "La caja lleva sólo " + diasInactiva + " día(s) inactiva. Debe tener más de 5 días de inactividad.");
                return;
            }
        }

        if (cajaRepository.eliminar(cajaSeleccionada.getId())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminada", "Caja eliminada exitosamente.");
            panelDetalle.setDisable(true);
            cargarCajas();
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
