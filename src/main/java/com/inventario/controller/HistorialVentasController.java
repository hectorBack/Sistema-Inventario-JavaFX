package com.inventario.controller;

import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import com.inventario.repository.ClienteRepository;
import com.inventario.repository.VentaRepository;
import com.inventario.repository.Impl.ClienteRepositoryImpl;
import com.inventario.repository.Impl.VentaRepositoryImpl;
import com.inventario.util.Ventas.ExcelExporter;
import com.inventario.util.Ventas.FiltroPeriodo;
import com.inventario.util.Ventas.ReportePrinterManager;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HistorialVentasController implements Initializable {

    @FXML
    private Label lblMostrarVentas;
    @FXML
    private ComboBox<FiltroPeriodo> cmbPeriodo;
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFin;
    @FXML
    private ComboBox<Cliente> cmbFiltroCliente;

    // Tabla de Ventas (Historial Principal)
    @FXML
    private TableView<Venta> tblVentas;
    @FXML
    private TableColumn<Venta, Integer> colId;
    @FXML
    private TableColumn<Venta, String> colCliente;
    @FXML
    private TableColumn<Venta, String> colFecha;
    @FXML
    private TableColumn<Venta, Double> colTotal;
    @FXML
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TableColumn<Venta, Void> colAcciones;

    @FXML
    private Label lblTotalHistorico;

    private final VentaRepository ventaRepository = new VentaRepositoryImpl();
    private final ClienteRepository clienteRepository = new ClienteRepositoryImpl();
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablas();
        cargarClientes();
        configurarFiltroPeriodo();

        // Cargar por defecto la opción "Hoy"
        cmbPeriodo.getSelectionModel().select(FiltroPeriodo.HOY);
        aplicarRangoPorPeriodo(FiltroPeriodo.HOY);
        onBuscarVentas(null);
    }

    private void configurarFiltroPeriodo() {
        if (lblMostrarVentas != null) {
            lblMostrarVentas.setText("Mostrar ventas de:");
        }
        cmbPeriodo.setItems(FXCollections.observableArrayList(FiltroPeriodo.values()));
        cmbPeriodo.setOnAction(event -> {
            FiltroPeriodo seleccionado = cmbPeriodo.getValue();
            if (seleccionado != null) {
                aplicarRangoPorPeriodo(seleccionado);
                onBuscarVentas(null);
            }
        });
    }

    private void aplicarRangoPorPeriodo(FiltroPeriodo periodo) {
        if (periodo == FiltroPeriodo.PERIODO_PERSONALIZADO) {
            dpInicio.setDisable(false);
            dpFin.setDisable(false);
        } else {
            LocalDate[] rango = periodo.obtenerRangoFechas();
            if (rango != null) {
                dpInicio.setValue(rango[0]);
                dpFin.setValue(rango[1]);
                dpInicio.setDisable(true);
                dpFin.setDisable(true);
            }
        }
    }

    private void configurarTablas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colCliente.setCellValueFactory(cellData -> {
            Venta venta = cellData.getValue();
            return new SimpleStringProperty(venta != null && venta.getCliente() != null ? venta.getCliente().getNombre() : "Público en General");
        });

        colFecha.setCellValueFactory(cellData -> {
            Venta venta = cellData.getValue();
            return new SimpleStringProperty(venta != null && venta.getFecha() != null ? venta.getFecha().format(formatter) : "");
        });

        colTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue() != null ? cellData.getValue().getTotal() : 0.0));
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        colTotal.setCellFactory(tc -> new TableCell<Venta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> new TableCell<Venta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("CANCELADA".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });

        configurarColumnaAcciones();
        tblVentas.setItems(listaVentas);
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<Venta, Void>() {
            private final Button btnDetalle = new Button("👁 Detalle");

            {
                btnDetalle.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
                btnDetalle.setOnAction(event -> {
                    Venta ventaSel = getTableView().getItems().get(getIndex());
                    abrirModalDetalles(ventaSel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnDetalle);
                    box.setStyle("-fx-alignment: CENTER;");
                    setGraphic(box);
                }
            }
        });
    }

    private void abrirModalDetalles(Venta venta) {
        try {
            URL fxmlLocation = getClass().getResource("/com/inventario/view/DetalleVentaModal.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("DetalleVentaModal.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            DetalleVentaModalController controller = loader.getController();
            controller.initData(venta);

            Stage modalStage = new Stage();
            modalStage.setTitle("Detalles de Venta #" + venta.getId());
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            if (controller.isEstadoCambiado()) {
                onBuscarVentas(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la vista de detalles: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarClientes() {
        List<Cliente> clientes = clienteRepository.listarTodos();
        cmbFiltroCliente.setItems(FXCollections.observableArrayList(clientes));

        // Asignación explícita de ListCell<Cliente> para evitar incompatible types
        cmbFiltroCliente.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cmbFiltroCliente.setButtonCell(cmbFiltroCliente.getCellFactory().call(null));
    }

    @FXML
    void onBuscarVentas(ActionEvent event) {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();
        Cliente cliente = cmbFiltroCliente.getValue();
        Integer clienteId = cliente != null ? cliente.getId() : null;

        // Si los campos están vacíos, fallback a hoy
        LocalDate fechaInicio = (inicio != null) ? inicio : LocalDate.now();
        LocalDate fechaFin = (fin != null) ? fin : LocalDate.now();

        List<Venta> resultados = ventaRepository.buscarPorRangoFechas(fechaInicio, fechaFin);

        if (clienteId != null) {
            resultados = resultados.stream()
                    .filter(v -> v.getCliente() != null && v.getCliente().getId() == clienteId)
                    .collect(Collectors.toList());
        }

        listaVentas.setAll(resultados);
        recalcularSumatoriaHistorica();
    }

    @FXML
    void onLimpiarFiltros(ActionEvent event) {
        cmbPeriodo.getSelectionModel().select(FiltroPeriodo.HOY);
        aplicarRangoPorPeriodo(FiltroPeriodo.HOY);
        cmbFiltroCliente.getSelectionModel().clearSelection();
        onBuscarVentas(null);
    }

    @FXML
    void onExportarExcel(ActionEvent event) {
        if (listaVentas.isEmpty()) {
            mostrarAlerta("Atención", "No hay registros disponibles para exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Historial de Ventas");
        fileChooser.setInitialFileName("Historial_Ventas_" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel / CSV (*.csv)", "*.csv")
        );

        Stage stage = (Stage) tblVentas.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                ExcelExporter.exportarVentasCSV(listaVentas, file);
                mostrarAlerta("Éxito", "El archivo se ha exportado correctamente en:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo exportar el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onImprimir(ActionEvent event) {
        if (listaVentas.isEmpty()) {
            mostrarAlerta("Atención", "No hay registros para imprimir.", Alert.AlertType.WARNING);
            return;
        }

        boolean impreso = ReportePrinterManager.imprimirNodo(tblVentas);
        if (impreso) {
            mostrarAlerta("Éxito", "El documento fue enviado a la impresora.", Alert.AlertType.INFORMATION);
        }
    }

    private void recalcularSumatoriaHistorica() {
        double totalCompleto = listaVentas.stream()
                .filter(v -> "COMPLETADA".equalsIgnoreCase(v.getEstado()))
                .mapToDouble(Venta::getTotal)
                .sum();
        lblTotalHistorico.setText(String.format("$%.2f", totalCompleto));
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
