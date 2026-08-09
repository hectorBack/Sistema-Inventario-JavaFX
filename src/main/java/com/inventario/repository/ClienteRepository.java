package com.inventario.repository;

import com.inventario.model.Cliente;
import java.util.List;

public interface ClienteRepository {

    List<Cliente> listarTodos();

    List<Cliente> listarActivos();

    Cliente buscarPorId(int id);

    boolean guardar(Cliente cliente);

    boolean actualizar(Cliente cliente);

    boolean eliminar(int id);

    boolean desactivar(int id);

    // Validaciones de negocio e integridad
    boolean existeRfc(String rfc, int idExcluir);

    boolean existeEmail(String email, int idExcluir);

    boolean tieneVentasAsociadas(int clienteId);

    List<Cliente> buscarConFiltro(String criterio);

}
