package com.inventario.model.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class VentaDTO {
    private final Integer id;
    private final Integer clienteId;
    private final String clienteNombre;
    private final LocalDateTime fecha;
    private final BigDecimal total;
    private final String estado;

    public VentaDTO(Integer id, Integer clienteId, LocalDateTime fecha, BigDecimal total, String estado) {
        this(id, clienteId, null, fecha, total, estado);
    }

    public VentaDTO(Integer id, Integer clienteId, String clienteNombre, LocalDateTime fecha, BigDecimal total, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public Integer getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
}
