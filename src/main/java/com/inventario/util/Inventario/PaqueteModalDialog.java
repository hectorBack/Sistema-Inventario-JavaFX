package com.inventario.util.Inventario;

import com.inventario.model.DetallePaquete;
import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import com.inventario.util.audio.SoundUtil;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PaqueteModalDialog {

    public static Optional<ObservableList<DetallePaquete>> mostrar(
            ProductoRepository repository,
            ObservableList<DetallePaquete> listaActual) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Contenido del Paquete");
        dialog.setHeaderText("Agregue los productos que componen este paquete");

        ButtonType btnGuardarType = new ButtonType("Guardar Paquete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarType, ButtonType.CANCEL);

        VBox root = new VBox(12);
        root.setPrefWidth(550);
        root.setPadding(new Insets(15));

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Escanee o escriba código de barras...");
        txtCodigo.setPrefHeight(32);
        HBox.setHgrow(txtCodigo, Priority.ALWAYS);

        TextField txtCant = new TextField("1");
        txtCant.setPrefWidth(70);
        txtCant.setPrefHeight(32);

        Button btnAdd = new Button("Agregar");
        btnAdd.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAdd.setPrefHeight(32);

        Button btnRemove = new Button("Remover");
        btnRemove.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRemove.setPrefHeight(32);

        HBox formBox = new HBox(10, txtCodigo, txtCant, btnAdd, btnRemove);
        formBox.setAlignment(Pos.CENTER_LEFT);

        Label lblProductoEscaneado = new Label("Producto: Ninguno seleccionado");
        lblProductoEscaneado.setStyle("-fx-font-weight: bold; -fx-text-fill: #0284c7;");

        Label lblCostoEscaneado = new Label("Costo unitario: $0.00");
        lblCostoEscaneado.setStyle("-fx-font-weight: bold; -fx-text-fill: #16a34a;");

        HBox infoPreviewBox = new HBox(20, lblProductoEscaneado, lblCostoEscaneado);

        final Producto[] productoTemporal = new Producto[1];

        Runnable buscarYActualizarPreview = () -> {
            String cod = txtCodigo.getText().trim();
            if (!cod.isEmpty()) {
                Producto p = repository.buscarPorCodigoBarras(cod);
                if (p != null) {
                    productoTemporal[0] = p;
                    lblProductoEscaneado.setText("Producto: " + p.getNombre());
                    lblCostoEscaneado.setText(String.format(java.util.Locale.US, "Costo unitario: $%.2f", p.getPrecioCompra()));
                } else {
                    productoTemporal[0] = null;
                    lblProductoEscaneado.setText("Producto: No encontrado");
                    lblCostoEscaneado.setText("Costo unitario: $0.00");
                }
            } else {
                productoTemporal[0] = null;
                lblProductoEscaneado.setText("Producto: Ninguno seleccionado");
                lblCostoEscaneado.setText("Costo unitario: $0.00");
            }
        };

        txtCodigo.textProperty().addListener((obs, oldVal, newVal) -> buscarYActualizarPreview.run());

        TableView<DetallePaquete> tblModal = new TableView<>();
        tblModal.setPrefHeight(220);
        tblModal.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DetallePaquete, String> colNom = new TableColumn<>("Producto");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        TableColumn<DetallePaquete, Double> colC = new TableColumn<>("Cantidad");
        colC.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colC.setPrefWidth(90);

        TableColumn<DetallePaquete, Double> colSubCosto = new TableColumn<>("Subtotal Costo");
        colSubCosto.setCellValueFactory(new PropertyValueFactory<>("subtotalCosto"));
        colSubCosto.setPrefWidth(120);

        colSubCosto.setCellFactory(tc -> new TableCell<DetallePaquete, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format(java.util.Locale.US, "$%.2f", item));
                }
            }
        });

        tblModal.getColumns().addAll(colNom, colC, colSubCosto);

        ObservableList<DetallePaquete> listaTemp = FXCollections.observableArrayList(listaActual);
        tblModal.setItems(listaTemp);

        Runnable agregarAccion = () -> {
            buscarYActualizarPreview.run();

            if (productoTemporal[0] == null) {
                SoundUtil.emitirBeep(450, 180);
                return;
            }

            double cant = 1.0;
            try {
                cant = Double.parseDouble(txtCant.getText().trim());
                if (cant <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                return;
            }

            Producto p = productoTemporal[0];
            SoundUtil.emitirBeep(900, 120);

            boolean existe = false;
            for (DetallePaquete item : listaTemp) {
                if (item.getProducto().getId() == p.getId()) {
                    item.setCantidad(item.getCantidad() + cant);
                    tblModal.refresh();
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                listaTemp.add(new DetallePaquete(p, cant));
            }

            txtCodigo.clear();
            txtCant.setText("1");
            productoTemporal[0] = null;
            buscarYActualizarPreview.run();
            txtCodigo.requestFocus();
        };

        txtCodigo.setOnAction(e -> agregarAccion.run());
        btnAdd.setOnAction(e -> agregarAccion.run());
        btnRemove.setOnAction(e -> {
            DetallePaquete sel = tblModal.getSelectionModel().getSelectedItem();
            if (sel != null) {
                listaTemp.remove(sel);
            }
        });

        root.getChildren().addAll(formBox, infoPreviewBox, tblModal);
        dialog.getDialogPane().setContent(root);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardarType) {
            return Optional.of(listaTemp);
        }

        return Optional.empty();
    }
}
