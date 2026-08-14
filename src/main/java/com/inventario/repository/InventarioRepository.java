package com.inventario.repository;

import com.inventario.model.MovimientoInventario;
import com.inventario.model.Producto;
import java.util.List;

public interface InventarioRepository {
    
    boolean agregarStock(int idProducto, double cantidad);
    List<Producto> obtenerProductosStockBajo();
    List<MovimientoInventario> obtenerHistorialMovimientos();
    
    

}
