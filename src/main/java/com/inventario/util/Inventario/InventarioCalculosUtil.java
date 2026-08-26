package com.inventario.util.Inventario;

import com.inventario.model.DetallePaquete;
import java.util.List;

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

    public static double aplicarRedondeo(double total, boolean habilitado, String tipoRedondeo) {
        if (!habilitado || tipoRedondeo == null || tipoRedondeo.trim().isEmpty()) {
            return total;
        }

        // Normalización: Mayúsculas y remoción de acentos para mayor tolerancia
        String tipo = tipoRedondeo.trim().toUpperCase()
                .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U");

        if (tipo.contains("CLIENTE")) {
            return Math.floor(total);
        } else if (tipo.contains("NEGOCIO")) {
            return Math.ceil(total);
        } else if (tipo.contains("CERCANO") || tipo.contains("PESO")) {
            return Math.round(total);
        }

        return total;
    }
}
