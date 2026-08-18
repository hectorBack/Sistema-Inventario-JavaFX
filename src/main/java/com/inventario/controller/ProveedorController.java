package com.inventario.controller;

import com.inventario.model.Proveedor;
import com.inventario.repository.Impl.ProveedorRepositoryImpl;
import com.inventario.repository.ProveedorRepository;
import com.inventario.util.Productos.KeyboardShortcutUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    @FXML
    private TextField txtBuscar;

    private final ProveedorRepository repo;

    public ProveedorController() {
        this(new ProveedorRepositoryImpl());
    }

    public ProveedorController(ProveedorRepository repo) {
        this.repo = repo;
    }

    private final ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Proveedor proveedorSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        configurarColumnas();
        listarProveedores();
        configurarAtajosTeclado();

        // Búsqueda en tiempo real
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarProveedores(newVal));
        }

        // Listener para selección en la tabla
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

        TableColumn<Proveedor, String> colNombre = new TableColumn<>("Nombre / Razon Social");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Proveedor, String> colContacto = new TableColumn<>("Contacto");
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));

        TableColumn<Proveedor, String> colTel = new TableColumn<>("Teléfono");
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        TableColumn<Proveedor, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Proveedor, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblProveedores.getColumns().setAll(colId, colNombre, colContacto, colTel, colEmail, colEstado);
    }

    private void listarProveedores() {
        listaProveedores.clear();
        listaProveedores.addAll(repo.listarTodos());
        tblProveedores.setItems(listaProveedores);
    }

    private void filtrarProveedores(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listarProveedores();
            return;
        }
        listaProveedores.clear();
        listaProveedores.addAll(repo.buscarConFiltro(criterio));
        tblProveedores.setItems(listaProveedores);
    }

    private void configurarAtajosTeclado() {
        KeyboardShortcutUtil.registrarAtajosCrud(
                tblProveedores,
                () -> {
                    limpiarCampos();
                    if (txtNombre != null) {
                        txtNombre.requestFocus();
                    } else {
                        txtNombre.requestFocus();
                    }
                },
                () -> onAgregar(null),
                () -> onActualizar(null),
                () -> onEliminar(null),
                () -> {
                    if (txtBuscar != null) {
                        txtBuscar.requestFocus();
                        txtBuscar.selectAll();
                    }
                },
                this::limpiarCampos
        );
    }

    @FXML
    void onAgregar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String contacto = txtContacto.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campos vacíos", "El nombre del proveedor o razón social es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        // Validación de Nombre duplicado
        if (repo.existeNombre(nombre, 0)) {
            mostrarAlerta("Registro duplicado", "Ya existe un proveedor registrado como '" + nombre + "'.", Alert.AlertType.WARNING);
            return;
        }

        // Validación de Email duplicado
        if (!email.isEmpty() && repo.existeEmail(email, 0)) {
            mostrarAlerta("Correo en uso", "El correo electrónico '" + email + "' ya está registrado con otro proveedor.", Alert.AlertType.WARNING);
            return;
        }

        Proveedor p = new Proveedor(nombre, contacto, telefono, email, cmbEstado.getValue());

        if (repo.guardar(p)) {
            mostrarAlerta("Éxito", "Proveedor registrado correctamente.", Alert.AlertType.INFORMATION);
            listarProveedores();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo registrar el proveedor en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (proveedorSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un proveedor de la tabla para actualizar.", Alert.AlertType.WARNING);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campos vacíos", "El nombre del proveedor no puede quedar vacío.", Alert.AlertType.WARNING);
            return;
        }

        int id = proveedorSeleccionado.getId();

        // Validar que el nuevo nombre no entre en colisión con otro registro
        if (repo.existeNombre(nombre, id)) {
            mostrarAlerta("Registro duplicado", "Ya existe otro proveedor registrado como '" + nombre + "'.", Alert.AlertType.WARNING);
            return;
        }

        // Validar que el nuevo email no esté en uso
        if (!email.isEmpty() && repo.existeEmail(email, id)) {
            mostrarAlerta("Correo en uso", "El correo electrónico '" + email + "' pertenece a otro proveedor.", Alert.AlertType.WARNING);
            return;
        }

        proveedorSeleccionado.setNombre(nombre);
        proveedorSeleccionado.setContacto(txtContacto.getText().trim());
        proveedorSeleccionado.setTelefono(txtTelefono.getText().trim());
        proveedorSeleccionado.setEmail(email);
        proveedorSeleccionado.setEstado(cmbEstado.getValue());

        if (repo.actualizar(proveedorSeleccionado)) {
            mostrarAlerta("Éxito", "Proveedor actualizado correctamente.", Alert.AlertType.INFORMATION);
            listarProveedores();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el proveedor.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (proveedorSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un proveedor de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        int id = proveedorSeleccionado.getId();

        // 1. Control de llaves foráneas: Verificar si existen productos asociados a este proveedor
        if (repo.tieneProductosAsociados(id)) {
            Alert confirmacionInactivo = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "El proveedor '" + proveedorSeleccionado.getNombre() + "' tiene productos asociados en el inventario.\n\n"
                    + "No se puede eliminar físicamente. ¿Deseas cambiar su estado a 'INACTIVO'?",
                    ButtonType.YES, ButtonType.NO
            );
            confirmacionInactivo.setTitle("Proveedor con Dependencias");
            confirmacionInactivo.setHeaderText(null);
            confirmacionInactivo.showAndWait();

            if (confirmacionInactivo.getResult() == ButtonType.YES) {
                if (repo.desactivar(id)) {
                    mostrarAlerta("Estado actualizado", "El proveedor ha sido marcado como INACTIVO.", Alert.AlertType.INFORMATION);
                    listarProveedores();
                    limpiarCampos();
                } else {
                    mostrarAlerta("Error", "No se pudo desactivar el proveedor.", Alert.AlertType.ERROR);
                }
            }
            return;
        }

        // 2. Eliminación física si no tiene vinculaciones
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de eliminar físicamente al proveedor '" + proveedorSeleccionado.getNombre() + "'?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            if (repo.eliminar(id)) {
                mostrarAlerta("Éxito", "Proveedor eliminado correctamente.", Alert.AlertType.INFORMATION);
                listarProveedores();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el proveedor.", Alert.AlertType.ERROR);
            }
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
