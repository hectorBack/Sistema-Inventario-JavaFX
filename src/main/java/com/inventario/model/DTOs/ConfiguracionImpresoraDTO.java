package com.inventario.model.DTOs;

public class ConfiguracionImpresoraDTO {

    private Integer id;
    private String nombreImpresora;
    private String fuente;
    private int tamanoFuente;
    private int columnas;
    private boolean usarFuenteNormalTotales;
    private boolean todasNegritas;

    public ConfiguracionImpresoraDTO() {
    }

    public ConfiguracionImpresoraDTO(Integer id, String nombreImpresora, String fuente, int tamanoFuente,
            int columnas, boolean usarFuenteNormalTotales, boolean todasNegritas) {
        this.id = id;
        this.nombreImpresora = nombreImpresora;
        this.fuente = fuente;
        this.tamanoFuente = tamanoFuente;
        this.columnas = columnas;
        this.usarFuenteNormalTotales = usarFuenteNormalTotales;
        this.todasNegritas = todasNegritas;
    }

    public ConfiguracionImpresoraDTO(String nombreImpresora, String fuente, int tamanoFuente, int columnas, boolean usarFuenteNormalTotales, boolean todasNegritas) {
        this.nombreImpresora = nombreImpresora;
        this.fuente = fuente;
        this.tamanoFuente = tamanoFuente;
        this.columnas = columnas;
        this.usarFuenteNormalTotales = usarFuenteNormalTotales;
        this.todasNegritas = todasNegritas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreImpresora() {
        return nombreImpresora;
    }

    public void setNombreImpresora(String nombreImpresora) {
        this.nombreImpresora = nombreImpresora;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public int getTamanoFuente() {
        return tamanoFuente;
    }

    public void setTamanoFuente(int tamanoFuente) {
        this.tamanoFuente = tamanoFuente;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public boolean isUsarFuenteNormalTotales() {
        return usarFuenteNormalTotales;
    }

    public void setUsarFuenteNormalTotales(boolean usarFuenteNormalTotales) {
        this.usarFuenteNormalTotales = usarFuenteNormalTotales;
    }

    public boolean isTodasNegritas() {
        return todasNegritas;
    }

    public void setTodasNegritas(boolean todasNegritas) {
        this.todasNegritas = todasNegritas;
    }

}
