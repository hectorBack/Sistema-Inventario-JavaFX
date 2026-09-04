package com.inventario.controller;

import com.inventario.model.ConexionConfig;
import com.inventario.model.InformacionBD;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.DatabaseRepository;
import com.inventario.repository.Impl.DatabaseRepositoryImpl;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ConfiguracionBaseDatosController implements Initializable {

    // --- COMPONENTES: PARÁMETROS DE CONEXIÓN ---
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtPuerto;
    @FXML
    private TextField txtBaseDatos;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtRutaPgDump;

    // --- COMPONENTES: INFORMACIÓN / DIAGNÓSTICO ---
    @FXML
    private Label lblEstadoConexion;
    @FXML
    private Label lblMotorVersion;
    @FXML
    private Label lblTamanioBD;
    @FXML
    private Label lblTotalProductos;
    @FXML
    private Label lblTotalVentas;
    @FXML
    private Label lblTotalCajeros;

    // --- REPOSITORIO Y DATOS ---
    private final DatabaseRepository databaseRepository = new DatabaseRepositoryImpl();
    private ConexionConfig conexionConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarConfiguracionUI();
        cargarDiagnosticoUI();
    }

    // --- LÓGICA DE CARGA DE DATOS ---
    private void cargarConfiguracionUI() {
        conexionConfig = DTOMapper.toModel(databaseRepository.cargarConfiguracionDTO());

        txtHost.setText(conexionConfig.getHost());
        txtPuerto.setText(conexionConfig.getPuerto());
        txtBaseDatos.setText(conexionConfig.getBaseDatos());
        txtUsuario.setText(conexionConfig.getUsuario());
        txtContrasena.setText(conexionConfig.getContrasena());
        txtRutaPgDump.setText(conexionConfig.getRutaPgDump());
    }

    private void cargarDiagnosticoUI() {
        InformacionBD info = DTOMapper.toModel(databaseRepository.obtenerDiagnosticoBDDTO());

        if (info.isEstadoConexion()) {
            lblEstadoConexion.setText("CONECTADO");
            lblEstadoConexion.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else {
            lblEstadoConexion.setText("DESCONECTADO");
            lblEstadoConexion.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }

        lblMotorVersion.setText(info.getMotorVersion());
        lblTamanioBD.setText(info.getTamanioBD());
        lblTotalProductos.setText(info.getTotalProductos());
        lblTotalVentas.setText(info.getTotalVentas());
        lblTotalCajeros.setText(info.getTotalCajeros());
    }

    private ConexionConfig extraerConfiguracionFormulario() {
        return new ConexionConfig(
                txtHost.getText().trim(),
                txtPuerto.getText().trim(),
                txtBaseDatos.getText().trim(),
                txtUsuario.getText().trim(),
                txtContrasena.getText().trim(),
                txtRutaPgDump.getText().trim()
        );
    }

    // --- ACCIONES DE PARÁMETROS DE CONEXIÓN ---
    @FXML
    private void accionProbarConexion() {
        ConexionConfig config = extraerConfiguracionFormulario();
        boolean exitoso = databaseRepository.probarConexionDTO(DTOMapper.toDTO(config));

        if (exitoso) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Conexión Exitosa", "Se logró establecer comunicación con la base de datos de PostgreSQL.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a PostgreSQL. Verifica el host, puerto, usuario y contraseña.");
        }
    }

    @FXML
    private void accionGuardarConfiguracion() {
        ConexionConfig config = extraerConfiguracionFormulario();

        if (databaseRepository.guardarConfiguracionDTO(DTOMapper.toDTO(config))) {
            conexionConfig = config;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La configuración fue guardada en 'config.properties'.");
            cargarDiagnosticoUI();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los datos en el archivo de propiedades.");
        }
    }

    @FXML
    private void accionExaminarPgDump() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccionar carpeta 'bin' de PostgreSQL");
        File directorioSeleccionado = dc.showDialog(getStage());

        if (directorioSeleccionado != null) {
            txtRutaPgDump.setText(directorioSeleccionado.getAbsolutePath());
        }
    }

    // --- ACCIONES DE RESPALDO Y RESTAURACIÓN ---
    @FXML
    private void accionGenerarRespaldo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar copia de seguridad de PostgreSQL");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PostgreSQL Backup (*.backup)", "*.backup"));

        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("backup_inventario_" + fechaActual + ".backup");

        File archivoDestino = fileChooser.showSaveDialog(getStage());

        if (archivoDestino != null) {
            ConexionConfig config = extraerConfiguracionFormulario();

            // Ejecución en segundo plano para no congelar la UI de JavaFX
            Task<Boolean> tarea = new Task<>() {
                @Override
                protected Boolean call() {
                    return databaseRepository.generarRespaldoDTO(DTOMapper.toDTO(config), archivoDestino);
                }
            };

            tarea.setOnSucceeded(e -> {
                if (tarea.getValue()) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El respaldo se generó correctamente en:\n" + archivoDestino.getAbsolutePath());
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Respaldo", "No se pudo generar la copia. Verifica la ruta de 'pg_dump' en los parámetros.");
                }
            });

            new Thread(tarea).start();
        }
    }

    @FXML
    private void accionRestaurarRespaldo() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Restauración");
        confirmacion.setHeaderText("ADVERTENCIA: Se sobrescribirán los datos actuales");
        confirmacion.setContentText("Restaurar una copia de seguridad reemplazará la información existente en la base de datos. ¿Deseas continuar?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo de respaldo");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PostgreSQL Backup (*.backup)", "*.backup"));

            File archivoOrigen = fileChooser.showOpenDialog(getStage());

            if (archivoOrigen != null) {
                ConexionConfig config = extraerConfiguracionFormulario();

                Task<Boolean> tarea = new Task<>() {
                    @Override
                    protected Boolean call() {
                        return databaseRepository.restaurarRespaldoDTO(DTOMapper.toDTO(config), archivoOrigen);
                    }
                };

                tarea.setOnSucceeded(e -> {
                    if (tarea.getValue()) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La base de datos fue restaurada correctamente.");
                        cargarDiagnosticoUI();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Restauración", "No se pudo restaurar el archivo. Asegúrate de tener la herramienta 'pg_restore' configurada.");
                    }
                });

                new Thread(tarea).start();
            }
        }
    }

    // --- ACCIONES DE MANTENIMIENTO Y REFRESCO ---
    @FXML
    private void accionOptimizarBD() {
        Task<Boolean> tarea = new Task<>() {
            @Override
            protected Boolean call() {
                return databaseRepository.optimizarBaseDatos();
            }
        };

        tarea.setOnSucceeded(e -> {
            if (tarea.getValue()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Mantenimiento Completado", "Se ejecutó el comando 'VACUUM ANALYZE' con éxito. Espacio libre recuperado.");
                cargarDiagnosticoUI();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo ejecutar la optimización de la base de datos.");
            }
        });

        new Thread(tarea).start();
    }

    @FXML
    private void accionRefrescarDiagnostico() {
        cargarDiagnosticoUI();
    }

    // --- MÉTODOS AUXILIARES ---
    private Stage getStage() {
        return (Stage) txtHost.getScene().getWindow();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
