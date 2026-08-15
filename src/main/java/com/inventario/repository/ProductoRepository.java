package com.inventario.repository;

import com.inventario.model.DetallePaquete;
import com.inventario.model.Producto;
import java.util.List;

public interface ProductoRepository {

    boolean guardar(Producto p);

    boolean actualizar(Producto p);

    // Eliminación
    boolean eliminar(int id);

    boolean eliminarLogico(int id);

    boolean tieneAsociaciones(int id);

    List<Producto> listarTodos();

    List<Producto> listarActivos();     // Util para combos o catálogos

    Producto buscarPorCodigoBarras(String codigo);

    List<Producto> buscarPorNombre(String nombre);

    boolean existeCodigoBarras(String codigo, int idExcluir);

    List<DetallePaquete> obtenerDetallesPaquete(int idPaquete);

    boolean guardarDetallesPaquete(int idPaquete, List<DetallePaquete> detalles);

    boolean reemplazarDetallesPaquete(int idPaquete, List<DetallePaquete> detalles);

}
