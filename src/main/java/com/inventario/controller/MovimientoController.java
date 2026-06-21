package com.inventario.controller;

import com.inventario.model.MovimientoInventario;
import com.inventario.model.Producto;
import com.inventario.repository.Impl.MovimientoRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
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
    @FXML
    private TableView<MovimientoInventario> tblMovimientos;

    private final MovimientoRepository movRepository = new MovimientoRepositoryImpl();
    private final ProductoRepository prodRepository = new ProductoRepositoryImpl();
    private final ObservableList<MovimientoInventario> listaMovimientos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar opciones del tipo de movimiento
        cmbTipo.setItems(FXCollections.observableArrayList("ENTRADA", "SALIDA"));
        cmbTipo.setValue("ENTRADA");

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

        tblMovimientos.getColumns().setAll(colProducto, colTipo, colCantidad, colMotivo, colFecha);
    }

    private void listarMovimientos() {
        listaMovimientos.clear();
        listaMovimientos.addAll(movRepository.listarTodos());
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

            // Validación de stock si es una salida
            if (cmbTipo.getValue().equals("SALIDA") && productoSeleccionado.getStock() < cantidad) {
                mostrarAlerta("Stock insuficiente", "No puedes retirar más unidades de las disponibles (" + productoSeleccionado.getStock() + ").", Alert.AlertType.ERROR);
                return;
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
                listarMovimientos();
            } else {
                mostrarAlerta("Error", "Ocurrió un problema en la base de datos al asentar el movimiento.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato incorrecto", "La cantidad debe ser un número entero mayor a cero.", Alert.AlertType.WARNING);
        }
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
