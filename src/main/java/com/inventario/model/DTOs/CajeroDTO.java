package com.inventario.model.DTOs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CajeroDTO {
    private final Integer id;
    private final String usuario;
    private final String contrasena;
    private final String nombreCompleto;
    private final boolean activo;
    private final Map<String, Boolean> permisos;

    public CajeroDTO(Integer id, String usuario, String contrasena, String nombreCompleto, boolean activo,
            Map<String, Boolean> permisos) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.activo = activo;
        this.permisos = new LinkedHashMap<>(permisos == null ? Collections.emptyMap() : permisos);
    }

    public Integer getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public String getNombreCompleto() { return nombreCompleto; }
    public boolean isActivo() { return activo; }
    public Map<String, Boolean> getPermisos() { return Collections.unmodifiableMap(permisos); }
    public boolean tienePermiso(String nombre) { return Boolean.TRUE.equals(permisos.get(nombre)); }
}
