package com.inventario.model.DTOs;

public final class FolioDTO {
    private final Integer id;
    private final String nombre;
    private final String modulo;
    private final String serie;
    private final int folioActual;
    private final int longitudCeros;
    private final String estado;

    public FolioDTO(Integer id, String nombre, String modulo, String serie, int folioActual, int longitudCeros, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.modulo = modulo;
        this.serie = serie;
        this.folioActual = folioActual;
        this.longitudCeros = longitudCeros;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getModulo() { return modulo; }
    public String getSerie() { return serie; }
    public int getFolioActual() { return folioActual; }
    public int getLongitudCeros() { return longitudCeros; }
    public String getEstado() { return estado; }
}
