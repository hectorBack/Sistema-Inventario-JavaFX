package com.inventario.repository;

import com.inventario.model.DTOs.ConfiguracionTicketDTO;

public interface ConfiguracionTicketRepository {

    ConfiguracionTicketDTO obtenerConfiguracionDTO();

    boolean guardarOActualizarDTO(ConfiguracionTicketDTO dto);
}
