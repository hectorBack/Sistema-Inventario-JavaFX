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
import com.inventario.util.Inventario.PaqueteModalDialog;
import com.inventario.util.audio.SoundUtil;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class InventarioController implements Initializable {

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
    private Button btnConfigurarPaquete;

    @FXML
    private TableView<Producto> tblProductos;
    private TableColumn<Producto, Integer> colId;
    private TableColumn<Producto, String> colCodigo, colNombre, colDescripcion, colTipoVenta, colEstado, colCategoria, colProveedor;
    private TableColumn<Producto, Double> colPrecio, colPrecioCompra, colPorcentajeGanancia, colPrecioMayoreo, colStock, colStockMin;

    private final ProductoRepository repository = new ProductoRepositoryImpl();
    private final CategoriaRepository catRepository = new CategoriaRepositoryImpl();
    private final ProveedorRepository provRepository = new ProveedorRepositoryImpl();

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private final ObservableList<DetallePaquete> listaDetallePaquete = FXCollections.observableArrayList();

    private Producto productoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstado.setValue("Activo");

        cmbTipoVenta.setItems(FXCollections.observableArrayList("UNIDAD", "GRANEL", "PAQUETE"));
        cmbTipoVenta.setValue("UNIDAD");

        cmbTipoVenta.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esPaquete = "PAQUETE".equalsIgnoreCase(newVal);
            pnlContenidoPaquete.setVisible(esPaquete);
            pnlContenidoPaquete.setManaged(esPaquete);
        });

        cargarComboCategorias();
        cargarComboProveedores();
        configurarColumnas();
        listarProductos();

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                InventarioUIUtil.cargarProductoEnFormulario(productoSeleccionado, txtCodigoBarras, txtNombre, txtDescripcion, txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo, txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbCategoria, cmbProveedor);

                listaDetallePaquete.clear();
                if ("PAQUETE".equalsIgnoreCase(productoSeleccionado.getTipoVenta())) {
                    List<DetallePaquete> detalles = repository.obtenerDetallesPaquete(productoSeleccionado.getId());
                    if (detalles != null) {
                        listaDetallePaquete.addAll(detalles);
                    }
                }
                actualizarTextoBotonPaquete();
            }
        });

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProductos(newVal));

        if (txtPrecioCompra != null) {
            txtPrecioCompra.textProperty().addListener((obs, oldVal, newVal) -> calcularPrecioVenta());
        }
        if (txtPorcentajeGanancia != null) {
            txtPorcentajeGanancia.textProperty().addListener((obs, oldVal, newVal) -> calcularPrecioVenta());
        }
    }

    @FXML
    private void onAbrirModalPaquete(ActionEvent event) {
        PaqueteModalDialog.mostrar(repository, listaDetallePaquete).ifPresent(nuevosDetalles -> {
            listaDetallePaquete.setAll(nuevosDetalles);
            actualizarTextoBotonPaquete();

            double costoTotal = InventarioCalculosUtil.calcularCostoTotalPaquete(listaDetallePaquete);
            if (txtPrecioCompra != null) {
                txtPrecioCompra.setText(String.format(Locale.US, "%.2f", costoTotal));
            }
            calcularPrecioVenta();
        });
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
            InventarioUIUtil.mostrarAlerta("Producto Encontrado", "El producto '" + encontrado.getNombre() + "' ya está registrado. Se cargaron sus datos.", Alert.AlertType.INFORMATION);
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

        // Validar que si es tipo PAQUETE contenga al menos un item
        if ("PAQUETE".equalsIgnoreCase(cmbTipoVenta.getValue()) && listaDetallePaquete.isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Paquete Vacío", "Debe configurar al menos un producto dentro del paquete.", Alert.AlertType.WARNING);
            return;
        }

        Producto nuevoProducto = InventarioUIUtil.extraerProductoDeFormulario(null, txtCodigoBarras, txtNombre, txtDescripcion, txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo, txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbCategoria, cmbProveedor);

        if (repository.guardar(nuevoProducto)) {
            // Persistir los items si es paquete
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
            // Reemplazar la lista de componentes en BD
            if ("PAQUETE".equalsIgnoreCase(productoSeleccionado.getTipoVenta())) {
                repository.reemplazarDetallesPaquete(productoSeleccionado.getId(), listaDetallePaquete);
            } else {
                // Si cambió de PAQUETE a UNIDAD/GRANEL, elimina los detalles anteriores
                repository.reemplazarDetallesPaquete(productoSeleccionado.getId(), null);
            }

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

        if (repository.eliminar(productoSeleccionado.getId())) {
            InventarioUIUtil.mostrarAlerta("Éxito", "Producto eliminado correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            listarProductos();
        } else {
            InventarioUIUtil.mostrarAlerta("Error", "No se pudo eliminar el producto", Alert.AlertType.ERROR);
        }
    }

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
        listaCategorias.addAll(catRepository.listarTodas());
        cmbCategoria.setItems(listaCategorias);
        cmbCategoria.setConverter(crearStringConverter(Categoria::getNombre));
    }

    private void cargarComboProveedores() {
        listaProveedores.clear();
        List<Proveedor> activos = provRepository.listarTodos().stream()
                .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList());
        listaProveedores.addAll(activos);
        cmbProveedor.setItems(listaProveedores);
        cmbProveedor.setConverter(crearStringConverter(Proveedor::getNombre));
    }

    private <T> StringConverter<T> crearStringConverter(java.util.function.Function<T, String> extractorNombre) {
        return new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : extractorNombre.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    private void configurarColumnas() {
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
        colPorcentajeGanancia = new TableColumn<>("% Gan.");
        colPorcentajeGanancia.setCellValueFactory(new PropertyValueFactory<>("porcentajeGanancia"));
        colPrecioMayoreo = new TableColumn<>("P. Mayoreo");
        colPrecioMayoreo.setCellValueFactory(new PropertyValueFactory<>("precioMayoreo"));
        colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStockMin = new TableColumn<>("Mín.");
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colTipoVenta = new TableColumn<>("Tipo Venta");
        colTipoVenta.setCellValueFactory(new PropertyValueFactory<>("tipoVenta"));
        colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(c -> c.getValue().getCategoria() != null ? c.getValue().getCategoria().nombreProperty() : new SimpleStringProperty("Sin Categoría"));

        colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(c -> c.getValue().getProveedor() != null ? c.getValue().getProveedor().nombreProperty() : new SimpleStringProperty("Sin Proveedor"));

        tblProductos.getColumns().setAll(colId, colCodigo, colNombre, colTipoVenta, colPrecio, colPrecioCompra, colPrecioMayoreo, colPorcentajeGanancia, colStock, colStockMin, colCategoria, colProveedor, colEstado);
    }

    private void listarProductos() {
        listaProductos.clear();
        listaProductos.addAll(repository.listarTodos());
        tblProductos.setItems(listaProductos);
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
        if (txtPorcentajeGanancia != null) {
            txtPorcentajeGanancia.clear();
        }
        if (txtPrecioMayoreo != null) {
            txtPrecioMayoreo.clear();
        }
        txtStock.clear();
        if (txtStockMinimo != null) {
            txtStockMinimo.clear();
        }

        cmbEstado.setValue("Activo");
        cmbTipoVenta.setValue("UNIDAD");
        cmbCategoria.setValue(null);
        cmbProveedor.setValue(null);
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
            InventarioUIUtil.mostrarAlerta("Categoría requerida", "Por favor selecciona una categoría para el producto", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbProveedor.getValue() == null) {
            InventarioUIUtil.mostrarAlerta("Proveedor requerido", "Por favor selecciona un proveedor para el producto", Alert.AlertType.WARNING);
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
            btnConfigurarPaquete.setText("🎁 Configurar Productos (" + listaDetallePaquete.size() + ")");
        }
    }
}
