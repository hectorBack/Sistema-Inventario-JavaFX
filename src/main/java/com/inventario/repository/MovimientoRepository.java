package com.inventario.repository;

import com.inventario.model.MovimientoInventario;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoRepository {

    List<MovimientoInventario> listarTodos();

    boolean registrarMovimiento(MovimientoInventario movimiento);

    // --- MÉTODOS DE CONSULTA Y LÓGICA DE NEGOCIO AGREGADOS ---
    /**
     * Devuelve el stock actual directo de la base de datos para validar stock
     * disponible.
     */
    int obtenerStockActual(int productoId);

    /**
     * Obtiene el historial de movimientos de un producto específico (Kardex).
     */
    List<MovimientoInventario> listarPorProducto(int productoId);

    /**
     * Filtra movimientos por tipo ("ENTRADA" o "SALIDA").
     */
    List<MovimientoInventario> listarPorTipo(String tipoMovimiento);

    /**
     * Filtra movimientos entre dos fechas (útil para auditorías y reportes).
     */
    List<MovimientoInventario> listarPorRangoFechas(LocalDate inicio, LocalDate fin);

    /**
     * Búsqueda combinada para filtrado avanzado desde la interfaz gráfica.
     */
    List<MovimientoInventario> buscarConFiltros(String termino, String tipo, LocalDate fechaInicio, LocalDate fechaFin);

}
