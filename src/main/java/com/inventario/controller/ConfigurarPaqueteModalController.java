package com.inventario.controller;

import com.inventario.model.DetallePaquete;
import com.inventario.model.Producto;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfigurarPaqueteModalController {

    @FXML
    private TextField txtCodigoProducto;
    @FXML
    private Label lblNombreProducto;
    @FXML
    private Label lblPrecioCosto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TableView<DetallePaquete> tblDetalles;
    @FXML
    private TableColumn<DetallePaquete, String> colProducto;
    @FXML
    private TableColumn<DetallePaquete, String> colCantidad;

    private final ObservableList<DetallePaquete> listaDetalles = FXCollections.observableArrayList();
    private final ProductoRepository repository;

    public ConfigurarPaqueteModalController() {
        this(new ProductoRepositoryImpl());
    }

    public ConfigurarPaqueteModalController(ProductoRepository repository) {
        this.repository = repository;
    }

    private Producto productoEncontrado;
    private boolean guardado = false;

    @FXML
    public void initialize() {
        // Usamos getNombreProducto() directo del modelo o getProducto().getNombre()
        colProducto.setCellValueFactory(cell
                -> new SimpleStringProperty(cell.getValue().getProducto() != null
                        ? cell.getValue().getProducto().getNombre() : ""));

        colCantidad.setCellValueFactory(cell
                -> new SimpleStringProperty(String.valueOf(cell.getValue().getCantidad())));

        tblDetalles.setItems(listaDetalles);
    }

    public void setDetallesExistentes(List<DetallePaquete> detalles) {
        if (detalles != null) {
            listaDetalles.addAll(detalles);
        }
    }

    @FXML
    private void onBuscarProducto() {
        String codigo = txtCodigoProducto.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }

        productoEncontrado = DTOMapper.toModel(repository.buscarPorCodigoBarrasDTO(codigo));
        if (productoEncontrado != null) {
            lblNombreProducto.setText("Producto: " + productoEncontrado.getNombre());
            lblPrecioCosto.setText(String.format("Precio Costo: $%.2f", productoEncontrado.getPrecioCompra()));
            txtCantidad.requestFocus();
        } else {
            lblNombreProducto.setText("Producto: No encontrado");
            lblPrecioCosto.setText("Precio Costo: $0.00");
        }
    }

    @FXML
    private void onAgregar() {
        if (productoEncontrado == null) {
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                return;
            }

            // Usamos el constructor parametrizado existente
            DetallePaquete detalle = new DetallePaquete(productoEncontrado, cantidad);

            listaDetalles.add(detalle);
            limpiarCamposBusqueda();
        } catch (NumberFormatException e) {
            // Manejo de error en formato de cantidad
        }
    }

    @FXML
    private void onRemover() {
        DetallePaquete seleccionado = tblDetalles.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaDetalles.remove(seleccionado);
        }
    }

    @FXML
    private void onGuardar() {
        this.guardado = true;
        cerrarVentana();
    }

    @FXML
    private void onCancelar() {
        this.guardado = false;
        cerrarVentana();
    }

    private void limpiarCamposBusqueda() {
        txtCodigoProducto.clear();
        lblNombreProducto.setText("Producto: -");
        lblPrecioCosto.setText("Precio Costo: $0.00");
        txtCantidad.setText("1");
        productoEncontrado = null;
        txtCodigoProducto.requestFocus();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) tblDetalles.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public List<DetallePaquete> getListaDetalles() {
        return listaDetalles;
    }
}
