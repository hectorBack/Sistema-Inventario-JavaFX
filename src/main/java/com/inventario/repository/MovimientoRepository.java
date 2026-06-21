package com.inventario.repository;

import com.inventario.model.MovimientoInventario;
import java.util.List;

public interface MovimientoRepository {

    List<MovimientoInventario> listarTodos();

    boolean registrarMovimiento(MovimientoInventario movimiento);

}
