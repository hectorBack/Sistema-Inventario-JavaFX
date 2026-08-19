package com.inventario.util.Ventas;

import com.inventario.controller.BusquedaProductoModalController;
import com.inventario.model.Producto;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BusquedaProductoUtil {

    public static Optional<Producto> abrirModalBusqueda(Stage ownerStage, List<Producto> productosList) {
        try {
            URL fxmlLocation = BusquedaProductoUtil.class.getResource("/com/inventario/view/BusquedaProductoModal.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = BusquedaProductoUtil.class.getResource("BusquedaProductoModal.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            BusquedaProductoModalController controller = loader.getController();
            controller.initData(productosList);

            Stage modalStage = new Stage();
            modalStage.setTitle("Búsqueda de Productos");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            if (ownerStage != null) {
                modalStage.initOwner(ownerStage);
            }
            modalStage.setScene(new Scene(root));

            controller.asociarAtajosEscena(modalStage);

            modalStage.showAndWait();

            return Optional.ofNullable(controller.getProductoSeleccionado());

        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
