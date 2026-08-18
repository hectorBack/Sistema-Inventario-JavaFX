package com.inventario.controller;

import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import com.inventario.repository.Impl.VentaRepositoryImpl;
import com.inventario.repository.VentaRepository;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DetalleVentaModalController implements Initializable {
    
    @FXML
    private Label lblTituloVenta;
    @FXML
    private Label lblInfoClienteFecha;
    @FXML
    private Label lblEstadoVenta;
    @FXML
    private Label lblTotalVenta;
    
    @FXML
    private TableView<DetalleVenta> tblDetalles;
    @FXML
    private TableColumn<DetalleVenta, String> colProducto;
    @FXML
    private TableColumn<DetalleVenta, Double> colCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML
    private TableColumn<DetalleVenta, Double> colSubtotal;
    
    @FXML
    private Button btnAnularVenta;
    
    private final VentaRepository ventaRepository;
    
    public DetalleVentaModalController() {
        this(new VentaRepositoryImpl());
    }
    
    public DetalleVentaModalController(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Venta venta;
    private boolean estadoCambiado = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }
    
    private void configurarTabla() {
        // 1. Enlace directo de la propiedad de texto
        colProducto.setCellValueFactory(cellData -> cellData.getValue().nombreProductoProperty());

        // 2. Enlace explícito de DoubleProperty a Object/Double
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        colCantidad.setStyle("-fx-alignment: CENTER;");
        colCantidad.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Formatea sin decimales si es entero (1, 2) o con decimales si es decimal (0.500)
                    setText(item % 1 == 0 ? String.format("%.0f", item) : String.format("%.3f", item));
                }
            }
        });

        // 3. Precio unitario
        colPrecio.setCellValueFactory(cellData -> cellData.getValue().precioUnitarioProperty().asObject());
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPrecio.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // 4. Subtotal recalculado dinámicamente según la propiedad
        colSubtotal.setCellValueFactory(cellData -> cellData.getValue().subtotalProperty().asObject());
        colSubtotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        colSubtotal.setCellFactory(tc -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });
    }
    
    public void initData(Venta venta) {
        this.venta = venta;
        lblTituloVenta.setText("Detalle de Venta #" + venta.getId());
        
        String clienteNombre = (venta.getCliente() != null) ? venta.getCliente().getNombre() : "Público en General";
        String fechaFormateada = (venta.getFecha() != null) ? venta.getFecha().format(formatter) : "N/A";
        lblInfoClienteFecha.setText("Cliente: " + clienteNombre + " | Fecha: " + fechaFormateada);
        
        lblTotalVenta.setText(String.format("$%.2f", venta.getTotal()));
        
        if ("CANCELADA".equalsIgnoreCase(venta.getEstado())) {
            lblEstadoVenta.setText("CANCELADA");
            lblEstadoVenta.setStyle("-fx-background-color: #fee2e2; -fx-padding: 4 12; -fx-background-radius: 12;");
            lblEstadoVenta.setTextFill(javafx.scene.paint.Color.web("#dc2626"));
            btnAnularVenta.setDisable(true);
        } else {
            lblEstadoVenta.setText("COMPLETADA");
            lblEstadoVenta.setStyle("-fx-background-color: #dcfce7; -fx-padding: 4 12; -fx-background-radius: 12;");
            lblEstadoVenta.setTextFill(javafx.scene.paint.Color.web("#15803d"));
            btnAnularVenta.setDisable(false);
        }
        
        List<DetalleVenta> detalles = ventaRepository.listarDetallesPorVenta(venta.getId());
        tblDetalles.setItems(FXCollections.observableArrayList(detalles));
    }
    
    @FXML
    void onAnularVenta(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Anulación");
        confirmacion.setHeaderText("¿Deseas anular la Venta #" + venta.getId() + "?");
        confirmacion.setContentText("Esta acción devolverá las existencias al inventario.");
        
        Optional<ButtonType> resp = confirmacion.showAndWait();
        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            if (ventaRepository.cancelarVenta(venta.getId())) {
                this.estadoCambiado = true;
                this.venta.setEstado("CANCELADA");
                initData(this.venta);
            }
        }
    }
    
    @FXML
    void onCerrar(ActionEvent event) {
        ((Stage) lblTituloVenta.getScene().getWindow()).close();
    }
    
    public boolean isEstadoCambiado() {
        return estadoCambiado;
    }
    
}
