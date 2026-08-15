package com.inventario.util.Productos;

import com.inventario.model.Producto;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class ProductosTableUtil {

    @SuppressWarnings("unchecked")
    public static void configurarColumnasProductos(TableView<Producto> tblProductos) {
        TableColumn<Producto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colTipoVenta = new TableColumn<>("Tipo Venta");
        colTipoVenta.setCellValueFactory(new PropertyValueFactory<>("tipoVenta"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("P. Venta");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Double> colPrecioCompra = new TableColumn<>("P. Costo");
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));

        TableColumn<Producto, Double> colPorcentajeGanancia = new TableColumn<>("% Gan.");
        colPorcentajeGanancia.setCellValueFactory(new PropertyValueFactory<>("porcentajeGanancia"));

        TableColumn<Producto, Double> colPrecioMayoreo = new TableColumn<>("P. Mayoreo");
        colPrecioMayoreo.setCellValueFactory(new PropertyValueFactory<>("precioMayoreo"));

        TableColumn<Producto, Double> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Producto, Double> colStockMin = new TableColumn<>("Mín.");
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        TableColumn<Producto, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Producto, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(c -> c.getValue().getCategoria() != null
                ? c.getValue().getCategoria().nombreProperty()
                : new SimpleStringProperty("Sin Categoría"));

        TableColumn<Producto, String> colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(c -> c.getValue().getProveedor() != null
                ? c.getValue().getProveedor().nombreProperty()
                : new SimpleStringProperty("Sin Proveedor"));

        tblProductos.getColumns().setAll(
                colId, colCodigo, colNombre, colTipoVenta, colPrecio,
                colPrecioCompra, colPrecioMayoreo, colPorcentajeGanancia,
                colStock, colStockMin, colCategoria, colProveedor, colEstado
        );
    }

    public static <T> StringConverter<T> crearStringConverter(Function<T, String> extractorNombre) {
        return new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : extractorNombre.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }
}
