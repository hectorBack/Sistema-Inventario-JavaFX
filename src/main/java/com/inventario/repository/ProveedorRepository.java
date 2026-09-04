package com.inventario.repository;

import com.inventario.model.Proveedor;
import com.inventario.model.DTOs.ProveedorDTO;
import java.util.List;

public interface ProveedorRepository {

    List<Proveedor> listarTodos();

    List<Proveedor> listarActivos();

    Proveedor buscarPorId(int id);

    List<ProveedorDTO> listarTodosDTO();

    List<ProveedorDTO> listarActivosDTO();

    ProveedorDTO buscarPorIdDTO(int id);

    List<ProveedorDTO> buscarConFiltroDTO(String criterio);

    boolean guardarDTO(ProveedorDTO proveedor);

    boolean actualizarDTO(ProveedorDTO proveedor);

    boolean guardar(Proveedor proveedor);

    boolean actualizar(Proveedor proveedor);

    boolean eliminar(int id); // O dar de baja cambiando el estado a INACTIVO

    boolean desactivar(int id);

    // Validaciones de negocio e integridad
    boolean existeNombre(String nombre, int idExcluir);

    boolean existeEmail(String email, int idExcluir);

    boolean tieneProductosAsociados(int proveedorId);

    List<Proveedor> buscarConFiltro(String criterio);

}
