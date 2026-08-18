package com.inventario.util.Ventas;

import java.net.URL;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModalNavigationUtil {

    public static <T> boolean abrirModal(Class<?> contextClass, String fxmlPath, String titulo, Consumer<T> controllerInitializer) {
        try {
            URL fxmlLocation = contextClass.getResource(fxmlPath);
            if (fxmlLocation == null) {
                // Fallback relativo
                String relativeName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
                fxmlLocation = contextClass.getResource(relativeName);
            }

            if (fxmlLocation == null) {
                throw new IllegalStateException("No se pudo encontrar la vista FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            T controller = loader.getController();
            if (controllerInitializer != null) {
                controllerInitializer.accept(controller);
            }

            Stage modalStage = new Stage();
            modalStage.setTitle(titulo);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
