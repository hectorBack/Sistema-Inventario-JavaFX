package com.inventario.model.DTOs;

public final class CategoriaDTO {
    private final Integer id;
    private final String nombre;
    private final String estado;

    public CategoriaDTO(Integer id, String nombre, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
}
