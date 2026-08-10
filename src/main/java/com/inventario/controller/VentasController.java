package com.inventario.controller;

import com.inventario.model.Cliente;
import com.inventario.model.DetalleVenta;
import com.inventario.model.Producto;
import com.inventario.model.Venta;
import com.inventario.repository.ClienteRepository;
import com.inventario.repository.Impl.ClienteRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.Impl.VentaRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.VentaRepository;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private final VentaRepository ventaRepository = new VentaRepositoryImpl();
    private final ClienteRepository clienteRepository = new ClienteRepositoryImpl();
    private final ProductoRepository productoRepository = new ProductoRepositoryImpl();

    // Lista en memoria que actúa como el carrito de compras temporal
    private final ObservableList<DetalleVenta> carritoItems = FXCollections.observableArrayList();
    private double totalAcumulado = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Cargar Combos de Datos Iniciales (Clientes y Productos Activos)
        cargarCombosIniciales();

        // 2. Configurar la estructura de columnas del carrito
        configurarColumnasCarrito();

        // 3. Listener para rellenar automáticamente el precio al seleccionar un artículo manualmente
        cmbProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtPrecio.setText(String.format("%.2f", newVal.getPrecio()));
            } else {
                txtPrecio.setText("0.00");
            }
        });

        // 4. Asegurar que el cursor siempre empiece en el campo del lector al entrar al módulo
        if (txtCodigoBarras != null) {
            Platform.runLater(() -> txtCodigoBarras.requestFocus());
        }
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
     * Evento ejecutado automáticamente cuando la pistola escanea un código y
     * envía la tecla ENTER al final.
     */
    @FXML
    void onEscanearCodigoBarras(ActionEvent event) {
        String codigo = txtCodigoBarras.getText().trim();

        if (codigo.isEmpty()) {
            return;
        }

        // Buscar producto directo (retorna Producto o null si no existe)
        Producto productoEncontrado = productoRepository.buscarPorCodigoBarras(codigo);

        if (productoEncontrado != null) {
            emitirBeep(900, 120); 
            procesarAgregadoACarrito(productoEncontrado, 1);
        } else {
            emitirBeep(450, 180); 
            mostrarAlerta("Producto no encontrado", "No se encontró ningún artículo registrado con el código: " + codigo, Alert.AlertType.WARNING);
        }

        // Limpiar el campo y mantener el foco listo para la siguiente lectura
        txtCodigoBarras.clear();
        txtCodigoBarras.requestFocus();
    }

    private void cargarCombosIniciales() {
        // Cargar Clientes Activos usando el repositorio
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

        // Cargar Productos Disponibles
        ObservableList<Producto> productos = FXCollections.observableArrayList(
                productoRepository.listarTodos().stream()
                        .filter(p -> p.getStock() > 0)
                        .collect(java.util.stream.Collectors.toList())
        );
        cmbProducto.setItems(productos);

        cmbProducto.setCellFactory(lv -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (Stock: " + item.getStock() + ")");
            }
        });
        cmbProducto.setButtonCell(cmbProducto.getCellFactory().call(null));
    }

    private void configurarColumnasCarrito() {
        TableColumn<DetalleVenta, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        TableColumn<DetalleVenta, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setStyle("-fx-alignment: CENTER;");

        TableColumn<DetalleVenta, Double> colPrecioUnitario = new TableColumn<>("Precio Unit.");
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioUnitario.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<DetalleVenta, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setStyle("-fx-alignment: CENTER-RIGHT;");

        tblCarrito.getColumns().setAll(colProducto, colCantidad, colPrecioUnitario, colSubtotal);
        tblCarrito.setItems(carritoItems);
    }

    @FXML
    void onAgregarAlCarrito(ActionEvent event) {
        Producto prodSeleccionado = cmbProducto.getValue();
        String cantTexto = txtCantidad.getText().trim();

        if (prodSeleccionado == null) {
            mostrarAlerta("Validación", "Por favor, selecciona un producto.", Alert.AlertType.WARNING);
            return;
        }

        int cantidad;
        try {
            cantidad = cantTexto.isEmpty() ? 1 : Integer.parseInt(cantTexto);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de datos", "La cantidad debe ser un número entero mayor a 0.", Alert.AlertType.WARNING);
            return;
        }

        procesarAgregadoACarrito(prodSeleccionado, cantidad);
        txtCantidad.clear();
    }

    /**
     * Lógica unificada para validar stock, buscar duplicados y
     * agregar/incrementar renglones.
     */
    private void procesarAgregadoACarrito(Producto producto, int cantidadAñadir) {
        if (producto.getStock() <= 0) {
            mostrarAlerta("Sin Stock", "El producto " + producto.getNombre() + " está agotado en almacén.", Alert.AlertType.WARNING);
            return;
        }

        // Buscar si el producto ya existe en el carrito
        DetalleVenta itemExistente = null;
        for (DetalleVenta item : carritoItems) {
            if (item.getProducto() != null && item.getProducto().getId() == producto.getId()) {
                itemExistente = item;
                break;
            }
        }

        if (itemExistente != null) {
            int nuevaCantidadTotal = itemExistente.getCantidad() + cantidadAñadir;

            // Validar si el acumulado en carrito supera el stock
            if (nuevaCantidadTotal > producto.getStock()) {
                mostrarAlerta("Stock Insuficiente", "El acumulado en carrito (" + nuevaCantidadTotal + ") supera el stock disponible (" + producto.getStock() + ").", Alert.AlertType.WARNING);
                return;
            }

            itemExistente.setCantidad(nuevaCantidadTotal);
            tblCarrito.refresh();
        } else {
            if (cantidadAñadir > producto.getStock()) {
                mostrarAlerta("Stock Insuficiente", "Solo quedan " + producto.getStock() + " unidades de este artículo.", Alert.AlertType.WARNING);
                return;
            }

            // Agregar nuevo registro usando tu constructor
            DetalleVenta nuevoDetalle = new DetalleVenta(producto, cantidadAñadir, producto.getPrecio());
            carritoItems.add(nuevoDetalle);
        }

        recalcularTotal();
    }

    @FXML
    void onQuitarItem(ActionEvent event) {
        DetalleVenta itemSeleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            mostrarAlerta("Selección vacía", "Elige un artículo de la tabla para removerlo del carrito.", Alert.AlertType.WARNING);
            return;
        }
        carritoItems.remove(itemSeleccionado);
        recalcularTotal();

        if (txtCodigoBarras != null) {
            txtCodigoBarras.requestFocus();
        }
    }

    @FXML
    void onRegistrarVentaCompleta(ActionEvent event) {
        Cliente cliente = cmbCliente.getValue();
        if (cliente == null) {
            mostrarAlerta("Validación", "Debes seleccionar un cliente para procesar la transacción.", Alert.AlertType.WARNING);
            return;
        }

        if (carritoItems.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No hay artículos en la orden actual para procesar la venta.", Alert.AlertType.WARNING);
            return;
        }

        Venta nuevaVenta = new Venta(cliente, totalAcumulado, "COMPLETADA");

        if (ventaRepository.registrarVenta(nuevaVenta, carritoItems)) {
            mostrarAlerta("Venta Procesada", "La venta se ha registrado exitosamente e inventarios actualizados.", Alert.AlertType.INFORMATION);
            limpiarPantallaCompleta();
            cargarCombosIniciales();
        } else {
            mostrarAlerta("Error Crítico", "Ocurrió un problema en la transacción SQL (posible cambio de stock concurrente). Venta cancelada de forma segura.", Alert.AlertType.ERROR);
        }
    }

    private void recalcularTotal() {
        totalAcumulado = carritoItems.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        lblTotal.setText(String.format("$%.2f", totalAcumulado));
    }

    private void limpiarPantallaCompleta() {
        cmbCliente.getSelectionModel().clearSelection();
        cmbProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtPrecio.setText("0.00");
        carritoItems.clear();
        recalcularTotal();

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
