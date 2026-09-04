package com.inventario.model.DTOs;

public final class ProveedorDTO {

    private final Integer id;
    private final String nombre;
    private final String contacto;
    private final String telefono;
    private final String email;
    private final String estado;

    public ProveedorDTO(Integer id, String nombre, String contacto, String telefono, String email, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getEstado() {
        return estado;
    }
}
