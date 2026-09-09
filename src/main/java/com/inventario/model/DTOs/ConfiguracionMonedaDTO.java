package com.inventario.model.DTOs;

public class ConfiguracionMonedaDTO {

    private Integer id;
    private String simboloMoneda;
    private String separadorMiles;
    private String separadorDecimal;

    public ConfiguracionMonedaDTO() {
    }

    public ConfiguracionMonedaDTO(Integer id, String simboloMoneda, String separadorMiles, String separadorDecimal) {
        this.id = id;
        this.simboloMoneda = simboloMoneda;
        this.separadorMiles = separadorMiles;
        this.separadorDecimal = separadorDecimal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSimboloMoneda() {
        return simboloMoneda;
    }

    public void setSimboloMoneda(String simboloMoneda) {
        this.simboloMoneda = simboloMoneda;
    }

    public String getSeparadorMiles() {
        return separadorMiles;
    }

    public void setSeparadorMiles(String separadorMiles) {
        this.separadorMiles = separadorMiles;
    }

    public String getSeparadorDecimal() {
        return separadorDecimal;
    }

    public void setSeparadorDecimal(String separadorDecimal) {
        this.separadorDecimal = separadorDecimal;
    }

}
