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
     * formulario/tabla (CRUD).
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

    /**
     * Configura los atajos de teclado para la vista de POS / Ventas.
     *
     * @param nodoReferencia Nodo perteneciente a la vista
     * @param accionAgregarCarrito Acción al presionar Ctrl + A (Añadir al
     * carrito)
     * @param accionQuitarItem Acción al presionar Tecla SUPR / DELETE (Quitar
     * item del carrito)
     * @param accionCobrar Acción al presionar F12 (Cobrar / Registrar Venta)
     * @param accionBuscarFocus Acción al presionar Ctrl + F (Enfocar buscador)
     * @param accionLimpiar Acción al presionar ESC (Limpiar venta actual)
     */
    public static void registrarAtajosVentas(
            Node nodoReferencia,
            Runnable accionAgregarCarrito,
            Runnable accionQuitarItem,
            Runnable accionCobrar,
            Runnable accionBuscarFocus,
            Runnable accionLimpiar) {

        Platform.runLater(() -> {
            if (nodoReferencia == null || nodoReferencia.getScene() == null) {
                return;
            }

            Scene scene = nodoReferencia.getScene();

            KeyCombination ctrlA = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            scene.setOnKeyPressed((KeyEvent event) -> {
                // F12: Cobrar
                if (event.getCode() == KeyCode.F12 && accionCobrar != null) {
                    accionCobrar.run();
                    event.consume();
                } // Ctrl + A: Agregar al carrito
                else if (ctrlA.match(event) && accionAgregarCarrito != null) {
                    accionAgregarCarrito.run();
                    event.consume();
                } // Ctrl + F: Buscar producto
                else if (ctrlF.match(event) && accionBuscarFocus != null) {
                    accionBuscarFocus.run();
                    event.consume();
                } // SUPR: Quitar elemento del carrito (si no está escribiendo en un campo de texto)
                else if (event.getCode() == KeyCode.DELETE && accionQuitarItem != null) {
                    if (!(scene.getFocusOwner() instanceof TextInputControl)) {
                        accionQuitarItem.run();
                        event.consume();
                    }
                } // ESC: Limpiar / Cancelar
                else if (event.getCode() == KeyCode.ESCAPE && accionLimpiar != null) {
                    accionLimpiar.run();
                    event.consume();
                }
            });
        });
    }

    /**
     * Configura los atajos de teclado para la vista de Control de Inventario.
     *
     * @param nodoReferencia Nodo perteneciente a la vista
     * @param accionAgregarStock Acción al presionar Ctrl + G
     * @param accionBajoStock Acción al presionar Ctrl + B
     * @param accionReporte Acción al presionar Ctrl + P
     * @param accionBuscarFocus Acción al presionar Ctrl + F
     * @param accionLimpiar Acción al presionar ESC
     */
    public static void registrarAtajosInventario(
            Node nodoReferencia,
            Runnable accionAgregarStock,
            Runnable accionBajoStock,
            Runnable accionReporte,
            Runnable accionBuscarFocus,
            Runnable accionLimpiar) {

        Platform.runLater(() -> {
            if (nodoReferencia == null || nodoReferencia.getScene() == null) {
                return;
            }

            Scene scene = nodoReferencia.getScene();

            KeyCombination ctrlG = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlB = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlP = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            scene.setOnKeyPressed((KeyEvent event) -> {
                // Ctrl + G: Agregar stock
                if (ctrlG.match(event) && accionAgregarStock != null) {
                    accionAgregarStock.run();
                    event.consume();
                } // Ctrl + B: Filtrar productos con bajo stock
                else if (ctrlB.match(event) && accionBajoStock != null) {
                    accionBajoStock.run();
                    event.consume();
                } // Ctrl + P: Generar / Imprimir reporte
                else if (ctrlP.match(event) && accionReporte != null) {
                    accionReporte.run();
                    event.consume();
                } // Ctrl + F: Enfocar campo de búsqueda
                else if (ctrlF.match(event) && accionBuscarFocus != null) {
                    accionBuscarFocus.run();
                    event.consume();
                } // ESC: Limpiar filtros o selecciones
                else if (event.getCode() == KeyCode.ESCAPE && accionLimpiar != null) {
                    accionLimpiar.run();
                    event.consume();
                }
            });
        });
    }

    /**
     * Configura los atajos de teclado para la vista de Movimientos / Ajuste de
     * Stock.
     *
     * @param nodoReferencia Nodo perteneciente a la vista
     * @param accionProcesarAjuste Acción al presionar Ctrl + S
     * @param accionBuscarFocus Acción al presionar Ctrl + F
     * @param accionLimpiarFiltros Acción al presionar ESC
     */
    public static void registrarAtajosMovimiento(
            Node nodoReferencia,
            Runnable accionProcesarAjuste,
            Runnable accionBuscarFocus,
            Runnable accionLimpiarFiltros) {

        Platform.runLater(() -> {
            if (nodoReferencia == null || nodoReferencia.getScene() == null) {
                return;
            }

            Scene scene = nodoReferencia.getScene();

            KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            scene.setOnKeyPressed((KeyEvent event) -> {
                // Ctrl + S: Procesar Ajuste
                if (ctrlS.match(event) && accionProcesarAjuste != null) {
                    accionProcesarAjuste.run();
                    event.consume();
                } // Ctrl + F: Buscar
                else if (ctrlF.match(event) && accionBuscarFocus != null) {
                    accionBuscarFocus.run();
                    event.consume();
                } // ESC: Limpiar
                else if (event.getCode() == KeyCode.ESCAPE && accionLimpiarFiltros != null) {
                    accionLimpiarFiltros.run();
                    event.consume();
                }
            });
        });
    }

    /**
     * Configura los atajos de teclado para la vista de Historial de Ventas.
     *
     * @param nodoReferencia Nodo perteneciente a la vista
     * @param accionFiltrar Acción al presionar Ctrl + L
     * @param accionExportarExcel Acción al presionar Ctrl + E
     * @param accionImprimir Acción al presionar Ctrl + P
     * @param accionBuscarFocus Acción al presionar Ctrl + F
     * @param accionLimpiarFiltros Acción al presionar ESC
     */
    public static void registrarAtajosHistorialVentas(
            Node nodoReferencia,
            Runnable accionFiltrar,
            Runnable accionExportarExcel,
            Runnable accionImprimir,
            Runnable accionBuscarFocus,
            Runnable accionLimpiarFiltros) {

        Platform.runLater(() -> {
            if (nodoReferencia == null || nodoReferencia.getScene() == null) {
                return;
            }

            Scene scene = nodoReferencia.getScene();

            KeyCombination ctrlL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlE = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlP = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            scene.setOnKeyPressed((KeyEvent event) -> {
                // Ctrl + L: Aplicar Filtro
                if (ctrlL.match(event) && accionFiltrar != null) {
                    accionFiltrar.run();
                    event.consume();
                } // Ctrl + E: Exportar a Excel
                else if (ctrlE.match(event) && accionExportarExcel != null) {
                    accionExportarExcel.run();
                    event.consume();
                } // Ctrl + P: Imprimir
                else if (ctrlP.match(event) && accionImprimir != null) {
                    accionImprimir.run();
                    event.consume();
                } // Ctrl + F: Enfocar campo de búsqueda
                else if (ctrlF.match(event) && accionBuscarFocus != null) {
                    accionBuscarFocus.run();
                    event.consume();
                } // ESC: Limpiar filtros
                else if (event.getCode() == KeyCode.ESCAPE && accionLimpiarFiltros != null) {
                    accionLimpiarFiltros.run();
                    event.consume();
                }
            });
        });
    }
}
