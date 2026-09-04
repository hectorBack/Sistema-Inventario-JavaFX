package com.inventario.model.DTOs;

public final class InformacionBDDTO {
    private final String motorVersion;
    private final String tamanioBD;
    private final String totalProductos;
    private final String totalVentas;
    private final String totalCajeros;
    private final boolean estadoConexion;

        public InformacionBDDTO(String motorVersion, String tamanioBD, String totalProductos, String totalVentas,
            String totalCajeros, boolean estadoConexion) {
        this.motorVersion = motorVersion;
        this.tamanioBD = tamanioBD;
        this.totalProductos = totalProductos;
        this.totalVentas = totalVentas;
        this.totalCajeros = totalCajeros;
        this.estadoConexion = estadoConexion;
    }

    public String getMotorVersion() { return motorVersion; }
    public String getTamanioBD() { return tamanioBD; }
    public String getTotalProductos() { return totalProductos; }
    public String getTotalVentas() { return totalVentas; }
    public String getTotalCajeros() { return totalCajeros; }
    public boolean isEstadoConexion() { return estadoConexion; }
}
