package com.inventario.model.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class VentaDTO {

    private final Integer id;
    private final Integer clienteId;
    private final String clienteNombre;
    private final LocalDateTime fecha;
    private final BigDecimal total;
    private final String estado;
    private final List<DetalleVentaDTO> detalles;

    public VentaDTO(Integer id, Integer clienteId, LocalDateTime fecha, BigDecimal total, String estado) {
        this(id, clienteId, null, fecha, total, estado, Collections.emptyList());
    }

    public VentaDTO(Integer id, Integer clienteId, String clienteNombre, LocalDateTime fecha, BigDecimal total, String estado) {
        this(id, clienteId, clienteNombre, fecha, total, estado, Collections.emptyList());
    }

    public VentaDTO(Integer id, Integer clienteId, String clienteNombre, LocalDateTime fecha,
            BigDecimal total, String estado, List<DetalleVentaDTO> detalles) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.detalles = detalles != null ? List.copyOf(detalles) : Collections.emptyList();
    }

    public Integer getId() {
        return id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getEstado() {
        return estado;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    /**
     * Calcula el número total de ítems/artículos comprados en la venta.
     */
    public int getNumeroArticulos() {
        if (detalles == null || detalles.isEmpty()) {
            return 0;
        }
        return detalles.stream()
                .mapToInt(d -> d.getCantidad() != null ? d.getCantidad().intValue() : 0)
                .sum();
    }
}
