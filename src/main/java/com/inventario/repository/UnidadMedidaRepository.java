package com.inventario.repository;

import com.inventario.model.DTOs.UnidadMedidaDTO;
import java.util.List;

public interface UnidadMedidaRepository {

    List<UnidadMedidaDTO> obtenerTodasDTO();

    List<UnidadMedidaDTO> obtenerActivasDTO();

    boolean actualizarEstadoDTO(int id, boolean activo);

    boolean guardarOActualizarDTO(UnidadMedidaDTO dto);

}
