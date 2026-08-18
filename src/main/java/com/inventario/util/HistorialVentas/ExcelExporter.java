package com.inventario.util.HistorialVentas;

import com.inventario.model.Venta;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void exportarVentasCSV(List<Venta> ventas, File destino) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(destino); // BOM de UTF-8 para Excel
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); BufferedWriter writer = new BufferedWriter(osw)) {

            // Escribir Byte Order Mark (BOM) para reconocimiento automático de tildes en Excel
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            // Cabeceras
            writer.write("ID Venta,Cliente,Fecha y Hora,Total,Estado\n");

            for (Venta v : ventas) {
                String cliente = (v.getCliente() != null && v.getCliente().getNombre() != null)
                        ? v.getCliente().getNombre()
                        : "Público en General";
                String fecha = (v.getFecha() != null) ? v.getFecha().format(FORMATTER) : "";

                String linea = String.format("%d,\"%s\",\"%s\",%.2f,\"%s\"\n",
                        v.getId(),
                        cliente.replace("\"", "\"\""),
                        fecha,
                        v.getTotal(),
                        v.getEstado() != null ? v.getEstado() : ""
                );
                writer.write(linea);
            }
        }
    }
}
