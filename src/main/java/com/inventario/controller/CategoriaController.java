package com.inventario.controller;

import com.inventario.model.Categoria;
import com.inventario.repository.CategoriaRepository;
import com.inventario.repository.Impl.CategoriaRepositoryImpl;
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

public class CategoriaController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbEstado;
    
    @FXML private TableView<Categoria> tblCategorias;
    private TableColumn<Categoria, Integer> colId;
    private TableColumn<Categoria, String> colNombre;
    private TableColumn<Categoria, String> colEstado;

    private final CategoriaRepository repository = new CategoriaRepositoryImpl();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    
    private Categoria categoriaSeleccionada;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEstado.setValue("Activo");

        configurarColumnas();
        listarCategorias();

        tblCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoriaSeleccionada = newSelection;
                txtNombre.setText(categoriaSeleccionada.getNombre());
                cmbEstado.setValue(categoriaSeleccionada.getEstado());
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
        listaCategorias.addAll(repository.listarTodas());
        tblCategorias.setItems(listaCategorias);
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campos vacíos", "El nombre de la categoría es obligatorio", Alert.AlertType.WARNING);
            return;
        }

        Categoria nuevaCategoria = new Categoria(
                txtNombre.getText().trim(),
                cmbEstado.getValue()
        );

        if (repository.guardar(nuevaCategoria)) {
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

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campos vacíos", "El nombre de la categoría no puede quedar vacío", Alert.AlertType.WARNING);
            return;
        }

        categoriaSeleccionada.setNombre(txtNombre.getText().trim());
        categoriaSeleccionada.setEstado(cmbEstado.getValue());

        if (repository.actualizar(categoriaSeleccionada)) {
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

        // Alerta de confirmación debido al impacto sobre la restricción de llave foránea
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar esta categoría? Los productos asociados se quedarán sin categoría.", ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            if (repository.eliminar(categoriaSeleccionada.getId())) {
                mostrarAlerta("Éxito", "Categoría eliminada correctamente", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                listarCategorias();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar la categoría", Alert.AlertType.ERROR);
            }
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        cmbEstado.setValue("Activo");
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
