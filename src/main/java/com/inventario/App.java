package com.inventario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Ampliamos el ancho total a 1100 (240px del sidebar + 860px de área de trabajo)
        scene = new Scene(loadFXML("view/MainLayout"), 1100, 600);
        stage.setScene(scene);
        stage.setTitle("Sistema de Gestión de Inventario");
        stage.setMinWidth(1120);
        stage.setMinHeight(640);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
