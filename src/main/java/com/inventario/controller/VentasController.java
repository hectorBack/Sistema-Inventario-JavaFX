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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    public VentasController(VentaRepository ventaRepository, ClienteRepository clienteRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    // Lista en memoria que actúa como el carrito de compras temporal
    private final ObservableList<DetalleVenta> carritoItems = FXCollections.observableArrayList();
    private double totalAcumulado = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombosIniciales();
        configurarColumnasCarrito();

        cmbProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            txtPrecio.setText(newVal != null ? String.format("%.2f", newVal.getPrecio()) : "0.00");
        });

        // Configuración de escuchas de teclado
        tblCarrito.setOnKeyPressed(this::manejarTeclasTabla);

        Platform.runLater(() -> {
            if (txtCodigoBarras != null) {
                txtCodigoBarras.requestFocus();
                txtCodigoBarras.getScene().setOnKeyPressed(this::manejarTeclasGlobales);
            }
        });
    }

    private void manejarTeclasGlobales(KeyEvent event) {
        if (event.getCode() == KeyCode.F12) {
            onRegistrarVentaCompleta(null);
            event.consume();
        }
    }

    private void manejarTeclasTabla(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.DELETE) {
            onQuitarItem(null);
            event.consume();
        } else if (code == KeyCode.ADD || code == KeyCode.PLUS || (event.isShiftDown() && code == KeyCode.EQUALS)) {
            onIncrementarCantidad(null);
            event.consume();
        } else if (code == KeyCode.SUBTRACT || code == KeyCode.MINUS) {
            onDecrementarCantidad(null);
            event.consume();
        }
    }

    @FXML
    void onRegistrarVentaCompleta(ActionEvent event) {
        if (carritoItems.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No hay artículos en el carrito.", Alert.AlertType.WARNING);
            return;
        }

        Cliente cliente = cmbCliente.getValue();

        // Si no seleccionaron ningún cliente de la lista, buscamos o creamos "Público en General"
        if (cliente == null) {
            cliente = obtenerOCrearClienteGeneral();
        }

        if (cliente == null) {
            mostrarAlerta("Error de Cliente", "No se pudo obtener ni registrar al cliente genérico.", Alert.AlertType.ERROR);
            return;
        }

        try {
            URL fxmlLocation = getClass().getResource("/com/inventario/view/CobroModal.fxml");
            if (fxmlLocation == null) {
                // Intento de fallback relativo a la estructura de paquetes
                fxmlLocation = getClass().getResource("CobroModal.fxml");
            }

            if (fxmlLocation == null) {
                mostrarAlerta("Error de Recurso", "No se encontró el archivo CobroModal.fxml en la ruta especificada.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            CobroModalController controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle("Procesar Pago");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));

            int totalArticulos = (int) carritoItems.stream().mapToDouble(DetalleVenta::getCantidad).sum();
            controller.initData(totalAcumulado, totalArticulos);

            modalStage.showAndWait();

            if (controller.isVentaConfirmada()) {
                Venta nuevaVenta = new Venta(cliente, totalAcumulado, "COMPLETADA");

                if (ventaRepository.registrarVenta(nuevaVenta, carritoItems)) {
                    emitirBeep(900, 120);
                    if (controller.isImprimirTicket()) {
                        // Lógica de impresión
                    }
                    mostrarAlerta("Venta Exitosa", "La transacción ha sido registrada.", Alert.AlertType.INFORMATION);
                    limpiarPantallaCompleta();
                    cargarCombosIniciales();
                } else {
                    mostrarAlerta("Error Crítico", "Ocurrió un problema en la transacción SQL.", Alert.AlertType.ERROR);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de Vista", "Detalle del error: " + e.toString(), Alert.AlertType.ERROR);
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
        String entrada = txtCodigoBarras.getText().trim();

        if (entrada.isEmpty()) {
            return;
        }

        String codigo = entrada;
        double cantidadSuma = 1.0;

        // Parseo de multiplicación por asterisco '*'
        if (entrada.contains("*")) {
            String[] partes = entrada.split("\\*");
            if (partes.length == 2) {
                try {
                    if (partes[0].length() < 5 && partes[1].length() >= 5) {
                        // Formato: CANTIDAD*CODIGO (ej. 100*75010001)
                        cantidadSuma = Double.parseDouble(partes[0].trim());
                        codigo = partes[1].trim();
                    } else {
                        // Formato: CODIGO*CANTIDAD (ej. 75010001*100 o 1*100)
                        codigo = partes[0].trim();
                        cantidadSuma = Double.parseDouble(partes[1].trim());
                    }
                } catch (NumberFormatException e) {
                    mostrarAlerta("Formato inválido", "No se pudo interpretar la cantidad introducida con *", Alert.AlertType.WARNING);
                    txtCodigoBarras.clear();
                    txtCodigoBarras.requestFocus();
                    return;
                }
            }
        }

        Producto productoEncontrado = productoRepository.buscarPorCodigoBarras(codigo);

        if (productoEncontrado != null) {
            emitirBeep(900, 120);

            // SI EL PRODUCTO ES A GRANEL SE ABRE EL MODAL
            if ("GRANEL".equalsIgnoreCase(productoEncontrado.getTipoVenta())) {
                abrirModalGranel(productoEncontrado, cantidadSuma);
            } else {
                procesarAgregadoACarrito(productoEncontrado, cantidadSuma);
            }
        } else {
            emitirBeep(450, 180);
            mostrarAlerta("Producto no encontrado", "No se encontró ningún artículo con el código: " + codigo, Alert.AlertType.WARNING);
        }

        txtCodigoBarras.clear();
        txtCodigoBarras.requestFocus();
    }

    private void abrirModalGranel(Producto producto, double cantidadInicial) {
        try {
            URL fxmlLocation = getClass().getResource("/com/inventario/view/CantidadGranelModal.fxml");
            if (fxmlLocation == null) {
                // Fallback relativo al paquete actual
                fxmlLocation = getClass().getResource("CantidadGranelModal.fxml");
            }

            if (fxmlLocation == null) {
                mostrarAlerta("Error de Recurso", "No se encontró el archivo CantidadGranelModal.fxml en la ruta especificada.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            CantidadGranelModalController modalController = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle("Cantidad de Producto");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));

            modalController.initData(producto, cantidadInicial);

            modalStage.showAndWait();

            if (modalController.isAceptado()) {
                procesarAgregadoACarrito(producto, modalController.getCantidadIngresada());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de Vista", "Detalle del error: " + e.toString(), Alert.AlertType.ERROR);
        }
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
        recalcularTotal();
    }

    private void cargarCombosIniciales() {
        List<Cliente> clientesLista = clienteRepository.listarActivos();
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

        TableColumn<DetalleVenta, Double> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setStyle("-fx-alignment: CENTER;");
        colCantidad.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Muestra 3 decimales si tiene decimales, o número entero si no los tiene
                    setText(item % 1 == 0 ? String.format("%.0f", item) : String.format("%.3f", item));
                }
            }
        });

        TableColumn<DetalleVenta, Double> colPrecioUnitario = new TableColumn<>("Precio Unit.");
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioUnitario.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPrecioUnitario.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        TableColumn<DetalleVenta, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        colSubtotal.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

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

        try {
            double cantidad = cantTexto.isEmpty() ? 1.0 : Double.parseDouble(cantTexto);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            if ("GRANEL".equalsIgnoreCase(prodSeleccionado.getTipoVenta())) {
                abrirModalGranel(prodSeleccionado, cantidad);
            } else {
                if (procesarAgregadoACarrito(prodSeleccionado, cantidad)) {
                    emitirBeep(900, 120);
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
        if (producto.getStock() <= 0) {
            mostrarAlerta("Sin Stock", "El producto está agotado en almacén.", Alert.AlertType.WARNING);
            return false;
        }

        DetalleVenta itemExistente = carritoItems.stream()
                .filter(item -> item.getProducto() != null && item.getProducto().getId() == producto.getId())
                .findFirst().orElse(null);

        if (itemExistente != null) {
            double nuevaCantidadTotal = "GRANEL".equalsIgnoreCase(producto.getTipoVenta())
                    ? cantidadAñadir
                    : itemExistente.getCantidad() + cantidadAñadir;

            if (nuevaCantidadTotal > producto.getStock()) {
                mostrarAlerta("Stock Insuficiente", "Supera el stock disponible (" + producto.getStock() + ").", Alert.AlertType.WARNING);
                return false;
            }
            itemExistente.setCantidad(nuevaCantidadTotal);
            tblCarrito.refresh();
        } else {
            if (cantidadAñadir > producto.getStock()) {
                mostrarAlerta("Stock Insuficiente", "Solo quedan " + producto.getStock() + " unidades.", Alert.AlertType.WARNING);
                return false;
            }
            carritoItems.add(new DetalleVenta(producto, cantidadAñadir, producto.getPrecio()));
        }

        recalcularTotal();
        return true;
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

    private void recalcularTotal() {
        double sumaRaw = carritoItems.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        // Redondea a 2 decimales exactos
        this.totalAcumulado = Math.round(sumaRaw * 100.0) / 100.0;
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
