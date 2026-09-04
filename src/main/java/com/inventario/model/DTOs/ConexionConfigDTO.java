package com.inventario.model.DTOs;

public final class ConexionConfigDTO {
    private final String host;
    private final String puerto;
    private final String baseDatos;
    private final String usuario;
    private final String contrasena;
    private final String rutaPgDump;

    public ConexionConfigDTO(String host, String puerto, String baseDatos, String usuario, String contrasena, String rutaPgDump) {
        this.host = host;
        this.puerto = puerto;
        this.baseDatos = baseDatos;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rutaPgDump = rutaPgDump;
    }

    public String getHost() { return host; }
    public String getPuerto() { return puerto; }
    public String getBaseDatos() { return baseDatos; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public String getRutaPgDump() { return rutaPgDump; }
}
