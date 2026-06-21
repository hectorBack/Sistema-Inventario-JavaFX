
package com.inventario.repository;

import com.inventario.model.Producto;
import java.util.List;


public interface ProductoRepository {

    List<Producto> listarTodos();

    boolean guardar(Producto producto);

    boolean actualizar(Producto producto);

    boolean eliminar(int id);

}
