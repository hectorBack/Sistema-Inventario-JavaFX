package com.inventario.repository;

import com.inventario.model.Promocion;
import com.inventario.model.DTOs.PromocionDTO;
import java.util.List;

public interface PromocionRepository {

    boolean guardar(Promocion promocion);

    boolean actualizar(Promocion promocion);

    boolean eliminar(int id);

    List<Promocion> listarActivas();

    List<Promocion> listarTodas();

    Promocion buscarPorId(int id);

    List<Promocion> buscarPorNombre(String nombre);

    List<PromocionDTO> listarActivasDTO();

    List<PromocionDTO> listarTodasDTO();

    PromocionDTO buscarPorIdDTO(int id);

    List<PromocionDTO> buscarPorNombreDTO(String nombre);

    boolean guardarDTO(PromocionDTO promocion);

    boolean actualizarDTO(PromocionDTO promocion);

    /**
     * Verifica si existe conflicto de rangos para un código de producto específico.
     * @param codigoBarras Código del producto
     * @param cantidadDesde Cantidad inicial del rango
     * @param cantidadHasta Cantidad final del rango
     * @param idExcluir ID de la promoción a excluir (útil para actualizaciones)
     * @return null si no hay conflicto, o una descripción del conflicto si lo hay
     */
    String verificarConflictoDeRango(String codigoBarras, double cantidadDesde, double cantidadHasta, int idExcluir);
}
