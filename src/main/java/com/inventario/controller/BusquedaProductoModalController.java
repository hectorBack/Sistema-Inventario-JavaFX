package com.inventario.controller;

import com.inventario.model.Producto;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class BusquedaProductoModalController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Producto> tblProductos;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, Double> colStock;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancelar;

    private Producto productoSeleccionado = null;
    private ObservableList<Producto> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarEventosTeclado();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPrecio.setCellFactory(tc -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Clic doble para seleccionar rápidamente
        tblProductos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblProductos.getSelectionModel().getSelectedItem() != null) {
                onAceptar(null);
            }
        });
    }

    public void initData(List<Producto> productos) {
        masterData.setAll(productos);

        // Inicia con un predicado en false para que no muestre nada de inicio
        FilteredList<Producto> filteredData = new FilteredList<>(masterData, p -> false);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(producto -> {
                // Si el campo de búsqueda está vacío o en blanco, no muestra nada
                if (newValue == null || newValue.isBlank()) {
                    return false;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                // Busca coincidencia en el nombre o en el código de barras
                boolean coincideNombre = producto.getNombre() != null && producto.getNombre().toLowerCase().contains(lowerCaseFilter);
                boolean coincideCodigo = producto.getCodigoBarras() != null && producto.getCodigoBarras().toLowerCase().contains(lowerCaseFilter);

                return coincideNombre || coincideCodigo;
            });

            // Selecciona automáticamente el primer resultado si hay coincidencias
            if (!tblProductos.getItems().isEmpty()) {
                tblProductos.getSelectionModel().selectFirst();
            }
        });

        tblProductos.setItems(filteredData);

        Platform.runLater(() -> txtBuscar.requestFocus());
    }

    private void configurarEventosTeclado() {
        // Permitir navegar hacia abajo desde la caja de texto hasta la tabla
        txtBuscar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                tblProductos.requestFocus();
                event.consume();
            }
        });
    }

    public void asociarAtajosEscena(Stage stage) {
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                onCancelar(null);
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                onAceptar(null);
                event.consume();
            }
        });
    }

    @FXML
    void onAceptar(ActionEvent event) {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            this.productoSeleccionado = seleccionado;
            cerrarVentana();
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        this.productoSeleccionado = null;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtBuscar.getScene().getWindow();
        stage.close();
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

}
