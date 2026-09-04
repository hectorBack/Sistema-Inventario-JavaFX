package com.inventario.model.DTOs;

import java.math.BigDecimal;

public final class OpcionesHabilitadasDTO {
    private final Integer id;
    private final boolean usarInventario;
    private final boolean ofrecerCredito;
    private final boolean productoComun;
    private final boolean calcularPrecio;
    private final BigDecimal margenGanancia;
    private final boolean habilitarRedondeo;
    private final String tipoRedondeo;

    public OpcionesHabilitadasDTO(Integer id, boolean usarInventario, boolean ofrecerCredito, boolean productoComun,
            boolean calcularPrecio, BigDecimal margenGanancia, boolean habilitarRedondeo, String tipoRedondeo) {
        this.id = id;
        this.usarInventario = usarInventario;
        this.ofrecerCredito = ofrecerCredito;
        this.productoComun = productoComun;
        this.calcularPrecio = calcularPrecio;
        this.margenGanancia = margenGanancia;
        this.habilitarRedondeo = habilitarRedondeo;
        this.tipoRedondeo = tipoRedondeo;
    }

    public Integer getId() { return id; }
    public boolean isUsarInventario() { return usarInventario; }
    public boolean isOfrecerCredito() { return ofrecerCredito; }
    public boolean isProductoComun() { return productoComun; }
    public boolean isCalcularPrecio() { return calcularPrecio; }
    public BigDecimal getMargenGanancia() { return margenGanancia; }
    public boolean isHabilitarRedondeo() { return habilitarRedondeo; }
    public String getTipoRedondeo() { return tipoRedondeo; }
}
