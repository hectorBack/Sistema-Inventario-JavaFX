package com.inventario.repository;

import com.inventario.model.Categoria;
import java.util.List;

public interface CategoriaRepository {

    List<Categoria> listarTodas();

    boolean guardar(Categoria categoria);

    boolean actualizar(Categoria categoria);

    boolean eliminar(int id);

}
