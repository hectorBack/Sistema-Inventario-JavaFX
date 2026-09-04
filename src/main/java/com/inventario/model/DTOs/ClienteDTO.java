package com.inventario.model.DTOs;

public final class ClienteDTO {

    private final Integer id;
    private final String nombre;
    private final String rfc;
    private final String telefono;
    private final String email;
    private final String direccion;
    private final String estado;

    public ClienteDTO(Integer id, String nombre, String rfc, String telefono, String email, String direccion, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.rfc = rfc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEstado() {
        return estado;
    }
}
