package com.inventario.util.Inventario;

import com.inventario.controller.ReporteInventarioModalController;
import com.inventario.model.Categoria;
import com.inventario.model.Producto;
import com.inventario.util.Ventas.BusquedaProductoUtil;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReporteInventarioModal {

    public static void abrirModalReporte(Stage parentStage, List<Producto> productos, List<Categoria> categorias) {
        try {
            FXMLLoader loader = new FXMLLoader(BusquedaProductoUtil.class.getResource("/com/inventario/view/reporteInventarioModal.fxml"));
            Parent root = loader.load();

            ReporteInventarioModalController controller = loader.getController();
            controller.initData(productos, categorias);

            Stage modalStage = new Stage();
            modalStage.setTitle("Reporte de Inventario");
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(parentStage);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
