package com.inventario.controller;

import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DTOs.ImpuestoDTO;
import com.inventario.model.Impuesto;
import com.inventario.repository.Impl.ImpuestoRepositoryImpl;
import com.inventario.repository.ImpuestoRepository;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ConfiguracionImpuestosController implements Initializable {

    @FXML
    private ComboBox<String> cmbPais;
    @FXML
    private CheckBox chkUsaImpuestos;
    @FXML
    private VBox vboxTablaImpuestos;

    @FXML
    private ComboBox<String> cmbNombre;
    @FXML
    private TextField txtPorcentaje;
    @FXML
    private CheckBox chkDesglosarTicket;
    @FXML
    private CheckBox chkPreciosConImpuesto;
    @FXML
    private Button btnAgregar;

    @FXML
    private TableView<Impuesto> tblImpuestos;
    @FXML
    private TableColumn<Impuesto, Integer> colId;
    @FXML
    private TableColumn<Impuesto, String> colNombre;
    @FXML
    private TableColumn<Impuesto, Double> colPorcentaje;
    @FXML
    private TableColumn<Impuesto, Boolean> colIncluidoNuevosProductos;
    @FXML
    private TableColumn<Impuesto, Void> colAcciones;

    private final ImpuestoRepository repository;
    private final ObservableList<Impuesto> listaImpuestos = FXCollections.observableArrayList();
    private Impuesto impuestoSeleccionado;

    public ConfiguracionImpuestosController() {
        this(new ImpuestoRepositoryImpl());
    }

    public ConfiguracionImpuestosController(ImpuestoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configurar Opciones de los ComboBoxes
        cmbPais.setItems(FXCollections.observableArrayList("México", "Otro"));
        cmbPais.getSelectionModel().select("México");

        cmbNombre.setItems(FXCollections.observableArrayList(
                "Impuesto al Valor Agregado (IVA)",
                "Impuesto sobre Hospedaje (ISH)",
                "Impuesto Especial sobre Productos y Servicios (IEPS)"
        ));

        // 2. Controlar la Visibilidad y Desplazamiento Dinámico del VBox de la Tabla mediante Binding
        vboxTablaImpuestos.visibleProperty().bind(chkUsaImpuestos.selectedProperty());
        vboxTablaImpuestos.managedProperty().bind(chkUsaImpuestos.selectedProperty());

        // 3. Configurar Columnas de la Tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        colIncluidoNuevosProductos.setCellValueFactory(data -> data.getValue().preciosConImpuestoProperty());
        colIncluidoNuevosProductos.setCellFactory(CheckBoxTableCell.forTableColumn(colIncluidoNuevosProductos));
        colIncluidoNuevosProductos.setEditable(true);
        tblImpuestos.setEditable(true);

        tblImpuestos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            impuestoSeleccionado = newSelection;
            if (newSelection == null) {
                btnAgregar.setText("Agregar Impuesto");
                return;
            }

            cmbNombre.getSelectionModel().select(newSelection.getNombre());
            txtPorcentaje.setText(String.valueOf(newSelection.getPorcentaje()));
            cmbPais.getSelectionModel().select(newSelection.getPais());
            chkDesglosarTicket.setSelected(newSelection.isDesglosarTicket());
            chkPreciosConImpuesto.setSelected(newSelection.isPreciosConImpuesto());
            btnAgregar.setText("Actualizar Impuesto");
        });

        // Configurar Columna de Acción (Eliminar)
        configurarColumnaAcciones();

        tblImpuestos.setItems(listaImpuestos);

        // 4. Cargar datos iniciales desde el Repositorio
        cargarDatos();
    }

    private void cargarDatos() {
        listaImpuestos.clear();
        List<ImpuestoDTO> dtos = repository.obtenerTodosDTO();
        for (ImpuestoDTO dto : dtos) {
            listaImpuestos.add(DTOMapper.toModel(dto));
        }

        // Si existen impuestos guardados, activar el checkbox por defecto
        if (!listaImpuestos.isEmpty()) {
            chkUsaImpuestos.setSelected(true);
        }
    }

    @FXML
    private void onAgregarImpuesto() {
        String nombre = cmbNombre.getValue();
        String porcentajeStr = txtPorcentaje.getText().trim();
        String pais = cmbPais.getValue();
        boolean desglosar = chkDesglosarTicket.isSelected();
        boolean preciosConImp = chkPreciosConImpuesto.isSelected();

        if (nombre == null || nombre.isEmpty() || porcentajeStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor seleccione el impuesto y complete el porcentaje.");
            return;
        }

        try {
            double porcentaje = Double.parseDouble(porcentajeStr);
            if (porcentaje < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor Inválido", "El porcentaje no puede ser negativo.");
                return;
            }

                int id = impuestoSeleccionado == null ? 0 : impuestoSeleccionado.getId();
                String estado = impuestoSeleccionado == null ? "ACTIVO" : impuestoSeleccionado.getEstado();
            Impuesto nuevoImpuesto = new Impuesto(
                    id,
                    nombre,
                    porcentaje,
                    pais,
                    estado,
                    desglosar,
                    preciosConImp
            );

            ImpuestoDTO dto = DTOMapper.toDTO(nuevoImpuesto);

            if (repository.guardarOActualizarDTO(dto)) {
                if (nombre.toUpperCase().contains("IVA")) {
                    preguntarAgregarIvaAProductos(porcentaje);
                }
                tblImpuestos.getSelectionModel().clearSelection();
                limpiarFormulario();
                cargarDatos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el impuesto en la base de datos.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Incorrecto", "El porcentaje debe ser un número válido.");
        }
    }

    private void limpiarFormulario() {
        cmbNombre.getSelectionModel().clearSelection();
        txtPorcentaje.clear();
        chkDesglosarTicket.setSelected(false);
        chkPreciosConImpuesto.setSelected(false);
    }

    @FXML
    private void onGuardar() {
        if (impuestoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Seleccione un impuesto para actualizar su configuración.");
            return;
        }
        onAgregarImpuesto();
    }

    private void preguntarAgregarIvaAProductos(double porcentaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Agregar IVA a tus productos");
        alert.setHeaderText("¿Deseas que se agregue el impuesto IVA a todos los productos dados de alta actualmente?");
        alert.setContentText(null);

        ButtonType agregar = new ButtonType(
                "Si, agregar IVA a todos mis productos y sumar este impuesto a los precios actuales");
        ButtonType dejarIntactos = new ButtonType(
                "No, dejar los productos intactos", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(agregar, dejarIntactos);

        if (alert.showAndWait().orElse(dejarIntactos) == agregar
                && repository.aplicarImpuestoAProductos(porcentaje) < 0) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo agregar el IVA a los precios de los productos.");
        }
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand;");
                btnEliminar.setOnAction(event -> {
                    Impuesto impuesto = getTableView().getItems().get(getIndex());
                    if (repository.eliminarDTO(impuesto.getId())) {
                        cargarDatos();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el impuesto.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
