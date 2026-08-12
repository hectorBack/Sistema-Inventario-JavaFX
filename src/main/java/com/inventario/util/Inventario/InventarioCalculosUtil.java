/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.util.Inventario;

import com.inventario.model.DetallePaquete;
import java.util.List;

/**
 *
 * @author azulc
 */
public class InventarioCalculosUtil {

    public static double calcularPrecioVenta(double costo, double porcentajeGanancia) {
        if (costo <= 0) {
            return 0.0;
        }
        return costo + (costo * (porcentajeGanancia / 100.0));
    }

    public static double calcularCostoTotalPaquete(List<DetallePaquete> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return 0.0;
        }
        return detalles.stream()
                .mapToDouble(DetallePaquete::getSubtotalCosto)
                .sum();
    }
}
