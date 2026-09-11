package com.inventario.repository;

import com.inventario.model.DTOs.ConfiguracionImpresoraDTO;

public interface ConfiguracionImpresoraRepository {

    ConfiguracionImpresoraDTO obtenerConfiguracion();

    boolean guardarOActualizar(ConfiguracionImpresoraDTO dto);

}
