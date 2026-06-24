package com.inventario.controller;

import com.inventario.model.Cliente;
import com.inventario.repository.ClienteRepository;
import com.inventario.repository.Impl.ClienteRepositoryImpl;
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

public class ClientesController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtRfc;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<String> cmbEstado;

    // Configuración de la Tabla y Columnas
    @FXML
    private TableView<Cliente> tblClientes;
    private TableColumn<Cliente, Integer> colId;
    private TableColumn<Cliente, String> colNombre;
    private TableColumn<Cliente, String> colRfc;
    private TableColumn<Cliente, String> colTelefono;
    private TableColumn<Cliente, String> colEmail;
    private TableColumn<Cliente, String> colDireccion;
    private TableColumn<Cliente, String> colEstado;

    // Dependencias y Listas
    private final ClienteRepository repository = new ClienteRepositoryImpl();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    private Cliente clienteSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Inicializar combo de estado
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        // 2. Configurar estructura de la tabla
        configurarColumnas();

        // 3. Cargar datos de la BD
        listarClientes();

        // 4. Listener para rellenar el formulario al seleccionar una fila
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                txtNombre.setText(clienteSeleccionado.getNombre());
                txtRfc.setText(clienteSeleccionado.getRfc());
                txtTelefono.setText(clienteSeleccionado.getTelefono());
                txtEmail.setText(clienteSeleccionado.getEmail());
                txtDireccion.setText(clienteSeleccionado.getDireccion());
                cmbEstado.setValue(clienteSeleccionado.getEstado());
            }
        });
    }

    private void configurarColumnas() {
        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        colNombre = new TableColumn<>("Nombre / Razón Social");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colRfc = new TableColumn<>("RFC");
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));

        colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Vinculamos todas las columnas limpiando residuos previos
        tblClientes.getColumns().setAll(colId, colNombre, colRfc, colTelefono, colEmail, colDireccion, colEstado);
    }

    private void listarClientes() {
        listaClientes.clear();
        listaClientes.addAll(repository.listarTodos());
        tblClientes.setItems(listaClientes);
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (validarCampos()) {
            Cliente nuevoCliente = new Cliente(
                    0, // El ID lo autogenera serial en PostgreSQL
                    txtNombre.getText().trim(),
                    txtRfc.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtEmail.getText().trim(),
                    txtDireccion.getText().trim(),
                    cmbEstado.getValue()
            );

            if (repository.guardar(nuevoCliente)) {
                mostrarAlerta("Éxito", "Cliente registrado correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarClientes();
            } else {
                mostrarAlerta("Error", "No se pudo registrar al cliente.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un cliente de la tabla para actualizar.", Alert.AlertType.WARNING);
            return;
        }

        if (validarCampos()) {
            clienteSeleccionado.setNombre(txtNombre.getText().trim());
            clienteSeleccionado.setRfc(txtRfc.getText().trim());
            clienteSeleccionado.setTelefono(txtTelefono.getText().trim());
            clienteSeleccionado.setEmail(txtEmail.getText().trim());
            clienteSeleccionado.setDireccion(txtDireccion.getText().trim());
            clienteSeleccionado.setEstado(cmbEstado.getValue());

            if (repository.actualizar(clienteSeleccionado)) {
                mostrarAlerta("Éxito", "Datos del cliente actualizados con éxito.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarClientes();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la información del cliente.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un cliente de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Alerta de confirmación opcional para evitar accidentes
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que deseas eliminar este cliente?", ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (repository.eliminar(clienteSeleccionado.getId())) {
                    mostrarAlerta("Éxito", "Cliente eliminado de forma definitiva.", Alert.AlertType.INFORMATION);
                    limpiarFormulario();
                    listarClientes();
                } else {
                    mostrarAlerta("Error", "No se puede eliminar el cliente (puede estar asociado a registros de transacciones).", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtRfc.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        cmbEstado.setValue("ACTIVO");
        clienteSeleccionado = null;
        tblClientes.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isBlank()) {
            mostrarAlerta("Campo requerido", "El nombre o razón social es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
