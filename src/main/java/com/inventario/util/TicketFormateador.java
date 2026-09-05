package com.inventario.util;

import com.inventario.model.DTOs.ConfiguracionTicketDTO;
import com.inventario.model.DTOs.DetalleVentaDTO;
import com.inventario.model.DTOs.VentaDTO;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TicketFormateador {

    private static final int ANCHO_TICKET = 40; // Ancho en caracteres para impresora de 80mm

    public static String generarTextoTicket(VentaDTO venta, ConfiguracionTicketDTO config) {
        StringBuilder sb = new StringBuilder();

        // 1. ENCABEZADO (Configuración dinámica)
        if (config != null && config.getLineasEncabezado() != null) {
            for (String linea : config.getLineasEncabezado()) {
                if (linea != null && !linea.trim().isEmpty()) {
                    sb.append(centrarTexto(linea.trim(), ANCHO_TICKET)).append("\n");
                }
            }
        }
        sb.append("\n");

        // 2. FECHA Y HORA (Formato español)
        if (venta.getFecha() != null) {
            DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

            String fechaStr = venta.getFecha().format(fmtFecha);
            String horaStr = venta.getFecha().format(fmtHora);

            sb.append(formatearDosColumnas(fechaStr, horaStr, ANCHO_TICKET)).append("\n");
        }
        sb.append(repetirCaracter('-', ANCHO_TICKET)).append("\n");

        // 3. CABECERA DE TABLA
        sb.append(String.format("%-5s %-22s %11s", "Cant.", "Descripcion", "Importe")).append("\n");
        sb.append(repetirCaracter('-', ANCHO_TICKET)).append("\n");

        // 4. DETALLE DE PRODUCTOS (DetalleVentaDTO)
        if (venta.getDetalles() != null) {
            for (DetalleVentaDTO item : venta.getDetalles()) {
                String cant = truncarTexto(item.getCantidadFormateada(), 5);
                String importe = String.format("$%.2f", item.getSubtotal());

                // Muestra opcional de Precio Unitario
                if (config != null && config.isIncluirPrecioUnitario() && item.getPrecioUnitario() != null) {
                    String precioUnitStr = String.format("@ $%.2f", item.getPrecioUnitario());
                    sb.append(String.format("%-5s %s\n", "", precioUnitStr));
                }

                String nombreProd = item.getNombreProducto() != null ? item.getNombreProducto() : "";

                if (config != null && config.isImprimirDescripcionCompleta()) {
                    // Si sobrepasa el ancho, ajusta renglones
                    sb.append(String.format("%-5s %-22s %11s\n", cant, nombreProd, importe));
                } else {
                    // Cortar si supera el ancho máximo asignado a la columna
                    String descTruncada = truncarTexto(nombreProd, 22);
                    sb.append(String.format("%-5s %-22s %11s\n", cant, descTruncada, importe));
                }
            }
        }

        sb.append(repetirCaracter('-', ANCHO_TICKET)).append("\n");

        // 5. NO. ARTÍCULOS Y TOTAL
        String articulosStr = "No. de Articulos: " + venta.getNumeroArticulos();
        sb.append(centrarTexto(articulosStr, ANCHO_TICKET)).append("\n");

        String totalStr = String.format("Total: $%.2f", venta.getTotal() != null ? venta.getTotal() : 0.0);
        sb.append(centrarTexto(totalStr, ANCHO_TICKET)).append("\n\n");

        // 6. PIE DE PÁGINA (Configuración dinámica)
        if (config != null && config.getLineasPie() != null) {
            for (String linea : config.getLineasPie()) {
                if (linea != null && !linea.trim().isEmpty()) {
                    sb.append(centrarTexto(linea.trim(), ANCHO_TICKET)).append("\n");
                }
            }
        }

        return sb.toString();
    }

    private static String centrarTexto(String texto, int ancho) {
        if (texto == null) {
            return "";
        }
        if (texto.length() >= ancho) {
            return texto.substring(0, ancho);
        }
        int espacios = (ancho - texto.length()) / 2;
        return " ".repeat(espacios) + texto;
    }

    private static String formatearDosColumnas(String izq, String der, int ancho) {
        int espacios = ancho - izq.length() - der.length();
        if (espacios < 1) {
            espacios = 1;
        }
        return izq + " ".repeat(espacios) + der;
    }

    private static String repetirCaracter(char c, int conteo) {
        return String.valueOf(c).repeat(Math.max(0, conteo));
    }

    private static String truncarTexto(String texto, int maxLongitud) {
        if (texto == null) {
            return "";
        }
        return texto.length() > maxLongitud ? texto.substring(0, maxLongitud) : texto;
    }

}
