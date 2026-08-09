package com.inventario.repository;

import com.inventario.model.Producto;
import java.util.List;

public interface ProductoRepository {

    // --- OPERACIONES BÁSICAS Y ESTADO ---
    List<Producto> listarTodos();
    List<Producto> listarActivos();
    Producto buscarPorId(int id);
    
    // --- BÚSQUEDAS DE NEGOCIO ---
    Producto buscarPorCodigoBarras(String codigoBarras);
    List<Producto> buscarPorNombre(String termino);
    List<Producto> listarPorCategoria(int categoriaId);
    List<Producto> listarPorProveedor(int proveedorId);
    
    // --- MONITOREO DE INVENTARIO ---
    List<Producto> listarProductosStockBajo(int limiteMinimo);
    boolean actualizarStock(int productoId, int nuevaCantidad);

    // --- PERSISTENCIA Y VALIDACIONES ---
    boolean guardar(Producto producto);
    boolean actualizar(Producto producto);
    boolean desactivar(int id); // Borrado Lógico (Recomendado)
    boolean eliminar(int id);

    boolean existeCodigoBarras(String codigoBarras, int idExcluir);
    boolean tieneMovimientosAsociados(int productoId);

}
