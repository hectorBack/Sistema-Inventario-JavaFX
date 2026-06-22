package com.inventario.repository;

import com.inventario.model.Proveedor;
import java.util.List;

public interface ProveedorRepository {

    List<Proveedor> listarTodos();

    boolean guardar(Proveedor proveedor);

    boolean actualizar(Proveedor proveedor);

    boolean eliminar(int id); // O dar de baja cambiando el estado a INACTIVO

}
