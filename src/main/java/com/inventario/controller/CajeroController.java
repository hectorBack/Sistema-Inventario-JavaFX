package com.inventario.controller;

import com.inventario.model.Cajero;
import com.inventario.repository.CajeroRepository;
import com.inventario.repository.Impl.CajeroRepositoryImpl;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CajeroController implements Initializable {

    // Componentes del Formulario
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtNombreCompleto;

    // Checkboxes Permisos: Ventas
    @FXML
    private CheckBox chkUtilizarProductoComun;
    @FXML
    private CheckBox chkAplicarMayoreo;
    @FXML
    private CheckBox chkAplicarDescuento;
    @FXML
    private CheckBox chkRevisarHistorialVentas;
    @FXML
    private CheckBox chkRegistrarEntradasEfectivo;
    @FXML
    private CheckBox chkRegistrarSalidasEfectivo;
    @FXML
    private CheckBox chkCobrarTicket;
    @FXML
    private CheckBox chkCobrarCredito;
    @FXML
    private CheckBox chkCancelarTickets;
    @FXML
    private CheckBox chkEliminarArticulosVenta;
    @FXML
    private CheckBox chkFacturarVerFacturas;
    @FXML
    private CheckBox chkVenderPagoServicio;
    @FXML
    private CheckBox chkCancelarPagoServicio;
    @FXML
    private CheckBox chkVenderRecargas;

    // Checkboxes Permisos: Clientes
    @FXML
    private CheckBox chkCrearModificarEliminarClientes;
    @FXML
    private CheckBox chkAsignarClienteVenta;
    @FXML
    private CheckBox chkAsignarRemoverCredito;
    @FXML
    private CheckBox chkVerCuentaAbonosReportes;

    // Checkboxes Permisos: Productos
    @FXML
    private CheckBox chkCrearNuevosProductos;
    @FXML
    private CheckBox chkModificarProductos;
    @FXML
    private CheckBox chkEliminarProductos;
    @FXML
    private CheckBox chkVerReporteVentas;
    @FXML
    private CheckBox chkCrearPromociones;
    @FXML
    private CheckBox chkModificarVarios;

    // Checkboxes Permisos: Inventario
    @FXML
    private CheckBox chkAgregarMercancia;
    @FXML
    private CheckBox chkVerReportesExistencias;
    @FXML
    private CheckBox chkVerMovimientoInventarios;
    @FXML
    private CheckBox chkAjustarInventario;

    // Checkboxes Permisos: Otros
    @FXML
    private CheckBox chkCorteTurnoEfectivo;
    @FXML
    private CheckBox chkCorteDiaTodosTurnos;
    @FXML
    private CheckBox chkVerGananciaDia;
    @FXML
    private CheckBox chkCambiarConfiguracion;
    @FXML
    private CheckBox chkAccederReportesVentasGanancias;
    @FXML
    private CheckBox chkCrearOrdenesCompra;
    @FXML
    private CheckBox chkRecibirOrdenesCompra;

    // Tabla y Buscador
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Cajero> tablaCajeros;
    @FXML
    private TableColumn<Cajero, Integer> colId;
    @FXML
    private TableColumn<Cajero, String> colUsuario;
    @FXML
    private TableColumn<Cajero, String> colNombreCompleto;

    // Contenedor de Pestañas y Botones
    @FXML
    private TabPane tabPanePrincipal;
    @FXML
    private Tab tabNuevoModificar;
    @FXML
    private Tab tabListado;

    private final CajeroRepository cajeroRepository = new CajeroRepositoryImpl();
    private final ObservableList<Cajero> listaCajeros = FXCollections.observableArrayList();
    private Cajero cajeroSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarCajeros();
        configurarBuscador();

        // Listener de selección en la tabla actualizado
        tablaCajeros.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cajeroSeleccionado = newSel;

                // 1. Cargar datos básicos
                txtUsuario.setText(newSel.getUsuario());
                txtContrasena.setText(newSel.getContrasena());
                txtNombreCompleto.setText(newSel.getNombreCompleto());

                // 2. Marcar/Desmarcar los CheckBoxes según el usuario seleccionado
                cargarPermisosEnFormulario(newSel);
            } else {
                limpiarFormulario();
            }
        });
    }

    private void configurarTabla() {

        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        tablaCajeros.setItems(listaCajeros);
    }

    private void cargarCajeros() {
        listaCajeros.clear();
        listaCajeros.addAll(cajeroRepository.obtenerTodosActivos());
    }

    private void configurarBuscador() {
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                cargarCajeros();
            } else {
                listaCajeros.clear();
                listaCajeros.addAll(cajeroRepository.buscarPorCriterio(newText.trim()));
            }
        });
    }

    // --- ACCIONES DE BOTONES ---
    @FXML
    private void accionGuardar() {
        if (!validarFormulario()) {
            return;
        }

        boolean esNuevo = (cajeroSeleccionado == null);
        Cajero c = esNuevo ? new Cajero(txtUsuario.getText().trim(), txtContrasena.getText().trim(), txtNombreCompleto.getText().trim())
                : cajeroSeleccionado;

        if (!esNuevo) {
            c.setUsuario(txtUsuario.getText().trim());
            c.setContrasena(txtContrasena.getText().trim());
            c.setNombreCompleto(txtNombreCompleto.getText().trim());
        }

        // Asignar Permisos desde CheckBoxes
        extraerPermisosFormulario(c);

        boolean exito = esNuevo ? cajeroRepository.guardar(c) : cajeroRepository.actualizar(c);

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El cajero fue guardado correctamente.");
            limpiarFormulario();
            cargarCajeros();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la información en la base de datos.");
        }
    }

    @FXML
    private void accionModificarCajero() {
        if (cajeroSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un cajero de la lista.");
            return;
        }

        txtUsuario.setText(cajeroSeleccionado.getUsuario());
        txtContrasena.setText(cajeroSeleccionado.getContrasena());
        txtNombreCompleto.setText(cajeroSeleccionado.getNombreCompleto());

        cargarPermisosEnFormulario(cajeroSeleccionado);

    }

    @FXML
    private void accionDarDeBajaLogica() {
        if (cajeroSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un cajero de la lista.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Baja Lógica");
        confirmacion.setHeaderText("¿Desactivar al cajero '" + cajeroSeleccionado.getNombreCompleto() + "'?");
        confirmacion.setContentText("El cajero se marcará como inactivo y conservará su historial de ventas.");

        Optional<ButtonType> result = confirmacion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (cajeroRepository.darDeBajaLogica(cajeroSeleccionado.getId())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El cajero ha sido dado de baja.");
                limpiarFormulario();
                cargarCajeros();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo dar de baja al cajero.");
            }
        }
    }

    @FXML
    private void accionEliminarFisico() {
        if (cajeroSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un cajero de la lista.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Borrado Físico");
        confirmacion.setHeaderText("¿ELIMINAR DEFINITIVAMENTE al cajero '" + cajeroSeleccionado.getNombreCompleto() + "'?");
        confirmacion.setContentText("Esta acción eliminará el registro de la BD. Solo debe usarse si el cajero no tiene ventas registradas.");

        Optional<ButtonType> result = confirmacion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (cajeroRepository.eliminarFisico(cajeroSeleccionado.getId())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cajero eliminado permanentemente.");
                limpiarFormulario();
                cargarCajeros();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el registro. Es probable que tenga ventas asociadas.");
            }
        }
    }

    @FXML
    private void accionNuevo() {
        limpiarFormulario();
        if (tablaCajeros != null && tablaCajeros.getSelectionModel() != null) {
            tablaCajeros.getSelectionModel().clearSelection();
        }
    }

    // --- MÉTODOS AUXILIARES DE PERMISOS ---
    @FXML
    private void marcarTodosVentas() {
        setEstadoPermisosVentas(true);
    }

    @FXML
    private void desmarcarTodosVentas() {
        setEstadoPermisosVentas(false);
    }

    private void setEstadoPermisosVentas(boolean estado) {
        chkUtilizarProductoComun.setSelected(estado);
        chkAplicarMayoreo.setSelected(estado);
        chkAplicarDescuento.setSelected(estado);
        chkRevisarHistorialVentas.setSelected(estado);
        chkRegistrarEntradasEfectivo.setSelected(estado);
        chkRegistrarSalidasEfectivo.setSelected(estado);
        chkCobrarTicket.setSelected(estado);
        chkCobrarCredito.setSelected(estado);
        chkCancelarTickets.setSelected(estado);
        chkEliminarArticulosVenta.setSelected(estado);
        chkFacturarVerFacturas.setSelected(estado);
        chkVenderPagoServicio.setSelected(estado);
        chkCancelarPagoServicio.setSelected(estado);
        chkVenderRecargas.setSelected(estado);
    }

    private void extraerPermisosFormulario(Cajero c) {
        // Ventas
        c.setPermUtilizarProductoComun(chkUtilizarProductoComun.isSelected());
        c.setPermAplicarMayoreo(chkAplicarMayoreo.isSelected());
        c.setPermAplicarDescuento(chkAplicarDescuento.isSelected());
        c.setPermRevisarHistorialVentas(chkRevisarHistorialVentas.isSelected());
        c.setPermRegistrarEntradasEfectivo(chkRegistrarEntradasEfectivo.isSelected());
        c.setPermRegistrarSalidasEfectivo(chkRegistrarSalidasEfectivo.isSelected());
        c.setPermCobrarTicket(chkCobrarTicket.isSelected());
        c.setPermCobrarCredito(chkCobrarCredito.isSelected());
        c.setPermCancelarTickets(chkCancelarTickets.isSelected());
        c.setPermEliminarArticulosVenta(chkEliminarArticulosVenta.isSelected());
        c.setPermFacturarVerFacturas(chkFacturarVerFacturas.isSelected());
        c.setPermVenderPagoServicio(chkVenderPagoServicio.isSelected());
        c.setPermCancelarPagoServicio(chkCancelarPagoServicio.isSelected());
        c.setPermVenderRecargas(chkVenderRecargas.isSelected());

        // Clientes
        c.setPermCrearModificarEliminarClientes(chkCrearModificarEliminarClientes.isSelected());
        c.setPermAsignarClienteVenta(chkAsignarClienteVenta.isSelected());
        c.setPermAsignarRemoverCredito(chkAsignarRemoverCredito.isSelected());
        c.setPermVerCuentaAbonosReportes(chkVerCuentaAbonosReportes.isSelected());

        // Productos
        c.setPermCrearNuevosProductos(chkCrearNuevosProductos.isSelected());
        c.setPermModificarProductos(chkModificarProductos.isSelected());
        c.setPermEliminarProductos(chkEliminarProductos.isSelected());
        c.setPermVerReporteVentas(chkVerReporteVentas.isSelected());
        c.setPermCrearPromociones(chkCrearPromociones.isSelected());
        c.setPermModificarVarios(chkModificarVarios.isSelected());

        // Inventario
        c.setPermAgregarMercancia(chkAgregarMercancia.isSelected());
        c.setPermVerReportesExistencias(chkVerReportesExistencias.isSelected());
        c.setPermVerMovimientoInventarios(chkVerMovimientoInventarios.isSelected());
        c.setPermAjustarInventario(chkAjustarInventario.isSelected());

        // Otros
        c.setPermCorteTurnoEfectivo(chkCorteTurnoEfectivo.isSelected());
        c.setPermCorteDiaTodosTurnos(chkCorteDiaTodosTurnos.isSelected());
        c.setPermVerGananciaDia(chkVerGananciaDia.isSelected());
        c.setPermCambiarConfiguracion(chkCambiarConfiguracion.isSelected());
        c.setPermAccederReportesVentasGanancias(chkAccederReportesVentasGanancias.isSelected());
        c.setPermCrearOrdenesCompra(chkCrearOrdenesCompra.isSelected());
        c.setPermRecibirOrdenesCompra(chkRecibirOrdenesCompra.isSelected());
    }

    private void cargarPermisosEnFormulario(Cajero c) {
        // Ventas
        chkUtilizarProductoComun.setSelected(c.isPermUtilizarProductoComun());
        chkAplicarMayoreo.setSelected(c.isPermAplicarMayoreo());
        chkAplicarDescuento.setSelected(c.isPermAplicarDescuento());
        chkRevisarHistorialVentas.setSelected(c.isPermRevisarHistorialVentas());
        chkRegistrarEntradasEfectivo.setSelected(c.isPermRegistrarEntradasEfectivo());
        chkRegistrarSalidasEfectivo.setSelected(c.isPermRegistrarSalidasEfectivo());
        chkCobrarTicket.setSelected(c.isPermCobrarTicket());
        chkCobrarCredito.setSelected(c.isPermCobrarCredito());
        chkCancelarTickets.setSelected(c.isPermCancelarTickets());
        chkEliminarArticulosVenta.setSelected(c.isPermEliminarArticulosVenta());
        chkFacturarVerFacturas.setSelected(c.isPermFacturarVerFacturas());
        chkVenderPagoServicio.setSelected(c.isPermVenderPagoServicio());
        chkCancelarPagoServicio.setSelected(c.isPermCancelarPagoServicio());
        chkVenderRecargas.setSelected(c.isPermVenderRecargas());

        // Clientes
        chkCrearModificarEliminarClientes.setSelected(c.isPermCrearModificarEliminarClientes());
        chkAsignarClienteVenta.setSelected(c.isPermAsignarClienteVenta());
        chkAsignarRemoverCredito.setSelected(c.isPermAsignarRemoverCredito());
        chkVerCuentaAbonosReportes.setSelected(c.isPermVerCuentaAbonosReportes());

        // Productos
        chkCrearNuevosProductos.setSelected(c.isPermCrearNuevosProductos());
        chkModificarProductos.setSelected(c.isPermModificarProductos());
        chkEliminarProductos.setSelected(c.isPermEliminarProductos());
        chkVerReporteVentas.setSelected(c.isPermVerReporteVentas());
        chkCrearPromociones.setSelected(c.isPermCrearPromociones());
        chkModificarVarios.setSelected(c.isPermModificarVarios());

        // Inventario
        chkAgregarMercancia.setSelected(c.isPermAgregarMercancia());
        chkVerReportesExistencias.setSelected(c.isPermVerReportesExistencias());
        chkVerMovimientoInventarios.setSelected(c.isPermVerMovimientoInventarios());
        chkAjustarInventario.setSelected(c.isPermAjustarInventario());

        // Otros
        chkCorteTurnoEfectivo.setSelected(c.isPermCorteTurnoEfectivo());
        chkCorteDiaTodosTurnos.setSelected(c.isPermCorteDiaTodosTurnos());
        chkVerGananciaDia.setSelected(c.isPermVerGananciaDia());
        chkCambiarConfiguracion.setSelected(c.isPermCambiarConfiguracion());
        chkAccederReportesVentasGanancias.setSelected(c.isPermAccederReportesVentasGanancias());
        chkCrearOrdenesCompra.setSelected(c.isPermCrearOrdenesCompra());
        chkRecibirOrdenesCompra.setSelected(c.isPermRecibirOrdenesCompra());
    }

    private void limpiarFormulario() {
        cajeroSeleccionado = null;
        txtUsuario.clear();
        txtContrasena.clear();
        txtNombreCompleto.clear();
        setEstadoPermisosVentas(false);

        chkCrearModificarEliminarClientes.setSelected(false);
        chkAsignarClienteVenta.setSelected(false);
        chkAsignarRemoverCredito.setSelected(false);
        chkVerCuentaAbonosReportes.setSelected(false);

        chkCrearNuevosProductos.setSelected(false);
        chkModificarProductos.setSelected(false);
        chkEliminarProductos.setSelected(false);
        chkVerReporteVentas.setSelected(false);
        chkCrearPromociones.setSelected(false);
        chkModificarVarios.setSelected(false);

        chkAgregarMercancia.setSelected(false);
        chkVerReportesExistencias.setSelected(false);
        chkVerMovimientoInventarios.setSelected(false);
        chkAjustarInventario.setSelected(false);

        chkCorteTurnoEfectivo.setSelected(false);
        chkCorteDiaTodosTurnos.setSelected(false);
        chkVerGananciaDia.setSelected(false);
        chkCambiarConfiguracion.setSelected(false);
        chkAccederReportesVentasGanancias.setSelected(false);
        chkCrearOrdenesCompra.setSelected(false);
        chkRecibirOrdenesCompra.setSelected(false);
    }

    private boolean validarFormulario() {
        if (txtUsuario.getText().trim().isEmpty() || txtContrasena.getText().trim().isEmpty() || txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios", "Por favor completa el usuario, contraseña y nombre completo.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
