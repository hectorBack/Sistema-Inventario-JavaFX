package com.inventario.repository;

import com.inventario.model.Caja;
import com.inventario.model.DTOs.CajaDTO;
import java.util.List;
import java.util.Optional;

public interface CajaRepository {

    List<Caja> obtenerTodas();

    Optional<Caja> obtenerPorId(int id);

    List<CajaDTO> obtenerTodasDTO();

    Optional<CajaDTO> obtenerPorIdDTO(int id);

    boolean guardar(Caja caja);

    boolean actualizar(Caja caja);

    boolean guardarDTO(CajaDTO caja);

    boolean actualizarDTO(CajaDTO caja);

    boolean eliminar(int id);
}
