package com.inventario.repository;

import com.inventario.model.Cliente;
import java.util.List;

public interface ClienteRepository {

    List<Cliente> listarTodos();

    boolean guardar(Cliente cliente);

    boolean actualizar(Cliente cliente);

    boolean eliminar(int id);

}
