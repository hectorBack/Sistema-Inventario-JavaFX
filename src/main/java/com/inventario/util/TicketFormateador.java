package com.inventario.util;

import com.inventario.model.DTOs.ConfiguracionTicketDTO;
import com.inventario.model.DTOs.DetalleVentaDTO;
import com.inventario.model.DTOs.VentaDTO;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TicketFormateador {

    public static String generarTextoTicket(VentaDTO venta, ConfiguracionTicketDTO config, int columnas) {
        if (columnas <= 0) {
            columnas = 36; // Fallback por defecto si no viene de la BD
        }

        StringBuilder sb = new StringBuilder();

        // 1. ENCABEZADO
        if (config != null && config.getLineasEncabezado() != null) {
            for (String linea : config.getLineasEncabezado()) {
                if (linea != null && !linea.trim().isEmpty()) {
                    sb.append(centrarTexto(linea.trim(), columnas)).append("\n");
                }
            }
        }
        sb.append("\n");

        // 2. FECHA Y HORA
        if (venta != null && venta.getFecha() != null) {
            DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

            String fechaStr = "Fecha: " + venta.getFecha().format(fmtFecha);
            String horaStr = "Hora: " + venta.getFecha().format(fmtHora);

            sb.append(formatearDosColumnas(fechaStr, horaStr, columnas)).append("\n");
        }
        sb.append(repetirCaracter('-', columnas)).append("\n");

        // 3. CABECERA DE TABLA DINÁMICA
        // Anchos: Cantidad(4), Importe(9). El resto se asigna a la Descripción.
        int anchoCant = 4;
        int anchoImporte = 9;
        int anchoDesc = columnas - anchoCant - anchoImporte - 2; // -2 por los dos espacios separadores

        if (anchoDesc < 5) {
            anchoDesc = 5; // Seguridad mínima
        }

        String formatoFila = "%-" + anchoCant + "s %-" + anchoDesc + "s %" + anchoImporte + "s";
        sb.append(String.format(formatoFila, "Cant", "Descripcion", "Importe")).append("\n");
        sb.append(repetirCaracter('-', columnas)).append("\n");

        // 4. DETALLE DE PRODUCTOS
        if (venta != null && venta.getDetalles() != null) {
            for (DetalleVentaDTO item : venta.getDetalles()) {
                String cant = truncarTexto(item.getCantidadFormateada(), anchoCant);
                String importe = FormatoMonedaUtil.formatear(item.getSubtotal());
                String nombreProd = item.getNombreProducto() != null ? item.getNombreProducto() : "";

                // Muestra opcional de Precio Unitario
                if (config != null && config.isIncluirPrecioUnitario() && item.getPrecioUnitario() != null) {
                    String precioUnitStr = "@ " + FormatoMonedaUtil.formatear(item.getPrecioUnitario());
                    sb.append(String.format("%-" + anchoCant + "s %s\n", "", precioUnitStr));
                }

                if (config != null && config.isImprimirDescripcionCompleta()) {
                    // Si sobrepasa la columna, la imprime entera ocupando la fila
                    sb.append(String.format(formatoFila, cant, nombreProd, importe)).append("\n");
                } else {
                    String descTruncada = truncarTexto(nombreProd, anchoDesc);
                    sb.append(String.format(formatoFila, cant, descTruncada, importe)).append("\n");
                }
            }
        }

        sb.append(repetirCaracter('-', columnas)).append("\n");

        // 5. NO. ARTÍCULOS Y TOTAL
        if (venta != null) {
            String articulosStr = "No. de Articulos: " + venta.getNumeroArticulos();
            sb.append(centrarTexto(articulosStr, columnas)).append("\n");

            String totalStr = "Total: " + FormatoMonedaUtil.formatear(
                    venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO);
            sb.append(centrarTexto(totalStr, columnas)).append("\n\n");
        }

        // 6. PIE DE PÁGINA
        if (config != null && config.getLineasPie() != null) {
            for (String linea : config.getLineasPie()) {
                if (linea != null && !linea.trim().isEmpty()) {
                    sb.append(centrarTexto(linea.trim(), columnas)).append("\n");
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
        if (izq == null) {
            izq = "";
        }
        if (der == null) {
            der = "";
        }

        // Si juntas exceden el ancho, se truncan proporcionalmente
        if ((izq.length() + der.length() + 1) > ancho) {
            int maxIzq = ancho - der.length() - 1;
            if (maxIzq > 0) {
                izq = izq.substring(0, maxIzq);
            }
        }
        int espacios = ancho - izq.length() - der.length();
        return izq + " ".repeat(Math.max(1, espacios)) + der;
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
