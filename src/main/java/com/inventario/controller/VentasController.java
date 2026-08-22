package com.inventario.controller;

import com.inventario.config.ConfiguracionSistema;
import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.repository.ClienteRepository;
import com.inventario.repository.Impl.ClienteRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.Impl.PromocionRepositoryImpl;
import com.inventario.repository.Impl.VentaRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.PromocionRepository;
import com.inventario.repository.VentaRepository;
import com.inventario.util.Productos.KeyboardShortcutUtil;
import com.inventario.util.Ventas.BusquedaProductoUtil;
import com.inventario.util.Ventas.CarritoService;
import com.inventario.util.Ventas.ClienteHelper;
import com.inventario.util.Ventas.CodigoBarrasParser;
import com.inventario.util.Ventas.ModalNavigationUtil;
import com.inventario.util.Ventas.VentasTablaUtil;
import com.inventario.util.audio.SoundUtil;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class VentasController implements Initializable {

    @FXML
    private TextField txtCodigoBarras;
    @FXML
    private ComboBox<Cliente> cmbCliente;
    @FXML
    private ComboBox<Producto> cmbProducto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Label lblTotal;

    // Tabla del Carrito de Compras
    @FXML
    private TableView<DetalleVenta> tblCarrito;
    private TableColumn<DetalleVenta, String> colProducto;
    private TableColumn<DetalleVenta, Integer> colCantidad;
    private TableColumn<DetalleVenta, Double> colPrecioUnitario;
    private TableColumn<DetalleVenta, Double> colSubtotal;

    // Repositorios de datos
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;
    private final CarritoService carritoService;

    public VentasController() {
        this(new VentaRepositoryImpl(), new ClienteRepositoryImpl(), new ProductoRepositoryImpl(), new PromocionRepositoryImpl(), new CarritoService());
    }

    public VentasController(VentaRepository ventaRepository, ClienteRepository clienteRepository, ProductoRepository productoRepository, PromocionRepository promocionRepository, CarritoService carritoService) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.promocionRepository = promocionRepository;
        this.carritoService = carritoService;
    }

    // Lista en memoria que actúa como el carrito de compras temporal
    private final ObservableList<DetalleVenta> carritoItems = FXCollections.observableArrayList();
    private double totalAcumulado = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombosIniciales();

        // Delegamos la configuración de la tabla a la clase utilitaria
        VentasTablaUtil.configurarColumnasCarrito(tblCarrito);
        tblCarrito.setItems(carritoService.getItems());

        cmbProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            txtPrecio.setText(newVal != null ? String.format("%.2f", newVal.getPrecio()) : "0.00");
        });

        configurarEventosTeclado();

        Platform.runLater(() -> {
            if (txtCodigoBarras != null) {
                txtCodigoBarras.requestFocus();
            }
        });
    }

    @FXML
    void onBuscarProductoModal(ActionEvent event) {
        Stage stageActual = null;

        // Compatible con Java 11 (Casting tradicional)
        if (event != null && event.getSource() instanceof Node) {
            Node node = (Node) event.getSource();
            stageActual = (Stage) node.getScene().getWindow();
        } // Si la llamada proviene del atajo de teclado (event es null)
        else if (txtCodigoBarras != null && txtCodigoBarras.getScene() != null) {
            stageActual = (Stage) txtCodigoBarras.getScene().getWindow();
        } else if (tblCarrito != null && tblCarrito.getScene() != null) {
            stageActual = (Stage) tblCarrito.getScene().getWindow();
        }

        if (stageActual != null) {
            List<Producto> productos = productoRepository.listarTodos();
            BusquedaProductoUtil.abrirModalBusqueda(stageActual, productos)
                    .ifPresent(this::agregarProductoAVenta);
        }
    }

    private void agregarProductoAVenta(Producto producto) {
        if (producto != null) {
            if ("GRANEL".equalsIgnoreCase(producto.getTipoVenta())) {
                abrirModalGranel(producto, 1.0);
            } else {
                if (procesarAgregadoACarrito(producto, 1.0)) {
                    SoundUtil.emitirBeep(900, 120);
                }
            }
        }
    }

    private void configurarEventosTeclado() {
        tblCarrito.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ADD:
                case PLUS:
                    onIncrementarCantidad(null);
                    event.consume();
                    break;
                case SUBTRACT:
                case MINUS:
                    onDecrementarCantidad(null);
                    event.consume();
                    break;
                default:
                    break;
            }
        });

        KeyboardShortcutUtil.registrarAtajosVentas(
                txtCodigoBarras != null ? txtCodigoBarras : tblCarrito,
                () -> onAgregarAlCarrito(null),
                () -> onQuitarItem(null),
                () -> onRegistrarVentaCompleta(null),
                () -> {
                    if (txtCodigoBarras != null) {
                        txtCodigoBarras.requestFocus();
                        txtCodigoBarras.selectAll();
                    } else if (cmbProducto != null) {
                        cmbProducto.requestFocus();
                    }
                },
                () -> onBuscarProductoModal(null),
                this::limpiarPantallaCompleta
        );

        if (txtCodigoBarras != null) {
            txtCodigoBarras.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ADD || event.getCode() == KeyCode.PLUS) {
                    if (txtCodigoBarras.getText().isEmpty() && !tblCarrito.getItems().isEmpty()) {
                        if (tblCarrito.getSelectionModel().getSelectedItem() == null) {
                            tblCarrito.getSelectionModel().selectLast();
                        }
                        onIncrementarCantidad(null);
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.SUBTRACT || event.getCode() == KeyCode.MINUS) {
                    if (txtCodigoBarras.getText().isEmpty() && !tblCarrito.getItems().isEmpty()) {
                        if (tblCarrito.getSelectionModel().getSelectedItem() == null) {
                            tblCarrito.getSelectionModel().selectLast();
                        }
                        onDecrementarCantidad(null);
                        event.consume();
                    }
                }
            });
        }
    }

    @FXML
    void onRegistrarVentaCompleta(ActionEvent event) {
        if (carritoService.estaVacio()) {
            mostrarAlerta("Carrito Vacío", "No hay artículos en el carrito.", Alert.AlertType.WARNING);
            return;
        }

        boolean ofrecerCredito = ConfiguracionSistema.getInstancia().getOpciones().isOfrecerCredito();
        Cliente cliente = cmbCliente.getValue();

        // Si ofrece crédito pero no seleccionaron cliente, o si no ofrece crédito, se asigna el cliente general por defecto
        if (cliente == null) {
            cliente = obtenerOCrearClienteGeneral();
        }

        final Cliente clienteFinal = cliente;
        final double total = carritoService.calcularTotal();

        boolean modalCargado = ModalNavigationUtil.<CobroModalController>abrirModal(
                getClass(),
                "/com/inventario/view/CobroModal.fxml",
                "Procesar Pago",
                modalCtrl -> modalCtrl.initData(
                        total,
                        carritoService.calcularTotalArticulos(),
                        (montoPago, debeImprimirTicket) -> {
                            // Si el pago es 0 y el cliente es público en general, se rechaza la venta a crédito
                            if (montoPago == 0.0 && "Público en General".equalsIgnoreCase(clienteFinal.getNombre())) {
                                mostrarAlerta("Cliente Requerido", "Debes seleccionar un cliente registrado para realizar una venta a crédito.", Alert.AlertType.WARNING);
                                return;
                            }
                            procesarPersistenciaVenta(clienteFinal, montoPago, debeImprimirTicket);
                        }
                )
        );

        if (!modalCargado) {
            mostrarAlerta("Error de Vista", "No se pudo cargar la ventana de cobro.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Persiste la venta en la base de datos, manda a imprimir si corresponde y
     * reinicia el estado del carrito/pantalla.
     */
    private void procesarPersistenciaVenta(Cliente cliente, double pagoCon, boolean imprimirTicket) {
        try {

            Venta nuevaVenta = new Venta();
            nuevaVenta.setCliente(cliente);
            nuevaVenta.setTotal(carritoService.calcularTotal());

            List<DetalleVenta> detalles = new ArrayList<>(carritoService.getItems());
            boolean guardadoExitoso = ventaRepository.registrarVenta(nuevaVenta, detalles);

            if (guardadoExitoso) {

                if (imprimirTicket) {
                    // TicketService.imprimirTicket(nuevaVenta, detalles, pagoCon);
                    mostrarAlerta("Venta Exitosa", "La venta se registró e imprimió correctamente.", Alert.AlertType.INFORMATION);
                }

                // Limpia pantalla y carrito inmediatamente sin frenar el flujo
                limpiarPantallaCompleta();

            } else {
                mostrarAlerta("Error de Guardado", "No se pudo completar el registro de la venta en la base de datos.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            mostrarAlerta("Error Inesperado", "Ocurrió un problema al procesar la venta: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Busca al cliente genérico en la base de datos de forma flexible. Si no
     * existe, lo registra automáticamente para evitar asociar ventas al cliente
     * incorrecto.
     */
    private Cliente obtenerOCrearClienteGeneral() {
        List<Cliente> clientesActivos = clienteRepository.listarActivos();

        // 1. Buscamos coincidencias insensibles a tildes y mayúsculas
        Optional<Cliente> clienteGeneralOpt = clientesActivos.stream()
                .filter(c -> {
                    if (c.getNombre() == null) {
                        return false;
                    }
                    String nombreLimpio = c.getNombre().toLowerCase()
                            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
                    return nombreLimpio.contains("publico")
                            || nombreLimpio.contains("general")
                            || nombreLimpio.contains("mostrador");
                })
                .findFirst();

        if (clienteGeneralOpt.isPresent()) {
            return clienteGeneralOpt.get();
        }

        // 2. Si no existe ningún cliente genérico en la BD, lo crea automáticamente
        Cliente nuevoClienteGeneral = new Cliente();
        nuevoClienteGeneral.setNombre("Público en General");
        nuevoClienteGeneral.setEstado("ACTIVO");

        if (clienteRepository.guardar(nuevoClienteGeneral)) {
            return clienteRepository.listarActivos().stream()
                    .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase("Público en General"))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    /**
     * Evento ejecutado automáticamente cuando la pistola escanea un código y
     * envía la tecla ENTER al final.
     */
    @FXML
    void onEscanearCodigoBarras(ActionEvent event) {
        String entrada = txtCodigoBarras.getText().trim();
        if (entrada.isEmpty()) {
            return;
        }

        try {
            CodigoBarrasParser.ResultadoParseo parseo = CodigoBarrasParser.parsear(entrada);
            Producto producto = productoRepository.buscarPorCodigoBarras(parseo.getCodigo());

            if (producto != null) {
                SoundUtil.emitirBeep(900, 120);
                if ("GRANEL".equalsIgnoreCase(producto.getTipoVenta())) {
                    abrirModalGranel(producto, parseo.getCantidad());
                } else {
                    procesarAgregadoACarrito(producto, parseo.getCantidad());
                }
            } else {
                SoundUtil.emitirBeep(450, 180);
                mostrarAlerta("Producto no encontrado", "No se encontró ningún artículo con el código: " + parseo.getCodigo(), Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "No se pudo interpretar la cantidad introducida con *", Alert.AlertType.WARNING);
        }

        txtCodigoBarras.clear();
        txtCodigoBarras.requestFocus();
    }

    private void abrirModalGranel(Producto producto, double cantidadInicial) {
        // Declaramos un arreglo de un solo elemento para capturar el controlador
        final CantidadGranelModalController[] modalCtrlRef = new CantidadGranelModalController[1];

        boolean modalCargado = ModalNavigationUtil.<CantidadGranelModalController>abrirModal(
                getClass(),
                "/com/inventario/view/CantidadGranelModal.fxml",
                "Cantidad de Producto",
                modalController -> {
                    modalController.setPromocionRepository(promocionRepository);
                    modalController.initData(producto, cantidadInicial);
                    modalCtrlRef[0] = modalController; // Guardamos la referencia
                }
        );

        if (modalCargado && modalCtrlRef[0] != null) {
            CantidadGranelModalController modalCtrl = modalCtrlRef[0];
            // Evaluamos si el usuario presionó 'Aceptar' o dio Enter
            if (modalCtrl.isAceptado()) {
                double cantidadFinal = modalCtrl.getCantidadIngresada();
                double precioConPromocion = modalCtrl.getPrecioUnitarioAplicado();
                procesarAgregadoACarritoConPrecio(producto, cantidadFinal, precioConPromocion);
            }
        } else {
            mostrarAlerta("Error", "No se pudo cargar la ventana para venta a granel.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        limpiarPantallaCompleta();
    }

    @FXML
    void onIncrementarCantidad(ActionEvent event) {
        DetalleVenta seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        if ("GRANEL".equalsIgnoreCase(seleccionado.getProducto().getTipoVenta())) {
            abrirModalGranel(seleccionado.getProducto(), seleccionado.getCantidad());
        } else {
            procesarAgregadoACarrito(seleccionado.getProducto(), 1.0);
        }
    }

    @FXML
    void onDecrementarCantidad(ActionEvent event) {
        DetalleVenta seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        double nuevaCantidad = seleccionado.getCantidad() - 1.0;
        if (nuevaCantidad <= 0) {
            carritoItems.remove(seleccionado);
        } else {
            seleccionado.setCantidad(nuevaCantidad);
            tblCarrito.refresh();
        }
        actualizarEtiquetaTotal();
    }

    private void cargarCombosIniciales() {
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteRepository.listarActivos());
        cmbCliente.setItems(clientes);
        cmbCliente.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cmbCliente.setButtonCell(cmbCliente.getCellFactory().call(null));

        List<Producto> listaProductos = productoRepository.listarTodos();

        // Si usarInventario es true, filtramos solo con stock > 0
        if (usarInventario) {
            listaProductos = listaProductos.stream()
                    .filter(p -> p.getStock() > 0)
                    .collect(Collectors.toList());
        }

        ObservableList<Producto> productos = FXCollections.observableArrayList(listaProductos);
        cmbProducto.setItems(productos);
        cmbProducto.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Ocultamos la etiqueta de Stock si la bandera está desactivada
                    setText(usarInventario ? item.getNombre() + " (Stock: " + item.getStock() + ")" : item.getNombre());
                }
            }
        });
        cmbProducto.setButtonCell(cmbProducto.getCellFactory().call(null));
    }

    @FXML
    void onAgregarAlCarrito(ActionEvent event) {
        Producto prodSeleccionado = cmbProducto.getValue();
        String cantTexto = txtCantidad.getText().trim();

        if (prodSeleccionado == null) {
            mostrarAlerta("Validación", "Por favor, selecciona un producto.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double cantidad = cantTexto.isEmpty() ? 1.0 : Double.parseDouble(cantTexto);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            if ("GRANEL".equalsIgnoreCase(prodSeleccionado.getTipoVenta())) {
                abrirModalGranel(prodSeleccionado, cantidad);
            } else {
                if (procesarAgregadoACarrito(prodSeleccionado, cantidad)) {
                    SoundUtil.emitirBeep(900, 120);
                }
            }
            txtCantidad.clear();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de datos", "Ingresa una cantidad válida mayor a 0.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Lógica unificada para validar stock, buscar duplicados y
     * agregar/incrementar renglones.
     */
    private boolean procesarAgregadoACarrito(Producto producto, double cantidadAñadir) {
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        // Validar límite de stock únicamente si usarInventario es TRUE
        if (usarInventario) {
            String errorStock = carritoService.agregarOActualizarProducto(producto, cantidadAñadir);
            if (errorStock != null) {
                mostrarAlerta("Stock Insuficiente", errorStock, Alert.AlertType.WARNING);
                return false;
            }
        } else {
            // Si inventario está desactivado, añadimos directamente sin validar existencias
            carritoService.agregarSinValidarStock(producto, cantidadAñadir);
        }

        tblCarrito.refresh();
        actualizarEtiquetaTotal();
        return true;
    }

    private boolean procesarAgregadoACarritoConPrecio(Producto producto, double cantidadAñadir, double precioConPromocion) {
        Producto productoModificado = new Producto(
                producto.getId(),
                producto.getCodigoBarras(),
                producto.getNombre(),
                producto.getDescripcion(),
                precioConPromocion,
                producto.getPrecioMayoreo(),
                producto.getPrecioCompra(),
                producto.getPorcentajeGanancia(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getTipoVenta(),
                producto.getEstado(),
                producto.getCategoria(),
                producto.getProveedor()
        );
        return procesarAgregadoACarrito(productoModificado, cantidadAñadir);
    }

    @FXML
    void onQuitarItem(ActionEvent event) {
        DetalleVenta itemSeleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            mostrarAlerta("Selección vacía", "Elige un artículo de la tabla para removerlo del carrito.", Alert.AlertType.WARNING);
            return;
        }
        carritoService.removerItem(itemSeleccionado);
        actualizarEtiquetaTotal();

        if (txtCodigoBarras != null) {
            txtCodigoBarras.requestFocus();
        }
    }

    private void actualizarEtiquetaTotal() {
        lblTotal.setText(String.format("$%.2f", carritoService.calcularTotal()));
    }

    private void limpiarPantallaCompleta() {
        cmbCliente.getSelectionModel().clearSelection();
        cmbProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtPrecio.setText("0.00");
        carritoService.limpiar();
        actualizarEtiquetaTotal();

        if (txtCodigoBarras != null) {
            txtCodigoBarras.clear();
            txtCodigoBarras.requestFocus();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
