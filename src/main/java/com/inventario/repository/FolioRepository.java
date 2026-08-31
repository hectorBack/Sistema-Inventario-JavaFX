package com.inventario.repository;

import com.inventario.model.Folio;
import java.util.List;
import java.util.Optional;

public interface FolioRepository {

    List<Folio> obtenerTodos();

    Optional<Folio> obtenerPorId(int id);

    Optional<Folio> obtenerPorModulo(String modulo);

    boolean guardar(Folio folio);

    boolean actualizar(Folio folio);

    boolean eliminar(int id);

    // Método utilitario para incrementar la secuencia al realizar una venta/operación
    boolean incrementarFolio(String modulo);

}
