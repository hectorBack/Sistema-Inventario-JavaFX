package com.inventario.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfiguracionController {

    // Helper reutilizable para abrir cualquier ventana o modal
    private void cambiarVista(ActionEvent event, String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent nuevaVista = loader.load();

            // Obtenemos el Stage actual a partir del evento del botón
            if (event != null && event.getSource() instanceof Node) {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();

                // Mantenemos los estilos actuales
                Scene scene = node.getScene();
                scene.setRoot(nuevaVista);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarEnContenidoCentral(ActionEvent event, String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent subVista = loader.load();

            Node node = (Node) event.getSource();
            BorderPane mainLayout = (BorderPane) node.getScene().lookup("#mainLayout");

            if (mainLayout != null) {
                mainLayout.setCenter(subVista);
            } else {
                System.err.println("ERROR: No se encontró la etiqueta #mainLayout en la escena actual.");
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: No se encontró el recurso FXML en la ruta: " + rutaFxml);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("ERROR: Falló la carga del FXML (Revisa sintaxis FXML o CSS inválido): " + rutaFxml);
            e.printStackTrace();
        }
    }

    // --- ACCIONES SUBMÓDULO: GENERAL ---
    @FXML
    void onOpcionesHabilitadas(ActionEvent e) {
        cargarEnContenidoCentral(e, "/com/inventario/view/opcionesHabilitadas.fxml");
    }

    @FXML
    void onCajeros(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/cajerosModal.fxml");
    }

    @FXML
    void onBaseDatos(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/baseDatosModal.fxml");
    }

    @FXML
    void onArticulosPrecargados(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/articulosPrecargadosModal.fxml");
    }

    @FXML
    void onFacturacion(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/facturacionModal.fxml");
    }

    @FXML
    void onModificarFolios(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/foliosModal.fxml");
    }

    @FXML
    void onAdministrarCajas(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/cajasModal.fxml");
    }

    // --- ACCIONES SUBMÓDULO: PERSONALIZACIÓN ---
    @FXML
    void onLogotipo(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/logotipoModal.fxml");
    }

    @FXML
    void onTicket(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/ticketModal.fxml");
    }

    @FXML
    void onFormasPago(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/formasPagoModal.fxml");
    }

    @FXML
    void onImpuestos(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/impuestosModal.fxml");
    }

    @FXML
    void onCorte(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/corteModal.fxml");
    }

    @FXML
    void onSimboloMoneda(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/monedaModal.fxml");
    }

    @FXML
    void onUnidadesMedida(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/unidadesModal.fxml");
    }

    // --- ACCIONES SUBMÓDULO: DISPOSITIVOS ---
    @FXML
    void onImpresoraTickets(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/impresoraModal.fxml");
    }

    @FXML
    void onLectorCodigos(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/lectorModal.fxml");
    }

    @FXML
    void onCajonDinero(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/cajonModal.fxml");
    }

    @FXML
    void onBascula(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/basculaModal.fxml");
    }

    // --- ACCIONES SUBMÓDULO: SERVICIOS ---
    @FXML
    void onRecargasElectronicas(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/recargasModal.fxml");
    }

    @FXML
    void onPagoServicios(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/serviciosModal.fxml");
    }

    @FXML
    void onNotificacionesCorreo(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/correoModal.fxml");
    }

    // --- ACCIONES SUBMÓDULO: MANTENIMIENTO ---
    @FXML
    void onRespaldoAutomatico(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/respaldoModal.fxml");
    }

    @FXML
    void onActualizacionesAutomaticas(ActionEvent e) {
        cambiarVista(e, "/com/inventario/view/config/actualizacionesModal.fxml");
    }

}
