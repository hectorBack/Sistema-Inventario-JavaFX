package com.inventario.util.Ventas;

import com.inventario.model.DetalleVenta;
import com.inventario.util.FormatoMonedaUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class VentasTablaUtil {

    public static void configurarColumnasCarrito(TableView<DetalleVenta> tblCarrito) {
        tblCarrito.setStyle("-fx-font-size: 16px;");
        TableColumn<DetalleVenta, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        TableColumn<DetalleVenta, String> colUnidad = new TableColumn<>("Unidad");
        colUnidad.setCellValueFactory(cell -> {
            DetalleVenta detalle = cell.getValue();
            String unidad = detalle.getProducto() == null ? "PZA" : detalle.getProducto().getUnidadMedida();
            return new SimpleStringProperty(unidad);
        });

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
                    setText(FormatoMonedaUtil.formatear(item));
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
                    setText(FormatoMonedaUtil.formatear(item));
                }
            }
        });

        tblCarrito.getColumns().setAll(colProducto, colUnidad, colCantidad, colPrecioUnitario, colSubtotal);
    }

}
