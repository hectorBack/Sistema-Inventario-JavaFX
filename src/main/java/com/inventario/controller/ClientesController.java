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
    @FXML
    private TextField txtBuscar;

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
    private final ClienteRepository repository;

    public ClientesController(ClienteRepository repository) {
        this.repository = repository;
    }

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

        // 4. Búsqueda en tiempo real
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarClientes(newVal));
        }

        // 5. Listener para rellenar el formulario al seleccionar una fila
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
        TableColumn<Cliente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Cliente, String> colNombre = new TableColumn<>("Nombre / Razón Social");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Cliente, String> colRfc = new TableColumn<>("RFC");
        colRfc.setCellValueFactory(new PropertyValueFactory<>("rfc"));

        TableColumn<Cliente, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        TableColumn<Cliente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Cliente, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        TableColumn<Cliente, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblClientes.getColumns().setAll(colId, colNombre, colRfc, colTelefono, colEmail, colDireccion, colEstado);
    }

    private void listarClientes() {
        listaClientes.clear();
        listaClientes.addAll(repository.listarTodos());
        tblClientes.setItems(listaClientes);
    }

    private void filtrarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listarClientes();
            return;
        }
        listaClientes.clear();
        listaClientes.addAll(repository.buscarConFiltro(criterio));
        tblClientes.setItems(listaClientes);
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos(0)) {
            return;
        }

        Cliente nuevoCliente = new Cliente(
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

    @FXML
    void onActualizar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un cliente de la tabla para actualizar.", Alert.AlertType.WARNING);
            return;
        }

        int id = clienteSeleccionado.getId();

        if (!validarCampos(id)) {
            return;
        }

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

    @FXML
    void onEliminar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un cliente de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        int id = clienteSeleccionado.getId();

        // 1. Control de llaves foráneas: Verificar si el cliente tiene ventas asociadas
        if (repository.tieneVentasAsociadas(id)) {
            Alert confirmacionInactivo = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "El cliente '" + clienteSeleccionado.getNombre() + "' tiene transacciones/ventas vinculadas.\n\n"
                    + "No se puede eliminar físicamente. ¿Deseas cambiar su estado a 'INACTIVO'?",
                    ButtonType.YES, ButtonType.NO
            );
            confirmacionInactivo.setTitle("Cliente con Historial Comercial");
            confirmacionInactivo.setHeaderText(null);

            confirmacionInactivo.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (repository.desactivar(id)) {
                        mostrarAlerta("Estado Actualizado", "El cliente ha sido marcado como INACTIVO.", Alert.AlertType.INFORMATION);
                        limpiarFormulario();
                        listarClientes();
                    } else {
                        mostrarAlerta("Error", "No se pudo desactivar al cliente.", Alert.AlertType.ERROR);
                    }
                }
            });
            return;
        }

        // 2. Eliminación física si no tiene historial
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de eliminar físicamente al cliente '" + clienteSeleccionado.getNombre() + "'?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (repository.eliminar(id)) {
                    mostrarAlerta("Éxito", "Cliente eliminado correctamente.", Alert.AlertType.INFORMATION);
                    limpiarFormulario();
                    listarClientes();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar al cliente.", Alert.AlertType.ERROR);
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

    private boolean validarCampos(int idExcluir) {
        String nombre = txtNombre.getText().trim();
        String rfc = txtRfc.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isBlank()) {
            mostrarAlerta("Campo requerido", "El nombre o razón social es obligatorio.", Alert.AlertType.WARNING);
            return false;
        }

        // Validación de RFC duplicado
        if (!rfc.isBlank() && repository.existeRfc(rfc, idExcluir)) {
            mostrarAlerta("RFC Duplicado", "El RFC '" + rfc + "' ya pertenece a otro cliente registrado.", Alert.AlertType.WARNING);
            return false;
        }

        // Validación de Email duplicado
        if (!email.isBlank() && repository.existeEmail(email, idExcluir)) {
            mostrarAlerta("Correo Duplicado", "El correo electrónico '" + email + "' ya está asociado a otro cliente.", Alert.AlertType.WARNING);
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
