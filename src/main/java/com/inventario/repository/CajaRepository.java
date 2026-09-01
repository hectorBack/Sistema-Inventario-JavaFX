package com.inventario.repository;

import com.inventario.model.Caja;
import java.util.List;
import java.util.Optional;

public interface CajaRepository {

    List<Caja> obtenerTodas();

    Optional<Caja> obtenerPorId(int id);

    boolean guardar(Caja caja);

    boolean actualizar(Caja caja);

    boolean eliminar(int id);
}
