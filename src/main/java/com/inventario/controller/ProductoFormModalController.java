package com.inventario.controller;

import com.inventario.config.ConfiguracionSistema;
import com.inventario.model.Categoria;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.model.DetallePaquete;
import com.inventario.model.OpcionesHabilitadas;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.model.UnidadMedida;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.Impl.CategoriaRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.Impl.ProveedorRepositoryImpl;
import com.inventario.repository.Impl.UnidadMedidaRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import com.inventario.repository.UnidadMedidaRepository;
import com.inventario.util.Inventario.InventarioCalculosUtil;
import com.inventario.util.Inventario.InventarioUIUtil;
import com.inventario.util.Productos.ProductosTableUtil;
import com.inventario.util.audio.SoundUtil;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductoFormModalController implements Initializable {

    @FXML
    private Label lblTituloModal;
    @FXML
    private TextField txtCodigoBarras, txtNombre, txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo, txtStock, txtStockMinimo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private ComboBox<String> cmbEstado, cmbTipoVenta;
    @FXML
    private ComboBox<UnidadMedida> cmbUnidadMedida;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;

    @FXML
    private VBox pnlContenidoPaquete;
    @FXML
    private Button btnConfigurarPaquete;
    @FXML
    private HBox contenedorStock;

    private final ProductoRepository repository = new ProductoRepositoryImpl();
    private final CategoriaRepository catRepository = new CategoriaRepositoryImpl();
    private final ProveedorRepository provRepository = new ProveedorRepositoryImpl();
    private final UnidadMedidaRepository unidadRepository = new UnidadMedidaRepositoryImpl();

    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private final ObservableList<UnidadMedida> listaUnidades = FXCollections.observableArrayList();
    private final ObservableList<DetallePaquete> listaDetallePaquete = FXCollections.observableArrayList();

    private Producto productoEdicion;
    private boolean guardado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarCombos();
        configurarListeners();
        aplicarConfiguracionInventario();
    }

    public void setProducto(Producto producto) {
        this.productoEdicion = producto;
        if (producto != null) {
            lblTituloModal.setText("EDITAR PRODUCTO");
            if ("PAQUETE".equalsIgnoreCase(producto.getTipoVenta())) {
                List<DetallePaquete> detalles = repository.obtenerDetallesPaquete(producto.getId());
                if (detalles != null) {
                    listaDetallePaquete.addAll(detalles);
                }
            }
            InventarioUIUtil.cargarProductoEnFormulario(
                    producto, txtCodigoBarras, txtNombre, txtDescripcion,
                    txtPrecio, txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo,
                    txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbUnidadMedida, cmbCategoria, cmbProveedor
            );
        } else {
            lblTituloModal.setText("NUEVO PRODUCTO");
            limpiarFormulario();
        }
        actualizarTextoBotonPaquete();
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void aplicarConfiguracionInventario() {
        OpcionesHabilitadas opciones = ConfiguracionSistema.getInstancia().getOpciones();
        boolean usarInventario = opciones.isUsarInventario();

        if (contenedorStock != null) {
            contenedorStock.setVisible(usarInventario);
            contenedorStock.setManaged(usarInventario);
        }
        if (txtPorcentajeGanancia != null) {
            txtPorcentajeGanancia.setDisable(!opciones.isCalcularPrecio());
        }
    }

    private void inicializarCombos() {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        cmbTipoVenta.setItems(FXCollections.observableArrayList("UNIDAD", "GRANEL", "PAQUETE"));
        cmbTipoVenta.setValue("UNIDAD");

        listaUnidades.setAll(unidadRepository.obtenerActivasDTO().stream()
                .map(DTOMapper::toModel)
                .collect(Collectors.toList()));
        cmbUnidadMedida.setItems(listaUnidades);

        cargarComboCategorias();
        cargarComboProveedores();
    }

    private void configurarListeners() {
        listaDetallePaquete.addListener((ListChangeListener<DetallePaquete>) change -> actualizarTextoBotonPaquete());

        cmbTipoVenta.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esPaquete = "PAQUETE".equalsIgnoreCase(newVal);
            if (pnlContenidoPaquete != null) {
                pnlContenidoPaquete.setVisible(esPaquete);
                pnlContenidoPaquete.setManaged(esPaquete);
            }
            if (!esPaquete) {
                listaDetallePaquete.clear();
            }
            actualizarTextoBotonPaquete();
        });

        if (txtPrecioCompra != null) {
            txtPrecioCompra.textProperty().addListener((o, old, nv) -> calcularPrecioVenta());
        }
        if (txtPorcentajeGanancia != null) {
            txtPorcentajeGanancia.textProperty().addListener((o, old, nv) -> calcularPrecioVenta());
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String codigo = txtCodigoBarras.getText() != null ? txtCodigoBarras.getText().trim() : "";
        int idExcluir = (productoEdicion != null) ? productoEdicion.getId() : 0;

        if (!codigo.isEmpty() && repository.existeCodigoBarras(codigo, idExcluir)) {
            InventarioUIUtil.mostrarAlerta("Código Duplicado", "Ya existe un producto con el código: " + codigo, Alert.AlertType.WARNING);
            return;
        }

        if ("PAQUETE".equalsIgnoreCase(cmbTipoVenta.getValue()) && listaDetallePaquete.isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Paquete Vacío", "Debe configurar al menos un producto dentro del paquete.", Alert.AlertType.WARNING);
            return;
        }

        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();
        if (!usarInventario) {
            txtStock.setText("0");
            txtStockMinimo.setText("0");
        }

        if (productoEdicion == null) {
            Producto nuevo = InventarioUIUtil.extraerProductoDeFormulario(
                    null, txtCodigoBarras, txtNombre, txtDescripcion, txtPrecio,
                    txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo,
                    txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbUnidadMedida, cmbCategoria, cmbProveedor
            );
            if (repository.guardarDTO(DTOMapper.toDTO(nuevo))) {
                if ("PAQUETE".equalsIgnoreCase(nuevo.getTipoVenta())) {
                    repository.guardarDetallesPaquete(nuevo.getId(), listaDetallePaquete);
                }
                guardado = true;
                cerrarVentana();
            }
        } else {
            InventarioUIUtil.extraerProductoDeFormulario(
                    productoEdicion, txtCodigoBarras, txtNombre, txtDescripcion, txtPrecio,
                    txtPrecioCompra, txtPorcentajeGanancia, txtPrecioMayoreo,
                    txtStock, txtStockMinimo, cmbEstado, cmbTipoVenta, cmbUnidadMedida, cmbCategoria, cmbProveedor
            );
            if (repository.actualizarDTO(DTOMapper.toDTO(productoEdicion))) {
                List<DetallePaquete> detalles = "PAQUETE".equalsIgnoreCase(productoEdicion.getTipoVenta()) ? listaDetallePaquete : null;
                repository.reemplazarDetallesPaquete(productoEdicion.getId(), detalles);
                guardado = true;
                cerrarVentana();
            }
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

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
    private void onEscanearCodigoFormulario(ActionEvent event) {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }

        Producto encontrado = DTOMapper.toModel(repository.buscarPorCodigoBarrasDTO(codigo));
        if (encontrado != null && (productoEdicion == null || encontrado.getId() != productoEdicion.getId())) {
            SoundUtil.emitirBeep(900, 120);
            InventarioUIUtil.mostrarAlerta("Código en Uso", "El código pertenece a: " + encontrado.getNombre(), Alert.AlertType.WARNING);
        } else {
            SoundUtil.emitirBeep(450, 180);
            txtNombre.requestFocus();
        }
    }

    private void calcularPrecioVenta() {
        if (!ConfiguracionSistema.getInstancia().getOpciones().isCalcularPrecio()) {
            return;
        }
        try {
            double costo = Double.parseDouble(txtPrecioCompra.getText().trim());
            double porcentaje = Double.parseDouble(txtPorcentajeGanancia.getText().trim());
            double precioVenta = InventarioCalculosUtil.calcularPrecioVenta(costo, porcentaje);
            txtPrecio.setText(String.format(Locale.US, "%.2f", precioVenta));
        } catch (NumberFormatException ignored) {
        }
    }

    private void cargarComboCategorias() {
        listaCategorias.setAll(catRepository.listarActivas());
        cmbCategoria.setItems(listaCategorias);
        cmbCategoria.setConverter(ProductosTableUtil.crearStringConverter(Categoria::getNombre));
    }

    private void cargarComboProveedores() {
        listaProveedores.setAll(provRepository.listarActivos());
        cmbProveedor.setItems(listaProveedores);
        cmbProveedor.setConverter(ProductosTableUtil.crearStringConverter(Proveedor::getNombre));
    }

    private void limpiarFormulario() {
        txtCodigoBarras.clear();
        txtNombre.clear();
        if (txtDescripcion != null) {
            txtDescripcion.clear();
        }

        OpcionesHabilitadas opciones = ConfiguracionSistema.getInstancia().getOpciones();
        txtPrecioCompra.setText("0.00");
        txtPorcentajeGanancia.setText(opciones.isCalcularPrecio() ? String.format(Locale.US, "%.2f", opciones.getMargenGanancia()) : "0.00");
        txtPrecio.setText("0.00");
        txtPrecioMayoreo.setText("0.00");
        txtStock.setText("0");
        txtStockMinimo.setText("5");

        cmbEstado.setValue("ACTIVO");
        cmbTipoVenta.setValue("UNIDAD");
        cmbUnidadMedida.setValue(listaUnidades.stream().filter(UnidadMedida::isPredeterminado).findFirst().orElse(null));
        cmbCategoria.setValue(null);
        cmbProveedor.setValue(null);
        listaDetallePaquete.clear();
    }

    private boolean validarCampos() {
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Campos vacíos", "Nombre y Precio Venta son obligatorios.", Alert.AlertType.WARNING);
            return false;
        }

        if (usarInventario && (txtStock.getText() == null || txtStock.getText().trim().isEmpty())) {
            InventarioUIUtil.mostrarAlerta("Campos vacíos", "Ingresa el Stock inicial.", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbCategoria.getValue() == null) {
            InventarioUIUtil.mostrarAlerta("Categoría requerida", "Selecciona una categoría.", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbProveedor.getValue() == null) {
            InventarioUIUtil.mostrarAlerta("Proveedor requerido", "Selecciona un proveedor.", Alert.AlertType.WARNING);
            return false;
        }

        try {
            Double.parseDouble(txtPrecio.getText().trim());
            if (txtPrecioCompra != null && !txtPrecioCompra.getText().trim().isEmpty()) {
                Double.parseDouble(txtPrecioCompra.getText().trim());
            }
            if (txtPorcentajeGanancia != null && !txtPorcentajeGanancia.getText().trim().isEmpty()) {
                Double.parseDouble(txtPorcentajeGanancia.getText().trim());
            }
            if (txtPrecioMayoreo != null && !txtPrecioMayoreo.getText().trim().isEmpty()) {
                Double.parseDouble(txtPrecioMayoreo.getText().trim());
            }

            if (usarInventario) {
                if (txtStock != null && !txtStock.getText().trim().isEmpty()) {
                    Double.parseDouble(txtStock.getText().trim());
                }
                if (txtStockMinimo != null && !txtStockMinimo.getText().trim().isEmpty()) {
                    Double.parseDouble(txtStockMinimo.getText().trim());
                }
            }
        } catch (NumberFormatException e) {
            InventarioUIUtil.mostrarAlerta("Datos inválidos", "Los campos numéricos contienen formatos incorrectos.", Alert.AlertType.ERROR);
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
