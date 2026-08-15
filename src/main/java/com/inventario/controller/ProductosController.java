package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.model.DetallePaquete;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.Impl.CategoriaRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.Impl.ProveedorRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import com.inventario.util.Inventario.InventarioCalculosUtil;
import com.inventario.util.Inventario.InventarioUIUtil;
import com.inventario.util.Productos.KeyboardShortcutUtil;
import com.inventario.util.Productos.ProductosTableUtil;
import com.inventario.util.audio.SoundUtil;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ProductosController implements Initializable {

    @FXML
    private TextField txtCodigoBarras, txtNombre, txtBuscar, txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo, txtStock, txtStockMinimo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private ComboBox<String> cmbEstado, cmbTipoVenta;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;

    @FXML
    private VBox pnlContenidoPaquete;
    @FXML
    private Button btnAgregar, btnActualizar, btnEliminar, btnConfigurarPaquete;

    @FXML
    private TableView<Producto> tblProductos;

    private final ProductoRepository repository;
    private final CategoriaRepository catRepository;
    private final ProveedorRepository provRepository;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private final ObservableList<DetallePaquete> listaDetallePaquete = FXCollections.observableArrayList();

    private Producto productoSeleccionado;

    // Constructor para inyección de dependencias (o constructor por defecto según tu setup)
    public ProductosController() {
        this(new ProductoRepositoryImpl(), new CategoriaRepositoryImpl(), new ProveedorRepositoryImpl());
    }

    public ProductosController(ProductoRepository repository, CategoriaRepository catRepository, ProveedorRepository provRepository) {
        this.repository = repository;
        this.catRepository = catRepository;
        this.provRepository = provRepository;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarCombos();
        configurarListeners();
        configurarTabla();
        cargarDatos();
        configurarAtajosTeclado();
    }

    private void inicializarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        cmbTipoVenta.setItems(FXCollections.observableArrayList("UNIDAD", "GRANEL", "PAQUETE"));
        cmbTipoVenta.setValue("UNIDAD");
    }

    private void configurarListeners() {
        listaDetallePaquete.addListener((ListChangeListener<DetallePaquete>) change -> actualizarTextoBotonPaquete());

        cmbTipoVenta.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esPaquete = "PAQUETE".equalsIgnoreCase(newVal);
            if (pnlContenidoPaquete != null) {
                pnlContenidoPaquete.setVisible(esPaquete);
                pnlContenidoPaquete.setManaged(esPaquete);
            }
            if (!esPaquete && !listaDetallePaquete.isEmpty()) {
                listaDetallePaquete.clear();
            }
            actualizarTextoBotonPaquete();
        });

        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProductos(newVal));
        }

        if (txtPrecioCompra != null) {
            txtPrecioCompra.textProperty().addListener((obs, oldVal, newVal) -> calcularPrecioVenta());
        }
        if (txtPorcentajeGanancia != null) {
            txtPorcentajeGanancia.textProperty().addListener((obs, oldVal, newVal) -> calcularPrecioVenta());
        }
    }

    private void configurarTabla() {
        ProductosTableUtil.configurarColumnasProductos(tblProductos);
        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> onSeleccionarProducto(newSel));
    }

    private void onSeleccionarProducto(Producto newSelection) {
        boolean productoCargado = (newSelection != null);

        if (btnActualizar != null) {
            btnActualizar.setDisable(!productoCargado);
        }
        if (btnEliminar != null) {
            btnEliminar.setDisable(!productoCargado);
        }
        if (btnAgregar != null) {
            btnAgregar.setDisable(productoCargado);
        }

        listaDetallePaquete.clear();

        if (productoCargado) {
            productoSeleccionado = newSelection;

            if ("PAQUETE".equalsIgnoreCase(productoSeleccionado.getTipoVenta())) {
                List<DetallePaquete> detalles = repository.obtenerDetallesPaquete(productoSeleccionado.getId());
                if (detalles != null) {
                    listaDetallePaquete.addAll(detalles);
                }
            }

            InventarioUIUtil.cargarProductoEnFormulario(
                    productoSeleccionado, txtCodigoBarras, txtNombre, txtDescripcion,
                    txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo,
                    txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbCategoria, cmbProveedor
            );
        } else {
            productoSeleccionado = null;
        }

        actualizarTextoBotonPaquete();
    }

    private void cargarDatos() {
        cargarComboCategorias();
        cargarComboProveedores();
        listarProductos();
    }

    private void configurarAtajosTeclado() {
        KeyboardShortcutUtil.registrarAtajosCrud(
                tblProductos,
                () -> {
                    limpiarFormulario();
                    if (txtCodigoBarras != null) {
                        txtCodigoBarras.requestFocus();
                    } else {
                        txtNombre.requestFocus();
                    }
                },
                () -> onAgregar(null),
                () -> onActualizar(null),
                () -> onEliminar(null),
                () -> {
                    if (txtBuscar != null) {
                        txtBuscar.requestFocus();
                        txtBuscar.selectAll();
                    }
                },
                this::limpiarFormulario
        );
    }

    // --- ACCIONES FXML ---
    @FXML
    private void onAbrirModalPaquete(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventario/view/ConfigurarPaqueteModalView.fxml"));
            Parent root = loader.load();

            ConfigurarPaqueteModalController modalController = loader.getController();
            modalController.setDetallesExistentes(listaDetallePaquete);

            Stage stage = new Stage();
            stage.setTitle("Configurar Componentes del Paquete");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (modalController.isGuardado()) {
                listaDetallePaquete.clear();
                listaDetallePaquete.addAll(modalController.getListaDetalles());
                actualizarTextoBotonPaquete();

                double costoTotal = listaDetallePaquete.stream().mapToDouble(DetallePaquete::getSubtotalCosto).sum();

                if (txtPrecioCompra != null) {
                    txtPrecioCompra.setText(String.format(Locale.US, "%.2f", costoTotal));
                }
                calcularPrecioVenta();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEscanearCodigoFormulario(ActionEvent event) {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }

        Producto encontrado = repository.buscarPorCodigoBarras(codigo);
        if (encontrado != null) {
            SoundUtil.emitirBeep(900, 120);
            tblProductos.getSelectionModel().select(encontrado);
            InventarioUIUtil.mostrarAlerta("Producto Encontrado", "El producto '" + encontrado.getNombre() + "' ya está registrado.", Alert.AlertType.INFORMATION);
        } else {
            SoundUtil.emitirBeep(450, 180);
            txtNombre.requestFocus();
        }
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String codigo = txtCodigoBarras.getText() != null ? txtCodigoBarras.getText().trim() : "";
        if (!codigo.isEmpty() && repository.existeCodigoBarras(codigo, 0)) {
            InventarioUIUtil.mostrarAlerta("Código Duplicado", "Ya existe un producto registrado con el código: " + codigo, Alert.AlertType.WARNING);
            return;
        }

        if ("PAQUETE".equalsIgnoreCase(cmbTipoVenta.getValue()) && listaDetallePaquete.isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Paquete Vacío", "Debe configurar al menos un producto dentro del paquete.", Alert.AlertType.WARNING);
            return;
        }

        Producto nuevoProducto = InventarioUIUtil.extraerProductoDeFormulario(null, txtCodigoBarras, txtNombre, txtDescripcion, txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo, txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbCategoria, cmbProveedor);

        if (repository.guardar(nuevoProducto)) {
            if ("PAQUETE".equalsIgnoreCase(nuevoProducto.getTipoVenta())) {
                repository.guardarDetallesPaquete(nuevoProducto.getId(), listaDetallePaquete);
            }
            InventarioUIUtil.mostrarAlerta("Éxito", "Producto agregado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            listarProductos();
        } else {
            InventarioUIUtil.mostrarAlerta("Error", "No se pudo guardar el producto", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (productoSeleccionado == null) {
            InventarioUIUtil.mostrarAlerta("Advertencia", "Selecciona un producto de la tabla para actualizar", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        String codigo = txtCodigoBarras.getText() != null ? txtCodigoBarras.getText().trim() : "";
        if (!codigo.isEmpty() && repository.existeCodigoBarras(codigo, productoSeleccionado.getId())) {
            InventarioUIUtil.mostrarAlerta("Código Duplicado", "El código de barras '" + codigo + "' ya pertenece a otro producto.", Alert.AlertType.WARNING);
            return;
        }

        if ("PAQUETE".equalsIgnoreCase(cmbTipoVenta.getValue()) && listaDetallePaquete.isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Paquete Vacío", "Debe configurar al menos un producto dentro del paquete.", Alert.AlertType.WARNING);
            return;
        }

        InventarioUIUtil.extraerProductoDeFormulario(productoSeleccionado, txtCodigoBarras, txtNombre, txtDescripcion, txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo, txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbCategoria, cmbProveedor);

        if (repository.actualizar(productoSeleccionado)) {
            List<DetallePaquete> detallesAActualizar = "PAQUETE".equalsIgnoreCase(productoSeleccionado.getTipoVenta()) ? listaDetallePaquete : null;
            repository.reemplazarDetallesPaquete(productoSeleccionado.getId(), detallesAActualizar);

            InventarioUIUtil.mostrarAlerta("Éxito", "Producto actualizado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            listarProductos();
        } else {
            InventarioUIUtil.mostrarAlerta("Error", "No se pudo actualizar el producto", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (productoSeleccionado == null) {
            InventarioUIUtil.mostrarAlerta("Advertencia", "Selecciona un producto de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        boolean tieneHistorial = repository.tieneAsociaciones(productoSeleccionado.getId());

        if (tieneHistorial) {
            boolean confirmar = InventarioUIUtil.mostrarConfirmacion(
                    "Producto con historial",
                    "Este producto tiene ventas o movimientos asociados.",
                    "No se puede eliminar de forma definitiva para mantener el historial. ¿Deseas deshabilitarlo/cambiar su estado a INACTIVO?"
            );

            if (confirmar && repository.eliminarLogico(productoSeleccionado.getId())) {
                InventarioUIUtil.mostrarAlerta("Éxito", "El producto ha sido deshabilitado correctamente (Estado: Inactivo).", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarProductos();
            }
        } else {
            boolean confirmar = InventarioUIUtil.mostrarConfirmacion(
                    "Confirmar eliminación",
                    "¿Eliminar producto definitivamente?",
                    "El producto '" + productoSeleccionado.getNombre() + "' no tiene historial registrado y se eliminará permanentemente."
            );

            if (confirmar && repository.eliminar(productoSeleccionado.getId())) {
                InventarioUIUtil.mostrarAlerta("Éxito", "Producto eliminado correctamente de la base de datos.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarProductos();
            }
        }
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---
    private void calcularPrecioVenta() {
        try {
            double costo = Double.parseDouble(txtPrecioCompra.getText().trim());
            double porcentaje = Double.parseDouble(txtPorcentajeGanancia.getText().trim());
            double precioVenta = InventarioCalculosUtil.calcularPrecioVenta(costo, porcentaje);
            txtPrecio.setText(String.format(Locale.US, "%.2f", precioVenta));
        } catch (NumberFormatException ignored) {
        }
    }

    private void buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listarProductos();
            return;
        }
        String termino = criterio.trim();
        listaProductos.clear();

        Producto pCodigo = repository.buscarPorCodigoBarras(termino);
        if (pCodigo != null) {
            listaProductos.add(pCodigo);
        } else {
            listaProductos.addAll(repository.buscarPorNombre(termino));
        }
        tblProductos.setItems(listaProductos);
    }

    private void cargarComboCategorias() {
        listaCategorias.clear();
        listaCategorias.addAll(catRepository.listarActivas());
        cmbCategoria.setItems(listaCategorias);
        cmbCategoria.setConverter(ProductosTableUtil.crearStringConverter(Categoria::getNombre));
    }

    private void cargarComboProveedores() {
        listaProveedores.clear();
        listaProveedores.addAll(provRepository.listarActivos());
        cmbProveedor.setItems(listaProveedores);
        cmbProveedor.setConverter(ProductosTableUtil.crearStringConverter(Proveedor::getNombre));
    }

    private void listarProductos() {
        listaProductos.clear();
        listaProductos.addAll(repository.listarTodos());
        tblProductos.setItems(listaProductos);
    }

    private void limpiarFormulario() {
        // Campos que se deben vaciar por completo
        if (txtCodigoBarras != null) {
            txtCodigoBarras.clear();
        }
        txtNombre.clear();
        if (txtDescripcion != null) {
            txtDescripcion.clear();
        }

        // Valores numéricos por defecto (coincidentes con la interfaz)
        if (txtPrecioCompra != null) {
            txtPrecioCompra.setText("0.00");
        }
        if (txtPorcentajeGanancia != null) {
            txtPorcentajeGanancia.setText("20.00");
        }
        txtPrecio.setText("0.00");
        if (txtPrecioMayoreo != null) {
            txtPrecioMayoreo.setText("0.00");
        }
        txtStock.setText("0");
        if (txtStockMinimo != null) {
            txtStockMinimo.setText("5");
        }

        // ComboBoxes a sus estados iniciales
        cmbEstado.setValue("ACTIVO");
        cmbTipoVenta.setValue("UNIDAD");
        cmbCategoria.setValue(null);
        cmbProveedor.setValue(null);

        // Reseteo de selección y detalles de paquete
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();

        listaDetallePaquete.clear();
        actualizarTextoBotonPaquete();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Campos vacíos", "Por favor rellena los campos obligatorios (Nombre, Precio Venta y Stock)", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbCategoria.getValue() == null) {
            InventarioUIUtil.mostrarAlerta("Categoría requerida", "Por favor selecciona una categoría", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbProveedor.getValue() == null) {
            InventarioUIUtil.mostrarAlerta("Proveedor requerido", "Por favor selecciona un proveedor", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Double.parseDouble(txtPrecio.getText().trim());
            Double.parseDouble(txtStock.getText().trim());
            if (txtPrecioCompra != null && !txtPrecioCompra.getText().trim().isEmpty()) {
                Double.parseDouble(txtPrecioCompra.getText().trim());
            }
            if (txtPorcentajeGanancia != null && !txtPorcentajeGanancia.getText().trim().isEmpty()) {
                Double.parseDouble(txtPorcentajeGanancia.getText().trim());
            }
            if (txtPrecioMayoreo != null && !txtPrecioMayoreo.getText().trim().isEmpty()) {
                Double.parseDouble(txtPrecioMayoreo.getText().trim());
            }
            if (txtStockMinimo != null && !txtStockMinimo.getText().trim().isEmpty()) {
                Double.parseDouble(txtStockMinimo.getText().trim());
            }
        } catch (NumberFormatException e) {
            InventarioUIUtil.mostrarAlerta("Datos inválidos", "Los campos numéricos deben contener valores válidos", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void actualizarTextoBotonPaquete() {
        if (btnConfigurarPaquete != null) {
            btnConfigurarPaquete.setText("Configurar Productos (" + listaDetallePaquete.size() + ")");
        }
    }
}
