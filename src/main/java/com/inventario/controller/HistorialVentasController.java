package com.inventario.controller;

import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import com.inventario.repository.ClienteRepository;
import com.inventario.repository.VentaRepository;
import com.inventario.repository.Impl.ClienteRepositoryImpl;
import com.inventario.repository.Impl.VentaRepositoryImpl;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistorialVentasController implements Initializable {

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

    // Tabla de Detalle (Ticket de la venta seleccionada)
    @FXML
    private TableView<DetalleVenta> tblDetalleVenta;
    @FXML
    private TableColumn<DetalleVenta, String> colProdNombre;
    @FXML
    private TableColumn<DetalleVenta, Integer> colProdCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colProdPrecio;
    @FXML
    private TableColumn<DetalleVenta, Double> colProdSubtotal;

    @FXML
    private Label lblVentaSeleccionada;
    @FXML
    private Label lblTotalHistorico;

    private final VentaRepository ventaRepository = new VentaRepositoryImpl();
    private final ClienteRepository clienteRepository = new ClienteRepositoryImpl();
    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private final ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablas();
        cargarClientes();

        // Listener seguro con tipos explícitos
        tblVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarDetalleVenta(newVal);
            } else {
                listaDetalles.clear();
                lblVentaSeleccionada.setText("Selecciona una venta");
            }
        });

        // Cargar últimos 30 días por defecto
        dpInicio.setValue(LocalDate.now().minusDays(30));
        dpFin.setValue(LocalDate.now());
        onBuscarVentas(null);
    }

    private void configurarTablas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Tipado explícito en los lambdas de cellData para evitar que sea tratado como Object
        colCliente.setCellValueFactory(cellData -> {
            Venta venta = cellData.getValue();
            return new SimpleStringProperty(venta != null && venta.getCliente() != null ? venta.getCliente().getNombre() : "N/A");
        });

        colFecha.setCellValueFactory(cellData -> {
            Venta venta = cellData.getValue();
            return new SimpleStringProperty(venta != null && venta.getFecha() != null ? venta.getFecha().format(formatter) : "");
        });

        colTotal.setCellValueFactory(cellData -> {
            Venta venta = cellData.getValue();
            return new SimpleObjectProperty<>(venta != null ? venta.getTotal() : 0.0);
        });
        colTotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Formateo visual con tipado estricto
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

        tblVentas.setItems(listaVentas);

        // Tabla Detalle
        colProdNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProdCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colProdPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colProdSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        colProdCantidad.setStyle("-fx-alignment: CENTER;");
        colProdPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        colProdSubtotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        tblDetalleVenta.setItems(listaDetalles);
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

        List<Venta> resultados = ventaRepository.buscarPorRangoFechas(
                inicio != null ? inicio : LocalDate.now().minusYears(1),
                fin != null ? fin : LocalDate.now()
        );

        if (clienteId != null) {
            resultados = resultados.stream()
                    .filter(v -> v.getCliente() != null && v.getCliente().getId() == clienteId)
                    .collect(java.util.stream.Collectors.toList());
        }

        listaVentas.setAll(resultados);
        recalcularSumatoriaHistorica();
    }

    @FXML
    void onLimpiarFiltros(ActionEvent event) {
        dpInicio.setValue(LocalDate.now().minusDays(30));
        dpFin.setValue(LocalDate.now());
        cmbFiltroCliente.getSelectionModel().clearSelection();
        onBuscarVentas(null);
    }

    @FXML
    void onAnularVenta(ActionEvent event) {
        Venta ventaSeleccionada = tblVentas.getSelectionModel().getSelectedItem();

        if (ventaSeleccionada == null) {
            mostrarAlerta("Selección vacía", "Debes seleccionar una venta del historial para poder anularla.", Alert.AlertType.WARNING);
            return;
        }

        if ("CANCELADA".equalsIgnoreCase(ventaSeleccionada.getEstado())) {
            mostrarAlerta("Venta Ya Anulada", "La venta ID " + ventaSeleccionada.getId() + " ya fue cancelada anteriormente.", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Anulación");
        confirmacion.setHeaderText("¿Deseas anular la Venta #" + ventaSeleccionada.getId() + "?");
        confirmacion.setContentText("Esta acción cambiará el estado a CANCELADA y reintegrará el stock de los productos al inventario.");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            if (ventaRepository.cancelarVenta(ventaSeleccionada.getId())) {
                mostrarAlerta("Éxito", "La venta fue anulada correctamente y el stock ha sido devuelto.", Alert.AlertType.INFORMATION);
                onBuscarVentas(null);
            } else {
                mostrarAlerta("Error", "No se pudo anular la venta en la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    private void mostrarDetalleVenta(Venta venta) {
        lblVentaSeleccionada.setText("Ticket Venta #" + venta.getId() + " - Cliente: " + (venta.getCliente() != null ? venta.getCliente().getNombre() : "N/A"));
        List<DetalleVenta> detalles = ventaRepository.listarDetallesPorVenta(venta.getId());
        listaDetalles.setAll(detalles);
    }

    private void recalcularSumatoriaHistorica() {
        // Al estar listaVentas bien tipada con <Venta>, Venta::getTotal funciona correctamente
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
