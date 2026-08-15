package com.inventario.util.Productos;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class KeyboardShortcutUtil {

    /**
     * Configura los atajos de teclado estándar para un módulo de
     * formulario/tabla.
     *
     * @param nodoReferencia Cualquier nodo de la vista (ej. la tabla o el
     * contenedor principal)
     * @param accionNuevo Acción al presionar Ctrl + N
     * @param accionGuardar Acción al presionar Ctrl + S
     * @param accionActualizar Acción al presionar Ctrl + U
     * @param accionEliminar Acción al presionar Tecla SUPR / DELETE
     * @param accionBuscar Acción al presionar Ctrl + F
     * @param accionCancelar Acción al presionar ESC
     */
    public static void registrarAtajosCrud(
            Node nodoReferencia,
            Runnable accionNuevo,
            Runnable accionGuardar,
            Runnable accionActualizar,
            Runnable accionEliminar,
            Runnable accionBuscar,
            Runnable accionCancelar) {

        Platform.runLater(() -> {
            if (nodoReferencia == null || nodoReferencia.getScene() == null) {
                return;
            }

            Scene scene = nodoReferencia.getScene();

            KeyCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlU = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            scene.setOnKeyPressed((KeyEvent event) -> {
                // Ctrl + N: Nuevo / Limpiar
                if (ctrlN.match(event) && accionNuevo != null) {
                    accionNuevo.run();
                    event.consume();
                } // Ctrl + S: Guardar / Agregar
                else if (ctrlS.match(event) && accionGuardar != null) {
                    accionGuardar.run();
                    event.consume();
                } // Ctrl + U: Actualizar
                else if (ctrlU.match(event) && accionActualizar != null) {
                    accionActualizar.run();
                    event.consume();
                } // Ctrl + F: Enfocar Búsqueda
                else if (ctrlF.match(event) && accionBuscar != null) {
                    accionBuscar.run();
                    event.consume();
                } // SUPR / DELETE: Eliminar (sólo si no se está escribiendo en un campo de texto)
                else if (event.getCode() == KeyCode.DELETE && accionEliminar != null) {
                    if (!(scene.getFocusOwner() instanceof TextInputControl)) {
                        accionEliminar.run();
                        event.consume();
                    }
                } // ESC: Cancelar / Limpiar
                else if (event.getCode() == KeyCode.ESCAPE && accionCancelar != null) {
                    accionCancelar.run();
                    event.consume();
                }
            });
        });
    }
}
