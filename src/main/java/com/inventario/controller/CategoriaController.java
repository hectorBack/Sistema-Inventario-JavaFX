package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.Impl.CategoriaRepositoryImpl;
import com.inventario.util.Productos.KeyboardShortcutUtil;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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

public class CategoriaController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Categoria> tblCategorias;
    private TableColumn<Categoria, Integer> colId;
    private TableColumn<Categoria, String> colNombre;
    private TableColumn<Categoria, String> colEstado;

    private final CategoriaRepository repository;

    public CategoriaController() {
        this(new CategoriaRepositoryImpl());
    }

    public CategoriaController(CategoriaRepository repository) {
        this.repository = repository;
    }

    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();

    private Categoria categoriaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        cmbEstado.setValue("ACTIVO");

        configurarColumnas();
        listarCategorias();

        // Búsqueda dinámica en tiempo real
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
                filtrarCategorias(newVal);
            });
        }

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoriaSeleccionada = newSelection;
                txtNombre.setText(categoriaSeleccionada.getNombre());
                cmbEstado.setValue(categoriaSeleccionada.getEstado());
            }
        });

        // Registrar atajos de teclado una vez que la escena esté lista
        txtNombre.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                KeyboardShortcutUtil.registrarAtajosCrud(
                        txtNombre,
                        () -> {
                            limpiarFormulario();
                            txtNombre.requestFocus();
                        }, // accionNuevo (Ctrl + N)
                        () -> onAgregar(null), // accionGuardar (Ctrl + S)
                        () -> onActualizar(null), // accionActualizar (Ctrl + U)
                        () -> onEliminar(null), // accionEliminar (DELETE)
                        () -> {
                            if (txtBuscar != null) {
                                txtBuscar.requestFocus();
                                txtBuscar.selectAll();
                            }
                        }, // accionBuscar (Ctrl + F)
                        this::limpiarFormulario // accionCancelar (ESC)
                );
            }
        });
    }

    private void configurarColumnas() {
        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblCategorias.getColumns().setAll(colId, colNombre, colEstado);
    }

    private void listarCategorias() {
        listaCategorias.clear();
        listaCategorias.addAll(repository.listarTodasDTO().stream()
            .map(DTOMapper::toModel)
            .collect(Collectors.toList()));
        tblCategorias.setItems(listaCategorias);
    }

    private void filtrarCategorias(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            listarCategorias();
            return;
        }
        String filtro = criterio.trim().toLowerCase();
        ObservableList<Categoria> filtradas = FXCollections.observableArrayList();
        for (Categoria cat : repository.listarTodasDTO().stream().map(DTOMapper::toModel).collect(Collectors.toList())) {
            if (cat.getNombre().toLowerCase().contains(filtro)) {
                filtradas.add(cat);
            }
        }
        tblCategorias.setItems(filtradas);
    }

    @FXML
    void onAgregar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campos vacíos", "El nombre de la categoría es obligatorio", Alert.AlertType.WARNING);
            return;
        }

        // Validar que no exista un registro previo con el mismo nombre
        if (repository.existeNombre(nombre, 0)) {
            mostrarAlerta("Registro duplicado", "Ya existe una categoría registrada como '" + nombre + "'.", Alert.AlertType.WARNING);
            return;
        }

        Categoria nuevaCategoria = new Categoria(
                nombre,
                cmbEstado.getValue()
        );

        if (repository.guardarDTO(DTOMapper.toDTO(nuevaCategoria))) {
            mostrarAlerta("Éxito", "Categoría guardada correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            listarCategorias();
        } else {
            mostrarAlerta("Error", "No se pudo guardar la categoría", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una categoría de la tabla para actualizar", Alert.AlertType.WARNING);
            return;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campos vacíos", "El nombre de la categoría no puede quedar vacío", Alert.AlertType.WARNING);
            return;
        }

        // Validar que el nombre modificado no colisione con otra categoría existente
        if (repository.existeNombre(nombre, categoriaSeleccionada.getId())) {
            mostrarAlerta("Registro duplicado", "Ya existe otra categoría registrada como '" + nombre + "'.", Alert.AlertType.WARNING);
            return;
        }

        categoriaSeleccionada.setNombre(nombre);
        categoriaSeleccionada.setEstado(cmbEstado.getValue());

        if (repository.actualizarDTO(DTOMapper.toDTO(categoriaSeleccionada))) {
            mostrarAlerta("Éxito", "Categoría actualizada correctamente", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            listarCategorias();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la categoría", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (categoriaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una categoría de la tabla para eliminar", Alert.AlertType.WARNING);
            return;
        }

        int id = categoriaSeleccionada.getId();

        // 1. Manejo de Integridad Referencial: Si tiene productos vinculados
        if (repository.tieneProductosAsociados(id)) {
            Alert confirmacionBaja = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "La categoría '" + categoriaSeleccionada.getNombre() + "' tiene productos vinculados.\n\n"
                    + "Por seguridad no puede eliminarse físicamente. ¿Deseas cambiar su estado a 'INACTIVO'?",
                    ButtonType.YES, ButtonType.NO
            );
            confirmacionBaja.setTitle("Categoría en uso");
            confirmacionBaja.setHeaderText(null);
            confirmacionBaja.showAndWait();

            if (confirmacionBaja.getResult() == ButtonType.YES) {
                if (repository.desactivar(id)) {
                    mostrarAlerta("Estado actualizado", "La categoría ha sido marcada como INACTIVA.", Alert.AlertType.INFORMATION);
                    limpiarFormulario();
                    listarCategorias();
                } else {
                    mostrarAlerta("Error", "No se pudo desactivar la categoría.", Alert.AlertType.ERROR);
                }
            }
            return;
        }

        // 2. Eliminación física si no existen relaciones en la BD
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de eliminar físicamente esta categoría?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            if (repository.eliminar(id)) {
                mostrarAlerta("Éxito", "Categoría eliminada correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarCategorias();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la categoría de la base de datos", Alert.AlertType.ERROR);
            }
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        cmbEstado.setValue("ACTIVO");
        categoriaSeleccionada = null;
        tblCategorias.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
