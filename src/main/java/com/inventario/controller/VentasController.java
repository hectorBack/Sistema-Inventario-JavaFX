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
import java.util.ResourceBundle;
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

public class VentasController implements Initializable {

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

        // 3. Listener para rellenar automáticamente el precio al seleccionar un artículo
        cmbProducto.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtPrecio.setText(String.format("%.2f", newVal.getPrecio()));
            } else {
                txtPrecio.setText("0.00");
            }
        });
    }

    private void cargarCombosIniciales() {
        // Cargar Clientes Activos
        ObservableList<Cliente> clientes = FXCollections.observableArrayList(
                clienteRepository.listarTodos().stream()
                        .filter(c -> "ACTIVO".equalsIgnoreCase(c.getEstado()))
                        .collect(java.util.stream.Collectors.toList())
        );
        cmbCliente.setItems(clientes);

        // Formateador para que el ComboBox muestre el Nombre en lugar de la dirección de memoria
        cmbCliente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cmbCliente.setButtonCell(cmbCliente.getCellFactory().call(null));

        // Cargar Productos Disponibles (Con stock mayor a 0)
        ObservableList<Producto> productos = FXCollections.observableArrayList(
                productoRepository.listarTodos().stream()
                        .filter(p -> p.getStock() > 0)
                        .collect(java.util.stream.Collectors.toList())
        );
        cmbProducto.setItems(productos);

        cmbProducto.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (Stock: " + item.getStock() + ")");
            }
        });
        cmbProducto.setButtonCell(cmbProducto.getCellFactory().call(null));
    }

    private void configurarColumnasCarrito() {
        colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setStyle("-fx-alignment: CENTER;");

        colPrecioUnitario = new TableColumn<>("Precio Unit.");
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioUnitario.setStyle("-fx-alignment: CENTER-RIGHT;");

        colSubtotal = new TableColumn<>("Subtotal");
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

        // Validar si hay stock suficiente en almacén
        if (cantidad > prodSeleccionado.getStock()) {
            mostrarAlerta("Stock Insuficiente", "Solo quedan " + prodSeleccionado.getStock() + " unidades de este artículo.", Alert.AlertType.WARNING);
            return;
        }

        // Buscar si el producto ya está en el carrito para consolidarlo en una sola fila
        DetalleVenta itemExistente = null;
        for (DetalleVenta item : carritoItems) {
            if (item.getProducto().getId() == prodSeleccionado.getId()) {
                itemExistente = item;
                break;
            }
        }

        if (itemExistente != null) {
            // Validar stock combinado acumulativo
            if ((itemExistente.getCantidad() + cantidad) > prodSeleccionado.getStock()) {
                mostrarAlerta("Stock Insuficiente", "El acumulado en carrito supera el stock disponible (" + prodSeleccionado.getStock() + ").", Alert.AlertType.WARNING);
                return;
            }
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            tblCarrito.refresh(); // Refrescar celdas visuales de la tabla
        } else {
            // Agregar nuevo renglón de detalle
            DetalleVenta nuevoDetalle = new DetalleVenta(0, 0, prodSeleccionado, cantidad, prodSeleccionado.getPrecio());
            carritoItems.add(nuevoDetalle);
        }

        txtCantidad.clear();
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

        // Construir entidad de cabecera principal
        Venta nuevaVenta = new Venta(0, cliente, LocalDateTime.now(), totalAcumulado, "COMPLETADA");

        // Ejecutar proceso atómico transaccional en base de datos
        if (ventaRepository.registrarVenta(nuevaVenta, carritoItems)) {
            mostrarAlerta("Venta Procesada", "La venta se ha registrado exitosamente e inventarios actualizados.", Alert.AlertType.INFORMATION);
            limpiarPantallaCompleta();
            cargarCombosIniciales(); // Recargar existencias visuales actualizadas de productos
        } else {
            mostrarAlerta("Error Crítico", "Ocurrió un problema en la transacción SQL. Venta cancelada de forma segura.", Alert.AlertType.ERROR);
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
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
