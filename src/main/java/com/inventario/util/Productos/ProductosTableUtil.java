package com.inventario.util.Productos;

import com.inventario.model.Producto;
import com.inventario.util.FormatoMonedaUtil;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class ProductosTableUtil {

    @SuppressWarnings("unchecked")
    public static void configurarColumnasProductos(
            TableView<Producto> tblProductos,
            Consumer<Producto> onEditar,
            Consumer<Producto> onEliminar
    ) {
        TableColumn<Producto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colTipoVenta = new TableColumn<>("Tipo Venta");
        colTipoVenta.setCellValueFactory(new PropertyValueFactory<>("tipoVenta"));

        TableColumn<Producto, String> colUnidadMedida = new TableColumn<>("Unidad");
        colUnidadMedida.setCellValueFactory(new PropertyValueFactory<>("unidadMedida"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("P. Venta");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        configurarColumnaMoneda(colPrecio);

        TableColumn<Producto, Double> colPrecioCompra = new TableColumn<>("P. Costo");
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        configurarColumnaMoneda(colPrecioCompra);

        TableColumn<Producto, Double> colPorcentajeGanancia = new TableColumn<>("% Gan.");
        colPorcentajeGanancia.setCellValueFactory(new PropertyValueFactory<>("porcentajeGanancia"));

        TableColumn<Producto, Double> colPrecioMayoreo = new TableColumn<>("P. Mayoreo");
        colPrecioMayoreo.setCellValueFactory(new PropertyValueFactory<>("precioMayoreo"));
        configurarColumnaMoneda(colPrecioMayoreo);

        TableColumn<Producto, Double> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Producto, Double> colStockMin = new TableColumn<>("Mín.");
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        TableColumn<Producto, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Producto, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(c -> {
            Producto p = c.getValue();
            String nombreCat = (p != null && p.getCategoria() != null && p.getCategoria().getNombre() != null && !p.getCategoria().getNombre().isBlank())
                    ? p.getCategoria().getNombre()
                    : "Sin Categoría";
            return new SimpleStringProperty(nombreCat);
        });

        TableColumn<Producto, String> colProveedor = new TableColumn<>("Proveedor");
        colProveedor.setCellValueFactory(c -> {
            Producto p = c.getValue();
            String nombreProv = (p != null && p.getProveedor() != null && p.getProveedor().getNombre() != null && !p.getProveedor().getNombre().isBlank())
                    ? p.getProveedor().getNombre()
                    : "Sin Proveedor";
            return new SimpleStringProperty(nombreProv);
        });

        // Columna de Acciones con Botones Integrados
        TableColumn<Producto, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setSortable(false);
        colAcciones.setStyle("-fx-alignment: CENTER;");
        colAcciones.setCellFactory(tc -> new TableCell<>() {
            private final Button btnEdit = new Button("Editar");
            private final Button btnDel = new Button("Eliminar");
            private final HBox container = new HBox(6, btnEdit, btnDel);

            {
                btnEdit.getStyleClass().addAll("action-button", "action-secondary");
                btnDel.getStyleClass().addAll("action-button", "action-danger");

                btnEdit.setStyle("-fx-padding: 3 8; -fx-font-size: 11px;");
                btnDel.setStyle("-fx-padding: 3 8; -fx-font-size: 11px;");

                container.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    Producto p = getTableView().getItems().get(getIndex());
                    if (onEditar != null && p != null) {
                        onEditar.accept(p);
                    }
                });

                btnDel.setOnAction(e -> {
                    Producto p = getTableView().getItems().get(getIndex());
                    if (onEliminar != null && p != null) {
                        onEliminar.accept(p);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        tblProductos.getColumns().setAll(
                colId, colCodigo, colNombre, colTipoVenta, colUnidadMedida, colPrecio,
                colPrecioCompra, colPrecioMayoreo, colPorcentajeGanancia,
                colStock, colStockMin, colCategoria, colProveedor, colEstado, colAcciones
        );
    }

    private static void configurarColumnaMoneda(TableColumn<Producto, Double> columna) {
        columna.setStyle("-fx-alignment: CENTER-RIGHT;");
        columna.setCellFactory(tc -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : FormatoMonedaUtil.formatear(item));
            }
        });
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
