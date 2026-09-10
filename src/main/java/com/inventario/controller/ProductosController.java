package com.inventario.controller;

import com.inventario.config.ConfiguracionSistema;
import com.inventario.model.OpcionesHabilitadas;
import com.inventario.model.Producto;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.util.Inventario.InventarioUIUtil;
import com.inventario.util.Productos.KeyboardShortcutUtil;
import com.inventario.util.Productos.ProductosTableUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductosController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Producto> tblProductos;

    private final ProductoRepository repository;
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private Producto productoSeleccionado;

    public ProductosController() {
        this(new ProductoRepositoryImpl());
    }

    public ProductosController(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarListeners();
        cargarDatos();
        configurarAtajosTeclado();
        aplicarConfiguracionInventario();
    }

    private void aplicarConfiguracionInventario() {
        OpcionesHabilitadas opciones = ConfiguracionSistema.getInstancia().getOpciones();
        tblProductos.getColumns().stream()
                .filter(col -> "Stock".equalsIgnoreCase(col.getText()))
                .findFirst()
                .ifPresent(col -> col.setVisible(opciones.isUsarInventario()));
    }

    private void configurarTabla() {
        ProductosTableUtil.configurarColumnasProductos(
                tblProductos,
                this::abrirModalFormulario,
                this::ejecutarEliminacion
        );

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            productoSeleccionado = newSel;
        });
    }

    private void configurarListeners() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarProductos(newVal));
        }
    }

    private void cargarDatos() {
        listarProductos();
    }

    private void listarProductos() {
        aplicarConfiguracionInventario();
        listaProductos.clear();
        listaProductos.addAll(repository.listarTodosDTO().stream()
                .map(DTOMapper::toModel)
                .collect(Collectors.toList()));
        tblProductos.setItems(listaProductos);
    }

    private void buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listarProductos();
            return;
        }
        String termino = criterio.trim();
        listaProductos.clear();

        Producto pCodigo = DTOMapper.toModel(repository.buscarPorCodigoBarrasDTO(termino));
        if (pCodigo != null) {
            listaProductos.add(pCodigo);
        } else {
            listaProductos.addAll(repository.buscarPorNombreDTO(termino).stream()
                    .map(DTOMapper::toModel)
                    .collect(Collectors.toList()));
        }
        tblProductos.setItems(listaProductos);
    }

    // --- ACCIONES FXML (MODALES) ---
    @FXML
    private void onNuevoProducto(ActionEvent event) {
        abrirModalFormulario(null);
    }

    @FXML
    private void onEditarProducto(ActionEvent event) {
        if (productoSeleccionado == null) {
            InventarioUIUtil.mostrarAlerta("Advertencia", "Selecciona un producto de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }
        abrirModalFormulario(productoSeleccionado);
    }

    private void abrirModalFormulario(Producto productoAEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventario/view/ProductoFormModalView.fxml"));
            Parent root = loader.load();

            ProductoFormModalController modalController = loader.getController();
            modalController.setProducto(productoAEditar);

            Stage stage = new Stage();
            stage.setTitle(productoAEditar == null ? "Nuevo Producto" : "Editar Producto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (modalController.isGuardado()) {
                listarProductos();
            }
        } catch (IOException e) {
            e.printStackTrace();
            InventarioUIUtil.mostrarAlerta("Error", "No se pudo abrir el formulario de producto.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        if (productoSeleccionado == null) {
            InventarioUIUtil.mostrarAlerta("Advertencia", "Selecciona un producto de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }
        ejecutarEliminacion(productoSeleccionado);
    }

    private void ejecutarEliminacion(Producto producto) {
        if (producto == null) {
            return;
        }

        boolean tieneHistorial = repository.tieneAsociaciones(producto.getId());

        if (tieneHistorial) {
            boolean confirmar = InventarioUIUtil.mostrarConfirmacion(
                    "Producto con historial",
                    "Este producto tiene ventas o movimientos asociados.",
                    "No se puede eliminar de forma definitiva. ¿Deseas deshabilitarlo (Estado: INACTIVO)?"
            );

            if (confirmar && repository.eliminarLogico(producto.getId())) {
                InventarioUIUtil.mostrarAlerta("Éxito", "El producto ha sido deshabilitado correctamente.", Alert.AlertType.INFORMATION);
                listarProductos();
            }
        } else {
            boolean confirmar = InventarioUIUtil.mostrarConfirmacion(
                    "Confirmar eliminación",
                    "¿Eliminar producto definitivamente?",
                    "El producto '" + producto.getNombre() + "' se eliminará permanentemente."
            );

            if (confirmar && repository.eliminar(producto.getId())) {
                InventarioUIUtil.mostrarAlerta("Éxito", "Producto eliminado correctamente.", Alert.AlertType.INFORMATION);
                listarProductos();
            }
        }
    }

    private void configurarAtajosTeclado() {
        KeyboardShortcutUtil.registrarAtajosCrud(
                tblProductos,
                () -> onNuevoProducto(null),
                () -> onNuevoProducto(null),
                () -> onEditarProducto(null),
                () -> onEliminar(null),
                () -> {
                    if (txtBuscar != null) {
                        txtBuscar.requestFocus();
                        txtBuscar.selectAll();
                    }
                },
                this::listarProductos
        );
    }
}
