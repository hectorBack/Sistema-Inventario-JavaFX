
package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.Impl.CategoriaRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class InventarioController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<Categoria> cmbCategoria;
    
    // Configuración de la Tabla y sus columnas
    @FXML private TableView<Producto> tblProductos;
    private TableColumn<Producto, Integer> colId;
    private TableColumn<Producto, String> colNombre;
    private TableColumn<Producto, Double> colPrecio;
    private TableColumn<Producto, Integer> colStock;
    private TableColumn<Producto, String> colEstado;
    private TableColumn<Producto, String> colCategoria;
    
    // Dependencias
    private final ProductoRepository repository = new ProductoRepositoryImpl();
    private final CategoriaRepository catRepository = new CategoriaRepositoryImpl();
    
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    
    // Variable para saber qué producto está seleccionado al actualizar/eliminar
    private Producto productoSeleccionado;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Inicializar el ComboBox con las opciones de Estado
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstado.setValue("Activo");
        
        cargarComboCategorias();

        // 2. Configurar las columnas del TableView de forma dinámica
        configurarColumnas();

        // 3. Cargar los datos desde PostgreSQL a la lista observable
        listarProductos();

        // 4. Escuchar los clics de la tabla para rellenar los campos de texto al seleccionar un producto
        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                txtNombre.setText(productoSeleccionado.getNombre());
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
                txtStock.setText(String.valueOf(productoSeleccionado.getStock()));
                cmbEstado.setValue(productoSeleccionado.getEstado());
                cmbCategoria.setValue(productoSeleccionado.getCategoria());
            }
        });
    }
    
    private void cargarComboCategorias() {
        listaCategorias.clear();
        // Filtrar opcionalmente para mostrar sólo las categorías con estado 'Activo'
        listaCategorias.addAll(catRepository.listarTodas()); 
        cmbCategoria.setItems(listaCategorias);
    }

    private void configurarColumnas() {
        // Creamos las columnas programáticamente para mantener un control absoluto del tipado
        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCategoria() != null) {
                return cellData.getValue().getCategoria().nombreProperty();
            }
            return new SimpleStringProperty("Sin Categoría");
        });

        // Añadimos las columnas configuradas al TableView limpiando las que Scene Builder trae por defecto
        tblProductos.getColumns().setAll(colId, colNombre, colCategoria, colPrecio, colStock, colEstado);
    }

    private void listarProductos() {
        listaProductos.clear();
        listaProductos.addAll(repository.listarTodos());
        tblProductos.setItems(listaProductos);
    } 
    
    @FXML
    void onAgregar(ActionEvent event) {
        if (validarCampos()) {
            Producto nuevoProducto = new Producto(
                    txtNombre.getText(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtStock.getText()),
                    cmbEstado.getValue(),
                    cmbCategoria.getValue() 
            );

            if (repository.guardar(nuevoProducto)) {
                mostrarAlerta("Éxito", "Producto agregado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarProductos();
            } else {
                mostrarAlerta("Error", "No se pudo guardar el producto", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un producto de la tabla para actualizar", Alert.AlertType.WARNING);
            return;
        }

        if (validarCampos()) {
            productoSeleccionado.setNombre(txtNombre.getText());
            productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText()));
            productoSeleccionado.setStock(Integer.parseInt(txtStock.getText()));
            productoSeleccionado.setEstado(cmbEstado.getValue());
            productoSeleccionado.setCategoria(cmbCategoria.getValue());

            if (repository.actualizar(productoSeleccionado)) {
                mostrarAlerta("Éxito", "Producto actualizado correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarProductos();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el producto", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un producto de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        if (repository.eliminar(productoSeleccionado.getId())) {
            mostrarAlerta("Éxito", "Producto eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            listarProductos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el producto", Alert.AlertType.ERROR);
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbEstado.setValue("Activo");
        cmbCategoria.setValue(null);
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
    }

   private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor rellena todos los campos obligatorios", Alert.AlertType.WARNING);
            return false;
        }
        
        // NUEVO: Validar que se haya seleccionado una categoría
        if (cmbCategoria.getValue() == null) {
            mostrarAlerta("Categoría requerida", "Por favor selecciona una categoría para el producto", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Double.parseDouble(txtPrecio.getText());
            Integer.parseInt(txtStock.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Datos inválidos", "Precio y Stock deben ser numéricos", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}
