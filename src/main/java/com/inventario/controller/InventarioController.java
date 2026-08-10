package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.Impl.CategoriaRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.Impl.ProveedorRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class InventarioController implements Initializable {

    @FXML
    private TextField txtCodigoBarras; // NUEVO
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtPrecioCompra;
    @FXML
    private TextField txtStock;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;

    // Configuración de la Tabla y sus columnas
    @FXML
    private TableView<Producto> tblProductos;
    private TableColumn<Producto, Integer> colId;
    private TableColumn<Producto, String> colCodigo;
    private TableColumn<Producto, String> colNombre;
    private TableColumn<Producto, String> colDescripcion;
    private TableColumn<Producto, Double> colPrecio;
    private TableColumn<Producto, Double> colPrecioCompra;
    private TableColumn<Producto, Integer> colStock;
    private TableColumn<Producto, Integer> colStockMin;
    private TableColumn<Producto, String> colEstado;
    private TableColumn<Producto, String> colCategoria;

    private TableColumn<Producto, String> colProveedor;

    // Dependencias
    private final ProductoRepository repository = new ProductoRepositoryImpl();
    private final CategoriaRepository catRepository = new CategoriaRepositoryImpl();
    private final ProveedorRepository provRepository = new ProveedorRepositoryImpl();

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();

    // Variable para saber qué producto está seleccionado al actualizar/eliminar
    private Producto productoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Inicializar el ComboBox con las opciones de Estado
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstado.setValue("Activo");

        cargarComboCategorias();
        cargarComboProveedores();

        // 2. Configurar las columnas del TableView de forma dinámica
        configurarColumnas();

        // 3. Cargar los datos desde PostgreSQL a la lista observable
        listarProductos();

        // 4. Escuchar los clics de la tabla para rellenar los campos de texto al seleccionar un producto
        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                txtCodigoBarras.setText(productoSeleccionado.getCodigoBarras() != null ? productoSeleccionado.getCodigoBarras() : "");
                txtNombre.setText(productoSeleccionado.getNombre());
                txtDescripcion.setText(productoSeleccionado.getDescripcion() != null ? productoSeleccionado.getDescripcion() : "");
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
                txtPrecioCompra.setText(String.valueOf(productoSeleccionado.getPrecioCompra()));
                txtStock.setText(String.valueOf(productoSeleccionado.getStock()));
                txtStockMinimo.setText(String.valueOf(productoSeleccionado.getStockMinimo()));
                cmbEstado.setValue(productoSeleccionado.getEstado());
                cmbCategoria.setValue(productoSeleccionado.getCategoria());
                cmbProveedor.setValue(productoSeleccionado.getProveedor());
            }
        });
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarProductos(newValue);
        });
    }

    /**
     * Emite un tono sintético vía AudioSystem sin requerir archivos .wav
     * externos.
     */
    private void emitirBeep(int hz, int msecs) {
        new Thread(() -> {
            try {
                byte[] buf = new byte[1];
                AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
                for (int i = 0; i < msecs * 8; i++) {
                    double angle = i / (8000f / hz) * 2.0 * Math.PI;
                    buf[0] = (byte) (Math.sin(angle) * 100);
                    sdl.write(buf, 0, 1);
                }
                sdl.drain();
                sdl.stop();
                sdl.close();
            } catch (Exception ignored) {
            }
        }).start();
    }

    /**
     * Evento disparado al escanear sobre el campo txtCodigoBarras con la
     * pistola. Si el producto ya existe, carga sus datos para editarlo o
     * consultar.
     */
    @FXML
    void onEscanearCodigoFormulario(ActionEvent event) {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }

        Producto encontrado = repository.buscarPorCodigoBarras(codigo);
        if (encontrado != null) {
            emitirBeep(900, 120); // Beep agudo para éxito
            tblProductos.getSelectionModel().select(encontrado);
            mostrarAlerta("Producto Encontrado", "El producto '" + encontrado.getNombre() + "' ya está registrado. Se cargaron sus datos para edición.", Alert.AlertType.INFORMATION);
        } else {
            emitirBeep(450, 180); // Beep más grave para código nuevo / no registrado
            txtNombre.requestFocus();
        }
    }

    private void buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listarProductos();
            return;
        }

        String termino = criterio.trim();
        listaProductos.clear();

        // 1. Intentar buscar coincidencia exacta por código de barras primero
        Producto pCodigo = repository.buscarPorCodigoBarras(termino);
        if (pCodigo != null) {
            listaProductos.add(pCodigo);
        } else {
            // 2. Si no coincide el código, buscar por nombre (coincidencia parcial)
            listaProductos.addAll(repository.buscarPorNombre(termino));
        }

        tblProductos.setItems(listaProductos);
    }

    private void cargarComboCategorias() {
        listaCategorias.clear();
        listaCategorias.addAll(catRepository.listarTodas());
        cmbCategoria.setItems(listaCategorias);

        // Convertidor para mostrar correctamente el nombre de la categoría en el ComboBox sin romper estilos CSS
        cmbCategoria.setConverter(new javafx.util.StringConverter<Categoria>() {
            @Override
            public String toString(Categoria cat) {
                return cat == null ? "" : cat.getNombre();
            }

            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });
    }

    private void cargarComboProveedores() {
        listaProveedores.clear();
        java.util.List<Proveedor> activos = provRepository.listarTodos().stream()
                .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList());
        listaProveedores.addAll(activos);
        cmbProveedor.setItems(listaProveedores);

        // Convertidor estándar usando StringConverter en lugar de ListCell anónima
        cmbProveedor.setConverter(new javafx.util.StringConverter<Proveedor>() {
            @Override
            public String toString(Proveedor prov) {
                return prov == null ? "" : prov.getNombre();
            }

            @Override
            public Proveedor fromString(String string) {
                return null;
            }
        });
    }

    private void configurarColumnas() {
        // Creamos las columnas programáticamente para mantener un control absoluto del tipado
        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));

        colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colPrecio = new TableColumn<>("P. Venta");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colPrecioCompra = new TableColumn<>("P. Costo");
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));

        colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colStockMin = new TableColumn<>("Mín.");
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCategoria() != null) {
                return cellData.getValue().getCategoria().nombreProperty();
            }
            return new SimpleStringProperty("Sin Categoría");
        });

        colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProveedor() != null) {
                return cellData.getValue().getProveedor().nombreProperty();
            }
            return new SimpleStringProperty("Sin Proveedor");
        });

        // Añadimos las columnas configuradas al TableView limpiando las que Scene Builder trae por defecto
        tblProductos.getColumns().setAll(
                colId, colCodigo, colNombre, colDescripcion, colCategoria,
                colProveedor, colPrecio, colPrecioCompra, colStock, colStockMin, colEstado
        );
    }

    private void listarProductos() {
        listaProductos.clear();
        listaProductos.addAll(repository.listarTodos());
        tblProductos.setItems(listaProductos);
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (validarCampos()) {
            String codigo = txtCodigoBarras.getText() != null ? txtCodigoBarras.getText().trim() : "";

            // Validar que el código de barras no exista previamente en otro producto
            if (!codigo.isEmpty() && repository.existeCodigoBarras(codigo, 0)) {
                mostrarAlerta("Código Duplicado", "Ya existe un producto registrado con el código de barras: " + codigo, Alert.AlertType.WARNING);
                return;
            }

            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";// Reservado para observaciones
            double precioVenta = Double.parseDouble(txtPrecio.getText().trim());
            double precioCompra = txtPrecioCompra.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtPrecioCompra.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            int stockMin = txtStockMinimo.getText().trim().isEmpty() ? 5 : Integer.parseInt(txtStockMinimo.getText().trim());
            String estado = cmbEstado.getValue() != null ? cmbEstado.getValue() : "ACTIVO";

            // CONSTRUCTOR CORREGIDO DE 10 PARÁMETROS
            Producto nuevoProducto = new Producto(
                    codigo,
                    nombre,
                    descripcion,
                    precioVenta,
                    precioCompra,
                    stock,
                    stockMin,
                    estado,
                    cmbCategoria.getValue(),
                    cmbProveedor.getValue()
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
            String codigo = txtCodigoBarras.getText() != null ? txtCodigoBarras.getText().trim() : "";

            // Validar que el nuevo código de barras no le pertenezca a OTRO producto distinto al actual
            if (!codigo.isEmpty() && repository.existeCodigoBarras(codigo, productoSeleccionado.getId())) {
                mostrarAlerta("Código Duplicado", "El código de barras '" + codigo + "' ya le pertenece a otro producto registrado.", Alert.AlertType.WARNING);
                return;
            }

            productoSeleccionado.setCodigoBarras(codigo);
            productoSeleccionado.setNombre(txtNombre.getText().trim());
            productoSeleccionado.setDescripcion(txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "");
            productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            productoSeleccionado.setPrecioCompra(txtPrecioCompra.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtPrecioCompra.getText().trim()));
            productoSeleccionado.setStock(Integer.parseInt(txtStock.getText().trim()));
            productoSeleccionado.setStockMinimo(txtStockMinimo.getText().trim().isEmpty() ? 5 : Integer.parseInt(txtStockMinimo.getText().trim()));
            productoSeleccionado.setEstado(cmbEstado.getValue());
            productoSeleccionado.setCategoria(cmbCategoria.getValue());
            productoSeleccionado.setProveedor(cmbProveedor.getValue());

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
        if (txtCodigoBarras != null) {
            txtCodigoBarras.clear();
        }
        txtNombre.clear();
        if (txtDescripcion != null) {
            txtDescripcion.clear();
        }
        txtPrecio.clear();
        if (txtPrecioCompra != null) {
            txtPrecioCompra.clear();
        }
        txtStock.clear();
        if (txtStockMinimo != null) {
            txtStockMinimo.clear();
        }
        cmbEstado.setValue("ACTIVO");
        cmbCategoria.setValue(null);
        cmbProveedor.setValue(null);
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor rellena los campos obligatorios (Nombre, Precio Venta y Stock)", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbCategoria.getValue() == null) {
            mostrarAlerta("Categoría requerida", "Por favor selecciona una categoría para el producto", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbProveedor.getValue() == null) {
            mostrarAlerta("Proveedor requerido", "Por favor selecciona un proveedor para el producto", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Double.parseDouble(txtPrecio.getText().trim());
            Integer.parseInt(txtStock.getText().trim());

            if (txtPrecioCompra != null && !txtPrecioCompra.getText().trim().isEmpty()) {
                Double.parseDouble(txtPrecioCompra.getText().trim());
            }
            if (txtStockMinimo != null && !txtStockMinimo.getText().trim().isEmpty()) {
                Integer.parseInt(txtStockMinimo.getText().trim());
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Datos inválidos", "Precio y Stock deben ser números válidos", Alert.AlertType.ERROR);
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
