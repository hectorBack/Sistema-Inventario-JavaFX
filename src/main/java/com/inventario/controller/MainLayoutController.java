package com.inventario.controller;

import com.inventario.config.ConfiguracionSistema;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainLayoutController implements Initializable {

    @FXML
    private BorderPane mainLayout;
    @FXML
    private Button btnProductos;
    @FXML
    private Button btnInventario;
    @FXML
    private Button btnCategorias;
    @FXML
    private Button btnMovimientos;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnHistorialVentas;
    @FXML
    private Button btnPromociones;
    @FXML
    private Button btnConfiguracion;

    private static MainLayoutController instancia;

    public static MainLayoutController getInstancia() {
        return instancia;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instancia = this;
        // Aplicar estado de los botones según la configuración
        actualizarEstadoModulos();

        // Al arrancar, cargamos por defecto el inventario en el centro y seleccionamos su botón
        cargarVista("view/ProductosView");
        actualizarEstiloMenu(btnProductos);
    }

    /**
     * Refresca la propiedad 'disable' de los botones según la configuración
     * activa.
     */
    public void actualizarEstadoModulos() {
        boolean usarInventario = ConfiguracionSistema.getInstancia().getOpciones().isUsarInventario();

        btnInventario.setDisable(!usarInventario);
        btnMovimientos.setDisable(!usarInventario);

        // Si se acaba de desactivar el inventario estando en una vista deshabilitada, regresar a Productos
        if (!usarInventario && (btnInventario.getStyleClass().contains("active") || btnMovimientos.getStyleClass().contains("active"))) {
            cargarVista("view/ProductosView");
            actualizarEstiloMenu(btnProductos);
        } else {
            actualizarEstiloMenu(obtenerBotonSeleccionadoActual());
        }
    }

    private Button obtenerBotonSeleccionadoActual() {
        List<Button> todos = List.of(
                btnProductos, btnInventario, btnCategorias, btnMovimientos,
                btnProveedores, btnClientes, btnVentas, btnHistorialVentas,
                btnPromociones, btnConfiguracion
        );
        return todos.stream().filter(b -> b.getStyle().contains("#334155")).findFirst().orElse(btnProductos);
    }

    @FXML
    void mostrarProductos(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/ProductosView");
        actualizarEstiloMenu(btnProductos);
    }

    @FXML
    void mostrarInventario(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/InventarioView");
        actualizarEstiloMenu(btnInventario);
    }

    @FXML
    void mostrarCategorias(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/Categoria");
        actualizarEstiloMenu(btnCategorias);
    }

    @FXML
    void mostrarMovimientos(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/Movimiento");
        actualizarEstiloMenu(btnMovimientos);
    }

    @FXML
    void mostrarProveedores(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/Proveedor");
        actualizarEstiloMenu(btnProveedores);
    }

    @FXML
    void mostrarClientes(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/Clientes");
        actualizarEstiloMenu(btnClientes);
    }

    @FXML
    void mostrarVentas(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/Ventas");
        actualizarEstiloMenu(btnVentas);
    }

    @FXML
    void mostrarHistorialVentas(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/HistorialVentas");
        actualizarEstiloMenu(btnHistorialVentas);
    }

    @FXML
    void mostrarPromociones(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/PromocionesView");
        actualizarEstiloMenu(btnPromociones);
    }

    @FXML
    void mostrarConfiguraciones(ActionEvent event) {
        actualizarEstadoModulos();
        cargarVista("view/configuracion");
        actualizarEstiloMenu(btnConfiguracion);
    }

    private void cargarVista(String fxmlPath) {
        try {
            // Cargamos el archivo FXML correspondiente de manera dinámica
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventario/" + fxmlPath + ".fxml"));
            Parent vista = loader.load();
            // Lo incrustamos en la región central del BorderPane
            mainLayout.setCenter(vista);
        } catch (IOException e) {
            System.out.println("Error al cargar la vista interna: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstiloMenu(Button seleccionado) {
        List<Button> todosLosBotones = List.of(
                btnProductos, btnInventario, btnCategorias, btnMovimientos,
                btnProveedores, btnClientes, btnVentas, btnHistorialVentas,
                btnPromociones, btnConfiguracion
        );

        for (Button boton : todosLosBotones) {
            if (boton.isDisabled()) {
                // Estilo para botones desactivados (opacidad reducida sin puntero de mano)
                boton.setStyle("-fx-background-color: transparent; -fx-opacity: 0.4; -fx-cursor: default;");
                boton.setTextFill(javafx.scene.paint.Color.web("#94a3b8"));
            } else if (boton == seleccionado) {
                // Botón activo/seleccionado
                boton.setStyle("-fx-background-color: #334155; -fx-background-radius: 6; -fx-cursor: hand; -fx-opacity: 1.0;");
                boton.setTextFill(javafx.scene.paint.Color.WHITE);
            } else {
                // Botones habilitados inactivos
                boton.setStyle("-fx-background-color: transparent; -fx-background-radius: 6; -fx-cursor: hand; -fx-opacity: 1.0;");
                boton.setTextFill(javafx.scene.paint.Color.web("#94a3b8"));
            }
        }
    }

}
