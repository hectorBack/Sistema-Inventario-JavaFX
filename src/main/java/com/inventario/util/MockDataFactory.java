package com.inventario.util;

import com.inventario.model.DTOs.DetalleVentaDTO;
import com.inventario.model.DTOs.VentaDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MockDataFactory {

    public static VentaDTO crearVentaEjemplo() {
        List<DetalleVentaDTO> detalles = List.of(
                new DetalleVentaDTO(1, 1001, 501, "Agua Ciel 600ml", new BigDecimal("2"), new BigDecimal("7.00"), new BigDecimal("14.00")),
                new DetalleVentaDTO(2, 1001, 502, "Coca Cola Light 600ml", new BigDecimal("1"), new BigDecimal("18.00"), new BigDecimal("18.00")),
                new DetalleVentaDTO(3, 1001, 503, "Galletas Chokis 76g", new BigDecimal("2"), new BigDecimal("19.50"), new BigDecimal("39.00")),
                new DetalleVentaDTO(4, 1001, 504, "Sabritas Sal 45g", new BigDecimal("1"), new BigDecimal("17.00"), new BigDecimal("17.00")),
                new DetalleVentaDTO(5, 1001, 505, "Tomate Saladette", new BigDecimal("1.5"), new BigDecimal("24.00"), new BigDecimal("36.00")),
                new DetalleVentaDTO(6, 1001, 506, "Leche Entera LALA 1L", new BigDecimal("2"), new BigDecimal("27.50"), new BigDecimal("55.00"))
        );

        return new VentaDTO(
                1001,
                1,
                "Publico en General",
                LocalDateTime.now(),
                new BigDecimal("179.00"),
                "COMPLETADA",
                detalles
        );
    }

}
