package com.inventario.repository;

import com.inventario.model.Categoria;
import java.util.List;

public interface CategoriaRepository {

    List<Categoria> listarTodas();

    List<Categoria> listarActivas(); // Vital para los ComboBox del módulo Productos

    Categoria buscarPorId(int id);

    boolean guardar(Categoria categoria);

    boolean actualizar(Categoria categoria);

    boolean eliminar(int id);

    boolean desactivar(int id); // Eliminación Lógica

    // Validaciones de integridad y negocio
    boolean existeNombre(String nombre, int idExcluir);

    boolean tieneProductosAsociados(int categoriaId); // Previene FK Constraint Violation

}
