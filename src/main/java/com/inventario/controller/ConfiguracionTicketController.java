package com.inventario.controller;

import com.inventario.model.DTOs.ConfiguracionTicketDTO;
import com.inventario.model.DTOs.DetalleVentaDTO;
import com.inventario.model.DTOs.VentaDTO;
import com.inventario.repository.ConfiguracionTicketRepository;
import com.inventario.repository.Impl.ConfiguracionTicketRepositoryImpl;
import com.inventario.util.MockDataFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ConfiguracionTicketController implements Initializable {

    @FXML
    private Spinner<Integer> spnLineasEncabezado;
    @FXML
    private Spinner<Integer> spnLineasPie;
    @FXML
    private VBox vboxEncabezadoInputs;
    @FXML
    private VBox vboxPieInputs;
    @FXML
    private CheckBox chkIncluirPrecioUnitario;
    @FXML
    private CheckBox chkImprimirDescripcionCompleta;

    // FXML de la vista previa del ticket
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblHora;
    @FXML
    private Label lblHeaderPrecioUnitario;
    @FXML
    private VBox vboxDetalleProductos;
    @FXML
    private Label lblNumeroArticulos;
    @FXML
    private Label lblTotal;

    private final ConfiguracionTicketRepository repository;
    private int configId = 1;
    private VentaDTO ventaMock;

    public ConfiguracionTicketController() {
        this(new ConfiguracionTicketRepositoryImpl());
    }

    public ConfiguracionTicketController(ConfiguracionTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cargar datos de prueba
        this.ventaMock = MockDataFactory.crearVentaEjemplo();

        spnLineasEncabezado.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 4));
        spnLineasPie.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 2));

        spnLineasEncabezado.valueProperty().addListener((obs, oldVal, newVal) -> recalcularCampos(vboxEncabezadoInputs, newVal));
        spnLineasPie.valueProperty().addListener((obs, oldVal, newVal) -> recalcularCampos(vboxPieInputs, newVal));

        // Binding de la columna P.U. en la cabecera
        if (lblHeaderPrecioUnitario != null) {
            lblHeaderPrecioUnitario.visibleProperty().bind(chkIncluirPrecioUnitario.selectedProperty());
            lblHeaderPrecioUnitario.managedProperty().bind(chkIncluirPrecioUnitario.selectedProperty());
        }

        // 2. Cargar datos de persistencia PRIMERO
        cargarDatos();

        // 3. Vincular listeners DESPUÉS de cargar la configuración guardada
        chkIncluirPrecioUnitario.selectedProperty().addListener((obs, oldV, newV) -> renderizarVistaPreviaTicket());
        chkImprimirDescripcionCompleta.selectedProperty().addListener((obs, oldV, newV) -> renderizarVistaPreviaTicket());

        // 4. Forzar primer renderizado
        renderizarVistaPreviaTicket();
    }

    private void renderizarVistaPreviaTicket() {
        if (ventaMock == null) {
            return;
        }

        // 1. Fecha y Hora
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        if (lblFecha != null && ventaMock.getFecha() != null) {
            lblFecha.setText("Fecha: " + ventaMock.getFecha().format(fmtFecha));
        }
        if (lblHora != null && ventaMock.getFecha() != null) {
            lblHora.setText("Hora: " + ventaMock.getFecha().format(fmtHora));
        }

        // 2. Renderizado dinámico de productos
        if (vboxDetalleProductos != null) {
            vboxDetalleProductos.getChildren().clear();

            List<DetalleVentaDTO> detalles = ventaMock.getDetalles();
            if (detalles == null || detalles.isEmpty()) {
                detalles = MockDataFactory.crearVentaEjemplo().getDetalles();
            }

            boolean mostrarPU = chkIncluirPrecioUnitario.isSelected();

            for (DetalleVentaDTO item : detalles) {
                HBox filaPrincipal = new HBox();
                filaPrincipal.setAlignment(Pos.CENTER_LEFT);
                filaPrincipal.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

                // Cantidad
                Label lblCant = new Label(item.getCantidadFormateada());
                lblCant.setMinWidth(35.0);
                lblCant.setPrefWidth(35.0);
                lblCant.setStyle("-fx-text-fill: #000000;");

                // Nombre / Descripción
                String nombre = item.getNombreProducto() != null ? item.getNombreProducto() : "";
                int limiteCaracteres = mostrarPU ? 12 : 18;
                if (!chkImprimirDescripcionCompleta.isSelected() && nombre.length() > limiteCaracteres) {
                    nombre = nombre.substring(0, limiteCaracteres) + "...";
                }
                Label lblNombre = new Label(nombre);
                lblNombre.setMaxWidth(Double.MAX_VALUE);
                lblNombre.setStyle("-fx-text-fill: #000000;");
                HBox.setHgrow(lblNombre, Priority.ALWAYS);

                filaPrincipal.getChildren().addAll(lblCant, lblNombre);

                // Columna Precio Unitario (P.U.) opcional en la misma fila
                if (mostrarPU) {
                    Label lblPU = new Label(String.format("$%.2f", item.getPrecioUnitario()));
                    lblPU.setMinWidth(60.0);
                    lblPU.setPrefWidth(60.0);
                    lblPU.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #000000;");
                    filaPrincipal.getChildren().add(lblPU);
                }

                // Importe (Subtotal)
                Label lblImporte = new Label(String.format("$%.2f", item.getSubtotal()));
                lblImporte.setMinWidth(60.0);
                lblImporte.setPrefWidth(60.0);
                lblImporte.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #000000;");

                filaPrincipal.getChildren().add(lblImporte);
                vboxDetalleProductos.getChildren().add(filaPrincipal);
            }
        }

        // 3. Totales
        if (lblNumeroArticulos != null) {
            lblNumeroArticulos.setText("No. de Artículos: " + ventaMock.getNumeroArticulos());
        }
        if (lblTotal != null) {
            lblTotal.setText(String.format("Total: $%.2f", ventaMock.getTotal()));
        }
    }

    private void cargarDatos() {
        ConfiguracionTicketDTO config = repository.obtenerConfiguracionDTO();
        if (config != null) {
            configId = config.getId();
            chkIncluirPrecioUnitario.setSelected(config.isIncluirPrecioUnitario());
            chkImprimirDescripcionCompleta.setSelected(config.isImprimirDescripcionCompleta());

            List<String> encabezado = config.getLineasEncabezado();
            if (encabezado != null) {
                spnLineasEncabezado.getValueFactory().setValue(encabezado.size());
                recalcularCampos(vboxEncabezadoInputs, encabezado.size());
                poblarCampos(vboxEncabezadoInputs, encabezado);
            }

            List<String> pie = config.getLineasPie();
            if (pie != null) {
                spnLineasPie.getValueFactory().setValue(pie.size());
                recalcularCampos(vboxPieInputs, pie.size());
                poblarCampos(vboxPieInputs, pie);
            }
        } else {
            recalcularCampos(vboxEncabezadoInputs, spnLineasEncabezado.getValue());
            recalcularCampos(vboxPieInputs, spnLineasPie.getValue());
        }
    }

    private void recalcularCampos(VBox container, int cantidadDeseada) {
        if (container == null) {
            return;
        }
        int actuales = container.getChildren().size();
        if (cantidadDeseada > actuales) {
            for (int i = actuales; i < cantidadDeseada; i++) {
                TextField txt = new TextField();
                txt.getStyleClass().add("form-field");
                txt.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
                container.getChildren().add(txt);
            }
        } else if (cantidadDeseada < actuales) {
            container.getChildren().remove(cantidadDeseada, actuales);
        }
    }

    private void poblarCampos(VBox container, List<String> textos) {
        if (container == null || textos == null) {
            return;
        }
        for (int i = 0; i < Math.min(container.getChildren().size(), textos.size()); i++) {
            if (container.getChildren().get(i) instanceof TextField) {
                TextField txt = (TextField) container.getChildren().get(i);
                txt.setText(textos.get(i));
            }
        }
    }

    private List<String> extraerTextos(VBox container) {
        List<String> textos = new ArrayList<>();
        if (container != null) {
            for (var node : container.getChildren()) {
                if (node instanceof TextField) {
                    TextField txt = (TextField) node;
                    textos.add(txt.getText());
                }
            }
        }
        return textos;
    }

    @FXML
    private void onAgregarLineaPie() {
        int actual = spnLineasPie.getValue();
        spnLineasPie.getValueFactory().setValue(actual + 1);
    }

    @FXML
    private void onGuardar() {
        ConfiguracionTicketDTO dto = new ConfiguracionTicketDTO(
                configId,
                extraerTextos(vboxEncabezadoInputs),
                extraerTextos(vboxPieInputs),
                chkIncluirPrecioUnitario.isSelected(),
                chkImprimirDescripcionCompleta.isSelected()
        );

        boolean exito = repository.guardarOActualizarDTO(dto);
        Alert alert = new Alert(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Configuración");
        alert.setContentText(exito ? "Guardado correctamente" : "Error al guardar");
        alert.showAndWait();
    }
}
