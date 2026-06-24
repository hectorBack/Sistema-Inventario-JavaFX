/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.inventario.repository;

import com.inventario.model.DetalleVenta;
import com.inventario.model.Venta;
import java.util.List;

/**
 *
 * @author azulc
 */
public interface VentaRepository {

    // Guarda la cabecera y toda la lista de productos del carrito en una sola transacción
    boolean registrarVenta(Venta venta, List<DetalleVenta> detalles);

    List<Venta> listarTodas();

    List<DetalleVenta> listarDetallesPorVenta(int ventaId);
}
