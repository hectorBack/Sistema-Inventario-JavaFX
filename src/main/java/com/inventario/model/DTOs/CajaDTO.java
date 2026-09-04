package com.inventario.model.DTOs;

import java.time.LocalDateTime;

public final class CajaDTO {
    private final Integer id;
    private final String nombre;
    private final String estado;
    private final String tipo;
    private final Integer cajaPadreId;
    private final LocalDateTime fechaUltimoAcceso;

    public CajaDTO(Integer id, String nombre, String estado, String tipo, Integer cajaPadreId, LocalDateTime fechaUltimoAcceso) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.tipo = tipo;
        this.cajaPadreId = cajaPadreId;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public String getTipo() { return tipo; }
    public Integer getCajaPadreId() { return cajaPadreId; }
    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
}
