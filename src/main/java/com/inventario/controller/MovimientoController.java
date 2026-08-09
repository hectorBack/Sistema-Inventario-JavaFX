package com.inventario.controller;

import com.inventario.model.MovimientoInventario;
import com.inventario.model.Producto;
import com.inventario.repository.Impl.MovimientoRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MovimientoController implements Initializable {

    @FXML
    private ComboBox<Producto> cmbProducto;
    @FXML
    private ComboBox<String> cmbTipo;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtMotivo;

    // Controles de búsqueda y filtrado
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbFiltroTipo;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private TableView<MovimientoInventario> tblMovimientos;

    private final MovimientoRepository movRepository = new MovimientoRepositoryImpl();
    private final ProductoRepository prodRepository = new ProductoRepositoryImpl();
    private final ObservableList<MovimientoInventario> listaMovimientos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar opciones del tipo de movimiento para el formulario
        cmbTipo.setItems(FXCollections.observableArrayList("ENTRADA", "SALIDA"));
        cmbTipo.setValue("ENTRADA");

        // Inicializar opciones del combo de filtrado por tipo
        if (cmbFiltroTipo != null) {
            cmbFiltroTipo.setItems(FXCollections.observableArrayList("TODOS", "ENTRADA", "SALIDA"));
            cmbFiltroTipo.setValue("TODOS");
            cmbFiltroTipo.setOnAction(e -> aplicarFiltros());
        }

        // Listeners para filtros dinámicos
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        }
        if (dpFechaInicio != null) {
            dpFechaInicio.setOnAction(e -> aplicarFiltros());
        }
        if (dpFechaFin != null) {
            dpFechaFin.setOnAction(e -> aplicarFiltros());
        }

        cargarProductosEnCombo();
        configurarColumnas();
        listarMovimientos();
    }

    private void cargarProductosEnCombo() {
        List<Producto> productos = prodRepository.listarTodos();
        cmbProducto.setItems(FXCollections.observableArrayList(productos));

        // Formatear el ComboBox para que muestre el nombre del producto de forma limpia
        cmbProducto.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cmbProducto.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private void configurarColumnas() {
        TableColumn<MovimientoInventario, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        TableColumn<MovimientoInventario, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));

        TableColumn<MovimientoInventario, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<MovimientoInventario, String> colMotivo = new TableColumn<>("Motivo / Razón");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        TableColumn<MovimientoInventario, LocalDateTime> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaMovimiento"));

        // Formato amigable para la fecha en la tabla
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        colFecha.setCellFactory(column -> new TableCell<MovimientoInventario, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        tblMovimientos.getColumns().setAll(colProducto, colTipo, colCantidad, colMotivo, colFecha);
    }

    private void listarMovimientos() {
        listaMovimientos.clear();
        listaMovimientos.addAll(movRepository.listarTodos());
        tblMovimientos.setItems(listaMovimientos);
    }

    private void aplicarFiltros() {
        String termino = (txtBuscar != null) ? txtBuscar.getText() : null;
        String tipo = (cmbFiltroTipo != null) ? cmbFiltroTipo.getValue() : "TODOS";
        LocalDate inicio = (dpFechaInicio != null) ? dpFechaInicio.getValue() : null;
        LocalDate fin = (dpFechaFin != null) ? dpFechaFin.getValue() : null;

        listaMovimientos.clear();
        listaMovimientos.addAll(movRepository.buscarConFiltros(termino, tipo, inicio, fin));
        tblMovimientos.setItems(listaMovimientos);
    }

    @FXML
    void onRegistrar(ActionEvent event) {
        Producto productoSeleccionado = cmbProducto.getValue();
        String cantidadStr = txtCantidad.getText().trim();

        if (productoSeleccionado == null || cantidadStr.isEmpty()) {
            mostrarAlerta("Campos faltantes", "Selecciona un producto e introduce una cantidad válida.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            // Validación de stock consultando directamente a la BD
            if ("SALIDA".equalsIgnoreCase(cmbTipo.getValue())) {
                int stockRealBD = movRepository.obtenerStockActual(productoSeleccionado.getId());
                if (stockRealBD < cantidad) {
                    mostrarAlerta("Stock insuficiente",
                            "No puedes retirar " + cantidad + " unidades. El stock real disponible en la base de datos es: " + stockRealBD + ".",
                            Alert.AlertType.ERROR);
                    return;
                }
            }

            MovimientoInventario nuevoMovimiento = new MovimientoInventario(
                    productoSeleccionado.getId(),
                    cmbTipo.getValue(),
                    cantidad,
                    txtMotivo.getText().trim().isEmpty() ? "Ajuste manual de inventario" : txtMotivo.getText().trim()
            );

            if (movRepository.registrarMovimiento(nuevoMovimiento)) {
                mostrarAlerta("Éxito", "Movimiento procesado y stock actualizado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                // Recargar productos en el ComboBox para sincronizar los valores de stock localmente
                cargarProductosEnCombo();
                aplicarFiltros();
            } else {
                mostrarAlerta("Error", "Ocurrió un problema en la base de datos al asentar el movimiento.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "La cantidad debe ser un número entero mayor a cero.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void onLimpiarFiltros(ActionEvent event) {
        if (txtBuscar != null) {
            txtBuscar.clear();
        }
        if (cmbFiltroTipo != null) {
            cmbFiltroTipo.setValue("TODOS");
        }
        if (dpFechaInicio != null) {
            dpFechaInicio.setValue(null);
        }
        if (dpFechaFin != null) {
            dpFechaFin.setValue(null);
        }
        listarMovimientos();
    }

    private void limpiarCampos() {
        cmbProducto.setValue(null);
        txtCantidad.clear();
        txtMotivo.clear();
        cmbTipo.setValue("ENTRADA");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
