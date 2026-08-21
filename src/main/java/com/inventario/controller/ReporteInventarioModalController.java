package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReporteInventarioModalController implements Initializable {

    @FXML
    private Label lblCostoTotal;
    @FXML
    private Label lblCantidadProductos;
    @FXML
    private Label lblValorVentaTotal;
    @FXML
    private ComboBox<Object> cmbCategoria; // Soporta String "Todas" y objetos Categoria

    @FXML
    private TableView<Producto> tblReporte;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colProducto;
    @FXML
    private TableColumn<Producto, Double> colCosto;
    @FXML
    private TableColumn<Producto, Double> colPrecioVenta;
    @FXML
    private TableColumn<Producto, Double> colStock;
    @FXML
    private TableColumn<Producto, Double> colStockMinimo;

    private ObservableList<Producto> masterData = FXCollections.observableArrayList();
    private FilteredList<Producto> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Costo (precioCompra)
        colCosto.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colCosto.setStyle("-fx-alignment: CENTER-RIGHT;");
        colCosto.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Precio Venta (precio)
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecioVenta.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPrecioVenta.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Stock actual
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setStyle("-fx-alignment: CENTER-RIGHT;");
        colStock.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item % 1 == 0 ? String.format("%.0f", item) : String.format("%.2f", item)));
            }
        });

        // Stock mínimo
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colStockMinimo.setStyle("-fx-alignment: CENTER-RIGHT;");
        colStockMinimo.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item % 1 == 0 ? String.format("%.0f", item) : String.format("%.2f", item)));
            }
        });
    }

    public void initData(List<Producto> productos, List<Categoria> categorias) {
        masterData.setAll(productos);
        filteredData = new FilteredList<>(masterData, p -> true);

        // Configurar ComboBox de Categorías
        cmbCategoria.getItems().clear();
        cmbCategoria.getItems().add("Todas");
        if (categorias != null) {
            cmbCategoria.getItems().addAll(categorias);
        }
        cmbCategoria.getSelectionModel().selectFirst();

        // Filtro por categoría
        cmbCategoria.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(producto -> {
                if (newVal == null || newVal.equals("Todas")) {
                    return true;
                }
                // Compatible con Java 11 (casting explícito)
                if (newVal instanceof Categoria) {
                    Categoria cat = (Categoria) newVal;
                    return producto.getCategoria() != null
                            && producto.getCategoria().getId() == cat.getId();
                }
                return true;
            });
            actualizarMetricas();
        });

        tblReporte.setItems(filteredData);
        actualizarMetricas();
    }

    private void actualizarMetricas() {
        double costoTotal = 0.0;
        double valorVentaTotal = 0.0;
        int totalProductos = filteredData.size();

        for (Producto p : filteredData) {
            costoTotal += (p.getPrecioCompra() * p.getStock());
            valorVentaTotal += (p.getPrecio() * p.getStock());
        }

        lblCostoTotal.setText(String.format("$%.2f", costoTotal));
        lblValorVentaTotal.setText(String.format("$%.2f", valorVentaTotal));
        lblCantidadProductos.setText(String.valueOf(totalProductos));
    }

    @FXML
    void onExportar(ActionEvent event) {
        // Lógica de exportación a Excel
    }

    @FXML
    void onImprimir(ActionEvent event) {
        // Lógica de impresión
    }

    @FXML
    void onCerrar(ActionEvent event) {
        Stage stage = (Stage) tblReporte.getScene().getWindow();
        stage.close();
    }
}
