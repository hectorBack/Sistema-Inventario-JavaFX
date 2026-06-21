package com.inventario.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainLayoutController implements Initializable {
    
    @FXML private BorderPane mainContainer;
    @FXML private Button btnInventario;
    @FXML private Button btnCategorias;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Al arrancar, cargamos por defecto el inventario en el centro
        cargarVista("view/Inventario");
    }

    @FXML
    void mostrarInventario(ActionEvent event) {
        cargarVista("view/Inventario");
        intercambiarEstiloBotones(btnInventario, btnCategorias);
    }

    @FXML
    void mostrarCategorias(ActionEvent event) {
        cargarVista("view/Categoria");
        intercambiarEstiloBotones(btnCategorias, btnInventario);
    }

    private void cargarVista(String fxmlPath) {
        try {
            // Cargamos el archivo FXML correspondiente de manera dinámica
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventario/" + fxmlPath + ".fxml"));
            Parent vista = loader.load();
            // Lo incrustamos en la región central del BorderPane
            mainContainer.setCenter(vista);
        } catch (IOException e) {
            System.out.println("Error al cargar la vista interna: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void intercambiarEstiloBotones(Button seleccionado, Button deseleccionado) {
        // Estilo activo (Slate 700 / Texto Blanco)
        seleccionado.setStyle("-fx-background-color: #334155; -fx-background-radius: 6; -fx-cursor: hand;");
        seleccionado.setTextFill(javafx.scene.paint.Color.WHITE);
        
        // Estilo inactivo (Transparente / Texto grisáceo)
        deseleccionado.setStyle("-fx-background-color: transparent; -fx-background-radius: 6; -fx-cursor: hand;");
        deseleccionado.setTextFill(javafx.scene.paint.Color.web("#94a3b8"));
    }

}
