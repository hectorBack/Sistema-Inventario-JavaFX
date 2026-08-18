package com.inventario.controller;

import com.inventario.model.MovimientoInventario;
import com.inventario.model.Producto;
import com.inventario.repository.Impl.InventarioRepositoryImpl;
import com.inventario.repository.Impl.ProductoRepositoryImpl;
import com.inventario.repository.InventarioRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.util.Inventario.InventarioUIUtil;
import com.inventario.util.audio.SoundUtil;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InventarioController implements Initializable {

    @FXML
    private TextField txtCodigoBarras;
    @FXML
    private Label lblNombreProducto;
    @FXML
    private Label lblCantidadActual;
    @FXML
    private TextField txtCantidadAgregar;

    @FXML
    private TableView<MovimientoInventario> tblInventario;
    @FXML
    private TableColumn<MovimientoInventario, Integer> colId;
    @FXML
    private TableColumn<MovimientoInventario, String> colCodigo, colProducto, colTipo;
    @FXML
    private TableColumn<MovimientoInventario, Double> colCantidad;
    @FXML
    private TableColumn<MovimientoInventario, String> colFecha;

    private final InventarioRepository invRepository;
    private final ProductoRepository prodRepository;

    public InventarioController() {
        this(new InventarioRepositoryImpl(), new ProductoRepositoryImpl());
    }

    public InventarioController(InventarioRepository invRepository, ProductoRepository prodRepository) {
        this.invRepository = invRepository;
        this.prodRepository = prodRepository;
    }

    private final ObservableList<MovimientoInventario> listaMovimientos = FXCollections.observableArrayList();
    private Producto productoEncontrado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaMovimientos();
        cargarHistorialMovimientos();
    }

    @FXML
    void onBuscarProductoPorCodigo(ActionEvent event) {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }

        Producto p = prodRepository.buscarPorCodigoBarras(codigo);
        if (p != null) {
            SoundUtil.emitirBeep(900, 120);
            productoEncontrado = p;
            lblNombreProducto.setText(p.getNombre());
            lblCantidadActual.setText(String.format("%.2f", p.getStock()));
            txtCantidadAgregar.requestFocus();
        } else {
            SoundUtil.emitirBeep(450, 180);
            InventarioUIUtil.mostrarAlerta("No Encontrado", "No se encontró ningún producto con el código: " + codigo, Alert.AlertType.WARNING);
            limpiarFormularioCarga();
        }
    }

    @FXML
    void onAgregarCantidadInventario(ActionEvent event) {
        if (productoEncontrado == null) {
            InventarioUIUtil.mostrarAlerta("Advertencia", "Primero escanea o busca un producto válido.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidadAgregar.getText().trim());
            if (cantidad <= 0) {
                InventarioUIUtil.mostrarAlerta("Cantidad Inválida", "La cantidad a ingresar debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }

            if (invRepository.agregarStock(productoEncontrado.getId(), cantidad)) {
                InventarioUIUtil.mostrarAlerta("Éxito", "Se agregaron " + cantidad + " unidades a '" + productoEncontrado.getNombre() + "'", Alert.AlertType.INFORMATION);
                limpiarFormularioCarga();
                cargarHistorialMovimientos();
            } else {
                InventarioUIUtil.mostrarAlerta("Error", "No se pudo actualizar el inventario en la base de datos.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            InventarioUIUtil.mostrarAlerta("Dato Inválido", "Ingresa una cantidad numérica válida.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onVerProductosBajosInventario(ActionEvent event) {
        List<Producto> escasos = invRepository.obtenerProductosStockBajo();
        if (escasos.isEmpty()) {
            InventarioUIUtil.mostrarAlerta("Inventario Correcto", "No hay productos con stock por debajo del mínimo.", Alert.AlertType.INFORMATION);
        } else {
            StringBuilder sb = new StringBuilder("Productos que requieren reabastecimiento:\n\n");
            for (Producto p : escasos) {
                sb.append("• ").append(p.getNombre())
                        .append(" | Stock actual: ").append(p.getStock())
                        .append(" (Mín: ").append(p.getStockMinimo()).append(")\n");
            }
            InventarioUIUtil.mostrarAlerta("Stock Bajo", sb.toString(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    void onGenerarReporteInventario(ActionEvent event) {
        InventarioUIUtil.mostrarAlerta("Reporte", "Generando reporte de inventario...", Alert.AlertType.INFORMATION);
    }

    @SuppressWarnings("unchecked")
    private void configurarTablaMovimientos() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        colFecha.setCellValueFactory(c -> {
            LocalDateTime fecha = c.getValue().getFechaMovimiento();
            String fechaFormat = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
            return new javafx.beans.property.SimpleStringProperty(fechaFormat);
        });

        tblInventario.getColumns().setAll(colId, colCodigo, colProducto, colTipo, colCantidad, colFecha);
    }

    private void cargarHistorialMovimientos() {
        listaMovimientos.clear();
        listaMovimientos.addAll(invRepository.obtenerHistorialMovimientos());
        tblInventario.setItems(listaMovimientos);
    }

    private void limpiarFormularioCarga() {
        txtCodigoBarras.clear();
        lblNombreProducto.setText("-");
        lblCantidadActual.setText("0.00");
        txtCantidadAgregar.setText("0.0000");
        productoEncontrado = null;
        txtCodigoBarras.requestFocus();
    }

}
