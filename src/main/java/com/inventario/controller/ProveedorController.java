package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.repository.Impl.ProveedorRepositoryImpl;
import com.inventario.repository.ProveedorRepository;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProveedorController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtContacto;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TableView<Proveedor> tblProveedores;

    private final ProveedorRepository repo = new ProveedorRepositoryImpl();
    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Proveedor proveedorSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        configurarColumnas();
        listarProveedores();

        // Listener para seleccionar filas de la tabla
        tblProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                proveedorSeleccionado = newSelection;
                txtNombre.setText(proveedorSeleccionado.getNombre());
                txtContacto.setText(proveedorSeleccionado.getContacto());
                txtTelefono.setText(proveedorSeleccionado.getTelefono());
                txtEmail.setText(proveedorSeleccionado.getEmail());
                cmbEstado.setValue(proveedorSeleccionado.getEstado());
            }
        });
    }

    private void configurarColumnas() {
        TableColumn<Proveedor, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Proveedor, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Proveedor, String> colContacto = new TableColumn<>("Contacto");
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));

        TableColumn<Proveedor, String> colTel = new TableColumn<>("Teléfono");
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        TableColumn<Proveedor, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblProveedores.getColumns().setAll(colId, colNombre, colContacto, colTel, colEstado);
    }

    private void listarProveedores() {
        listaProveedores.clear();
        listaProveedores.addAll(repo.listarTodos());
        tblProveedores.setItems(listaProveedores);
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre es obligatorio", Alert.AlertType.WARNING);
            return;
        }
        Proveedor p = new Proveedor(0, txtNombre.getText().trim(), txtContacto.getText().trim(), txtTelefono.getText().trim(), txtEmail.getText().trim(), cmbEstado.getValue());
        if (repo.guardar(p)) {
            listarProveedores();
            limpiarCampos();
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un proveedor de la tabla", Alert.AlertType.WARNING);
            return;
        }
        proveedorSeleccionado.setNombre(txtNombre.getText().trim());
        proveedorSeleccionado.setContacto(txtContacto.getText().trim());
        proveedorSeleccionado.setTelefono(txtTelefono.getText().trim());
        proveedorSeleccionado.setEmail(txtEmail.getText().trim());
        proveedorSeleccionado.setEstado(cmbEstado.getValue());

        if (repo.actualizar(proveedorSeleccionado)) {
            listarProveedores();
            limpiarCampos();
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un proveedor de la tabla", Alert.AlertType.WARNING);
            return;
        }
        if (repo.eliminar(proveedorSeleccionado.getId())) {
            listarProveedores();
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtEmail.clear();
        cmbEstado.setValue("ACTIVO");
        proveedorSeleccionado = null;
        tblProveedores.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String t, String m, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

}
