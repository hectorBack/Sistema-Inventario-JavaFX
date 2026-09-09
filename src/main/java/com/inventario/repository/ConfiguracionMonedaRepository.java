package com.inventario.repository;

import com.inventario.model.DTOs.ConfiguracionMonedaDTO;

public interface ConfiguracionMonedaRepository {

    ConfiguracionMonedaDTO obtenerConfiguracion();

    boolean guardarOActualizarDTO(ConfiguracionMonedaDTO dto);

}
