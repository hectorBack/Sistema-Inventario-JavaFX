package com.inventario.model.DTOs;

public class UnidadMedidaDTO {

    private Integer id;
    private String nombre;
    private String clave;
    private boolean activo;
    private boolean predeterminado;

    public UnidadMedidaDTO() {
    }

    public UnidadMedidaDTO(Integer id, String nombre, String clave, boolean activo, boolean predeterminado) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.activo = activo;
        this.predeterminado = predeterminado;
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isPredeterminado() {
        return predeterminado;
    }

    public void setPredeterminado(boolean predeterminado) {
        this.predeterminado = predeterminado;
    }

}
