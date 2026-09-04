package com.inventario.model.DTOs;

import java.time.LocalDateTime;

public final class EmpresaDTO {
    private final Integer id;
    private final String nombreEmpresa;
    private final String rfc;
    private final String telefono;
    private final String direccion;
    private final String logoPath;
    private final LocalDateTime fechaActualizacion;

    public EmpresaDTO(Integer id, String nombreEmpresa, String rfc, String telefono, String direccion,
            String logoPath, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombreEmpresa = nombreEmpresa;
        this.rfc = rfc;
        this.telefono = telefono;
        this.direccion = direccion;
        this.logoPath = logoPath;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getId() { return id; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getRfc() { return rfc; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getLogoPath() { return logoPath; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
