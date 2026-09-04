package com.inventario.model.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class VentaDTO {
    private final Integer id;
    private final Integer clienteId;
    private final LocalDateTime fecha;
    private final BigDecimal total;
    private final String estado;

    public VentaDTO(Integer id, Integer clienteId, LocalDateTime fecha, BigDecimal total, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public Integer getClienteId() { return clienteId; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
}
