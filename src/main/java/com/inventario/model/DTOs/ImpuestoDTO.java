package com.inventario.model.DTOs;

public class ImpuestoDTO {

    private int id;
    private String nombre;
    private double porcentaje;
    private String pais;
    private String estado;
    private Boolean desglosarTicket;
    private Boolean preciosConImpuesto;

    public ImpuestoDTO() {
    }

    public ImpuestoDTO(Integer id, String nombre, Double porcentaje, String pais, String estado, Boolean desglosarTicket, Boolean preciosConImpuesto) {
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.pais = pais;
        this.estado = estado;
        this.desglosarTicket = desglosarTicket;
        this.preciosConImpuesto = preciosConImpuesto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getDesglosarTicket() {
        return desglosarTicket;
    }

    public void setDesglosarTicket(Boolean desglosarTicket) {
        this.desglosarTicket = desglosarTicket;
    }

    public Boolean getPreciosConImpuesto() {
        return preciosConImpuesto;
    }

    public void setPreciosConImpuesto(Boolean preciosConImpuesto) {
        this.preciosConImpuesto = preciosConImpuesto;
    }

}
