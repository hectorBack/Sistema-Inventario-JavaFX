package com.inventario.repository;

import com.inventario.model.DTOs.ImpuestoDTO;
import java.util.List;

public interface ImpuestoRepository {

    List<ImpuestoDTO> obtenerTodosDTO();

    ImpuestoDTO obtenerPorIdDTO(int id);

    boolean guardarOActualizarDTO(ImpuestoDTO dto);

    int aplicarImpuestoAProductos(double porcentaje);

    boolean eliminarDTO(int id);
}
