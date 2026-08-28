package com.inventario.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConexionConfig {

    private final StringProperty host;
    private final StringProperty puerto;
    private final StringProperty baseDatos;
    private final StringProperty usuario;
    private final StringProperty contrasena;
    private final StringProperty rutaPgDump; // Opcional: Para ubicar la herramienta pg_dump/psql en Windows/Linux

    public ConexionConfig() {
        this("localhost", "5432", "Inventario", "postgres", "", "");
    }

    public ConexionConfig(String host, String puerto, String baseDatos, String usuario, String contrasena, String rutaPgDump) {
        this.host = new SimpleStringProperty(host);
        this.puerto = new SimpleStringProperty(puerto);
        this.baseDatos = new SimpleStringProperty(baseDatos);
        this.usuario = new SimpleStringProperty(usuario);
        this.contrasena = new SimpleStringProperty(contrasena);
        this.rutaPgDump = new SimpleStringProperty(rutaPgDump);
    }

    // --- GETTERS, SETTERS Y PROPERTIES ---
    public String getHost() {
        return host.get();
    }

    public void setHost(String value) {
        host.set(value);
    }

    public StringProperty hostProperty() {
        return host;
    }

    public String getPuerto() {
        return puerto.get();
    }

    public void setPuerto(String value) {
        puerto.set(value);
    }

    public StringProperty puertoProperty() {
        return puerto;
    }

    public String getBaseDatos() {
        return baseDatos.get();
    }

    public void setBaseDatos(String value) {
        baseDatos.set(value);
    }

    public StringProperty baseDatosProperty() {
        return baseDatos;
    }

    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String value) {
        usuario.set(value);
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena.get();
    }

    public void setContrasena(String value) {
        contrasena.set(value);
    }

    public StringProperty contrasenaProperty() {
        return contrasena;
    }

    public String getRutaPgDump() {
        return rutaPgDump.get();
    }

    public void setRutaPgDump(String value) {
        rutaPgDump.set(value);
    }

    public StringProperty rutaPgDumpProperty() {
        return rutaPgDump;
    }

}
