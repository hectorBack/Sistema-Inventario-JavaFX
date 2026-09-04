package com.inventario.controller;

import com.inventario.model.ArticulosPrecargados;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.ArticulosPrecargadosRepository;
import com.inventario.repository.Impl.ArticulosPrecargadosRepositoryImpl;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ArticulosPrecargadosController implements Initializable {

    // --- COMPONENTES: SELECCIÓN DE ARCHIVO ---
    @FXML
    private TextField txtRutaArchivo;
    @FXML
    private RadioButton rbOmitirDuplicados;
    @FXML
    private RadioButton rbActualizarDuplicados;
    @FXML
    private ToggleGroup groupDuplicados;

    // --- COMPONENTES: TABLA DE PREVISUALIZACIÓN ---
    @FXML
    private TableView<ArticulosPrecargados> tblArticulos;
    @FXML
    private TableColumn<ArticulosPrecargados, String> colCodigo;
    @FXML
    private TableColumn<ArticulosPrecargados, String> colNombre;
    @FXML
    private TableColumn<ArticulosPrecargados, String> colDescripcion;
    @FXML
    private TableColumn<ArticulosPrecargados, Double> colPrecioCompra;
    @FXML
    private TableColumn<ArticulosPrecargados, Double> colPrecioVenta;
    @FXML
    private TableColumn<ArticulosPrecargados, Integer> colStock;
    @FXML
    private TableColumn<ArticulosPrecargados, String> colCategoria;

    // --- COMPONENTES: RESUMEN Y BARRAS ---
    @FXML
    private Label lblTotalCargados;
    @FXML
    private ComboBox<String> cbGiroComercial;

    // --- REPOSITORIO Y DATOS ---
    private final ArticulosPrecargadosRepository repository = new ArticulosPrecargadosRepositoryImpl();
    private final ObservableList<ArticulosPrecargados> listaArticulos = FXCollections.observableArrayList();
    private File archivoSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarOpcionesGiros();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockInicial"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        tblArticulos.setItems(listaArticulos);
    }

    private void cargarOpcionesGiros() {
        cbGiroComercial.setItems(FXCollections.observableArrayList("Abarrotes", "Papelería"));
    }

    // --- ACCIONES DE ARCHIVOS Y PLANTILLAS ---
    @FXML
    private void accionSeleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV de artículos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));

        archivoSeleccionado = fileChooser.showOpenDialog(getStage());

        if (archivoSeleccionado != null) {
            txtRutaArchivo.setText(archivoSeleccionado.getAbsolutePath());

            // Cargar y previsualizar en segundo plano
            Task<List<com.inventario.model.DTOs.ArticulosPrecargadosDTO>> tarea = new Task<>() {
                @Override
                protected List<com.inventario.model.DTOs.ArticulosPrecargadosDTO> call() {
                    return repository.leerArticulosDesdeArchivoDTO(archivoSeleccionado);
                }
            };

            tarea.setOnSucceeded(e -> {
                listaArticulos.setAll(tarea.getValue().stream().map(DTOMapper::toModel).collect(Collectors.toList()));
                lblTotalCargados.setText(String.valueOf(listaArticulos.size()));
            });

            new Thread(tarea).start();
        }
    }

    @FXML
    private void accionDescargarPlantilla() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Plantilla de Ejemplo CSV");
        fileChooser.setInitialFileName("plantilla_articulos.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));

        File destino = fileChooser.showSaveDialog(getStage());

        if (destino != null) {
            boolean exito = repository.generarPlantillaEjemplo(destino);
            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Plantilla Generada", "La plantilla CSV fue guardada en:\n" + destino.getAbsolutePath());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo generar el archivo de plantilla.");
            }
        }
    }

    // --- ACCIÓN DE IMPORTACIÓN MASIVA ---
    @FXML
    private void accionProcesarEImportar() {
        if (listaArticulos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin datos", "No hay artículos cargados en la tabla para importar.");
            return;
        }

        int modoDuplicados = rbActualizarDuplicados.isSelected() ? 1 : 0;

        Task<Integer> tareaImportacion = new Task<>() {
            @Override
            protected Integer call() {
                return repository.guardarArticulosEnLoteDTO(listaArticulos.stream()
                    .map(DTOMapper::toDTO).collect(Collectors.toList()), modoDuplicados);
            }
        };

        tareaImportacion.setOnSucceeded(e -> {
            int insertados = tareaImportacion.getValue();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Importación Finalizada", "Se importaron / procesaron " + insertados + " artículos con éxito en la base de datos.");
            listaArticulos.clear();
            txtRutaArchivo.clear();
            lblTotalCargados.setText("0");
        });

        tareaImportacion.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Importación", "Ocurrió un error al intentar guardar los artículos en lote.");
        });

        new Thread(tareaImportacion).start();
    }

    // --- ACCIÓN DE CARGA POR GIRO COMERCIAL ---
    @FXML
    private void accionCargarCatalogoBase() {
        String giro = cbGiroComercial.getValue();
        if (giro == null || giro.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Por favor selecciona un giro comercial.");
            return;
        }

        List<ArticulosPrecargados> catalogoBase = repository.obtenerCatalogoBasePorGiroDTO(giro).stream()
            .map(DTOMapper::toModel)
            .collect(Collectors.toList());
        listaArticulos.setAll(catalogoBase);
        lblTotalCargados.setText(String.valueOf(listaArticulos.size()));
    }

    // --- MÉTODOS AUXILIARES ---
    private Stage getStage() {
        return (Stage) txtRutaArchivo.getScene().getWindow();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
