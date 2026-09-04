package com.inventario.repository;

import com.inventario.model.Cajero;
import com.inventario.model.DTOs.CajeroDTO;
import java.util.List;
import java.util.Optional;

public interface CajeroRepository {

    boolean guardar(Cajero cajero);

    boolean actualizar(Cajero cajero);

    Optional<Cajero> buscarPorId(int id);

    Optional<Cajero> buscarPorUsuario(String usuario);

    Optional<CajeroDTO> buscarPorIdDTO(int id);

    Optional<CajeroDTO> buscarPorUsuarioDTO(String usuario);

    List<CajeroDTO> obtenerTodosActivosDTO();

    List<CajeroDTO> buscarPorCriterioDTO(String textoBusqueda);

    boolean guardarDTO(CajeroDTO cajero);

    boolean actualizarDTO(CajeroDTO cajero);

    List<Cajero> obtenerTodosActivos();

    List<Cajero> buscarPorCriterio(String textoBusqueda);

    // --- MÉTODOS DE ELIMINACIÓN ---
    boolean darDeBajaLogica(int id);  // Cambia activo = false

    boolean eliminarFisico(int id);    // DELETE de la base de datos

}
