package com.inventario.repository;

import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import java.time.LocalDate;
import java.util.List;

public interface VentaRepository {

    // Guarda la cabecera y toda la lista de productos del carrito en una sola transacción
    boolean registrarVenta(Venta venta, List<DetalleVenta> detalles);

    boolean cancelarVenta(int ventaId);

    List<Venta> listarTodas();

    List<Venta> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);

    List<DetalleVenta> listarDetallesPorVenta(int ventaId);

    List buscarConFiltros(LocalDate inicio, LocalDate fin, Integer clienteId);
}
