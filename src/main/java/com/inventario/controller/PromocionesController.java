package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.model.Promocion;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.Impl.PromocionRepositoryImpl;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.PromocionRepository;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

public class PromocionesController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNombrePromocion;
    @FXML
    private TextField txtCodigoBarras;
    @FXML
    private Label lblNombreProducto;
    @FXML
    private TextField txtCantidadDesde;
    @FXML
    private TextField txtCantidadHasta;
    @FXML
    private TextField txtPrecioUnitario;
    @FXML
    private Label lblPrecioNormal;
    @FXML
    private Label lblPrecioCosto;
    @FXML
    private Button btnGuardarPromocion;
    @FXML
    private Label lblPromocionesVigentes;
    @FXML
    private TableView<Promocion> tblPromociones;
    @FXML
    private TableColumn<Promocion, String> colNombre;
    @FXML
    private TableColumn<Promocion, String> colCodigo;
    @FXML
    private TableColumn<Promocion, Number> colDesde;
    @FXML
    private TableColumn<Promocion, Number> colHasta;
    @FXML
    private TableColumn<Promocion, Number> colPrecioPromocion;
    @FXML
    private TableColumn<Promocion, String> colAccion;
    @FXML
    private Button btnEliminar;

    private final PromocionRepository repository = new PromocionRepositoryImpl();
    private final ProductoRepository productoRepository = new ProductoRepositoryImpl();
    private final ObservableList<Promocion> promociones = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtCantidadDesde.setText("0.00");
        txtCantidadHasta.setText("0.00");
        txtPrecioUnitario.setText("0.00");
        lblPrecioNormal.setText("Precio Normal: 0.00");
        lblPrecioCosto.setText("Precio Costo: 0.00");

        configurarTabla();
        cargarPromociones();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarrasProducto"));
        colDesde.setCellValueFactory(new PropertyValueFactory<>("cantidadDesde"));
        colHasta.setCellValueFactory(new PropertyValueFactory<>("cantidadHasta"));
        colPrecioPromocion.setCellValueFactory(new PropertyValueFactory<>("precioPromocion"));
        tblPromociones.setItems(promociones);
    }

    private void cargarPromociones() {
        promociones.clear();
        List<Promocion> lista = repository.listarActivasDTO().stream()
            .map(DTOMapper::toModel)
            .collect(Collectors.toList());
        if (lista != null) {
            promociones.addAll(lista);
        }
        lblPromocionesVigentes.setText("Promociones vigentes: " + promociones.size());
    }

    @FXML
    private void buscarProductoPorCodigo(ActionEvent event) {
        String codigo = txtCodigoBarras.getText() == null ? "" : txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) {
            lblNombreProducto.setText("");
            lblPrecioNormal.setText("Precio Normal: 0.00");
            lblPrecioCosto.setText("Precio Costo: 0.00");
            return;
        }

        Producto producto = DTOMapper.toModel(productoRepository.buscarPorCodigoBarrasDTO(codigo));
        if (producto != null) {
            lblNombreProducto.setText(producto.getNombre());
            lblNombreProducto.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
            lblPrecioNormal.setText(String.format(Locale.US, "Precio Normal: %.2f", producto.getPrecio()));
            lblPrecioCosto.setText(String.format(Locale.US, "Precio Costo: %.2f", producto.getPrecioCompra()));

            if (txtPrecioUnitario.getText() == null || txtPrecioUnitario.getText().trim().isEmpty() || "0.00".equals(txtPrecioUnitario.getText().trim())) {
                txtPrecioUnitario.setText(String.format(Locale.US, "%.2f", producto.getPrecio()));
            }
        } else {
            lblNombreProducto.setText("Producto no encontrado");
            lblNombreProducto.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            lblPrecioNormal.setText("Precio Normal: 0.00");
            lblPrecioCosto.setText("Precio Costo: 0.00");
        }
    }

    @FXML
    private void guardarPromocion(ActionEvent event) {
        try {
            String nombre = txtNombrePromocion.getText().trim();
            String codigo = txtCodigoBarras.getText().trim();
            double cantidadDesde = Double.parseDouble(txtCantidadDesde.getText().trim().replace(",", "."));
            double cantidadHasta = Double.parseDouble(txtCantidadHasta.getText().trim().replace(",", "."));
            double precioUnitario = Double.parseDouble(txtPrecioUnitario.getText().trim().replace(",", "."));

            if (nombre.isEmpty() || codigo.isEmpty()) {
                mostrarAlerta("Validación", "Por favor complete el nombre y código de barras.", Alert.AlertType.WARNING);
                return;
            }

            if (cantidadDesde >= cantidadHasta) {
                mostrarAlerta("Validación", "La cantidad 'desde' debe ser menor que 'hasta'.", Alert.AlertType.WARNING);
                return;
            }

            // Verificar si hay conflicto de rangos
            String conflicto = repository.verificarConflictoDeRango(codigo, cantidadDesde, cantidadHasta, -1);
            if (conflicto != null) {
                mostrarAlerta("Promoción en Conflicto", conflicto, Alert.AlertType.ERROR);
                return;
            }

            Promocion promo = new Promocion();
            promo.setNombre(nombre);
            promo.setCodigoBarrasProducto(codigo);
            promo.setCantidadDesde(cantidadDesde);
            promo.setCantidadHasta(cantidadHasta);
            promo.setPrecioUnitario(precioUnitario);
            promo.setPrecioPromocion(promo.getPrecioUnitario());

            Producto producto = DTOMapper.toModel(productoRepository.buscarPorCodigoBarrasDTO(promo.getCodigoBarrasProducto()));
            if (producto != null) {
                promo.setPrecioNormal(producto.getPrecio());
                promo.setPrecioCosto(producto.getPrecioCompra());
            }
            promo.setEstado("ACTIVA");

            repository.guardarDTO(DTOMapper.toDTO(promo));
            mostrarAlerta("Exito", "Promoción guardada correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarPromociones();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Verifique que los valores numéricos sean válidos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarPromocion(ActionEvent event) {
        Promocion seleccionada = tblPromociones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Advertencia", "Por favor selecciona una promoción para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar eliminación");
        alertaConfirmacion.setHeaderText(null);
        alertaConfirmacion.setContentText("¿Estás seguro de que deseas borrar esta promoción?\n\n" +
                "Nombre: " + seleccionada.getNombre() + "\n" +
                "Producto: " + seleccionada.getCodigoBarrasProducto() + "\n" +
                "Rango: " + String.format("%.2f", seleccionada.getCantidadDesde()) + " - " + 
                String.format("%.2f", seleccionada.getCantidadHasta()) + " kg/unid\n" +
                "Precio: $" + String.format("%.2f", seleccionada.getPrecioPromocion()));
        
        // Aumentar tamaño del texto del contenido
        Label contentLabel = (Label) alertaConfirmacion.getDialogPane().getContent();
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        }

        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            repository.eliminar(seleccionada.getId());
            mostrarAlerta("Éxito", "Promoción eliminada correctamente.", Alert.AlertType.INFORMATION);
            cargarPromociones();
        }
    }

    private void limpiarFormulario() {
        txtNombrePromocion.clear();
        txtCodigoBarras.clear();
        txtCantidadDesde.setText("0.00");
        txtCantidadHasta.setText("0.00");
        txtPrecioUnitario.setText("0.00");
        lblNombreProducto.setText("");
        lblNombreProducto.setStyle("-fx-text-fill: #475569; -fx-font-weight: normal;");
        lblPrecioNormal.setText("Precio Normal: 0.00");
        lblPrecioCosto.setText("Precio Costo: 0.00");
        tblPromociones.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        
        // Aumentar tamaño del texto
        Label contentLabel = (Label) alert.getDialogPane().getContent();
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-font-size: 12; -fx-padding: 15; -fx-text-alignment: center; -fx-wrap-text: true;");
        }
        
        alert.showAndWait();
    }
}
