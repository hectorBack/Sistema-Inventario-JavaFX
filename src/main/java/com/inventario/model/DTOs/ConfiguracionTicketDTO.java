package com.inventario.model.DTOs;

import java.util.List;

public class ConfiguracionTicketDTO {

    private final Integer id;
    private final List<String> lineasEncabezado;
    private final List<String> lineasPie;
    private final boolean incluirPrecioUnitario;
    private final boolean imprimirDescripcionCompleta;

    public ConfiguracionTicketDTO(Integer id, List<String> lineasEncabezado, List<String> lineasPie,
            boolean incluirPrecioUnitario, boolean imprimirDescripcionCompleta) {
        this.id = id;
        this.lineasEncabezado = lineasEncabezado;
        this.lineasPie = lineasPie;
        this.incluirPrecioUnitario = incluirPrecioUnitario;
        this.imprimirDescripcionCompleta = imprimirDescripcionCompleta;
    }

    public Integer getId() {
        return id;
    }

    public List<String> getLineasEncabezado() {
        return lineasEncabezado;
    }

    public List<String> getLineasPie() {
        return lineasPie;
    }

    public boolean isIncluirPrecioUnitario() {
        return incluirPrecioUnitario;
    }

    public boolean isImprimirDescripcionCompleta() {
        return imprimirDescripcionCompleta;
    }

}
