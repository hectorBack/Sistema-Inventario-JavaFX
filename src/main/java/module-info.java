module com.inventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    
    opens com.inventario to javafx.fxml;

    // Abrir controladores a FXML
    opens com.inventario.controller to javafx.fxml;
    // Abrir modelos si usas TableView de JavaFX (necesita leer propiedades mediante reflexión)
    opens com.inventario.model to javafx.base;
    
    
    exports com.inventario;
}
