package com.inventario.repository;

import com.inventario.model.DetallePaquete;
import com.inventario.model.Producto;
import java.util.List;

public interface ProductoRepository {

    boolean guardar(Producto p);
    boolean actualizar(Producto p);
    
    boolean eliminar(int id);
    List<Producto> listarTodos();
    Producto buscarPorCodigoBarras(String codigo);
    List<Producto> buscarPorNombre(String nombre);
    boolean existeCodigoBarras(String codigo, int idExcluir);
    List<DetallePaquete> obtenerDetallesPaquete(int idPaquete);
    boolean guardarDetallesPaquete(int idPaquete, List<DetallePaquete> detalles);
    boolean reemplazarDetallesPaquete(int idPaquete, List<DetallePaquete> detalles);
    
}
