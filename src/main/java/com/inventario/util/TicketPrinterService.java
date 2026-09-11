package com.inventario.util;

import com.inventario.model.DTOs.ConfiguracionImpresoraDTO;
import com.inventario.model.DTOs.ConfiguracionTicketDTO;
import com.inventario.model.DTOs.VentaDTO;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TicketPrinterService {

    public boolean imprimir(VentaDTO venta, ConfiguracionTicketDTO configuracionTicket,
            ConfiguracionImpresoraDTO configuracionImpresora) {

        Printer impresora = null;

        if (configuracionImpresora != null && configuracionImpresora.getNombreImpresora() != null
                && !configuracionImpresora.getNombreImpresora().trim().isEmpty()) {
            impresora = Printer.getAllPrinters().stream()
                    .filter(printer -> configuracionImpresora.getNombreImpresora().equalsIgnoreCase(printer.getName()))
                    .findFirst()
                    .orElse(null);
        }

        if (impresora == null) {
            impresora = Printer.getAllPrinters().stream()
                    .filter(p -> p.getName().toLowerCase().contains("microsoft print to pdf"))
                    .findFirst()
                    .orElseGet(() -> Printer.getAllPrinters().stream()
                    .filter(p -> p.getName().toLowerCase().contains("pdf")
                    && !p.getName().toLowerCase().contains("adobe"))
                    .findFirst()
                    .orElse(Printer.getDefaultPrinter()));
        }

        if (impresora == null) {
            return false;
        }

        // Obtener columnas de la configuración (por defecto 36 como en la vista)
        int columnas = (configuracionImpresora != null && configuracionImpresora.getColumnas() > 0)
                ? configuracionImpresora.getColumnas() : 36;

        // Generar texto respetando la cantidad de columnas dinámicas
        String textoTicket = TicketFormateador.generarTextoTicket(venta, configuracionTicket, columnas);

        double tamanoFuente = (configuracionImpresora != null && configuracionImpresora.getTamanoFuente() > 0)
                ? configuracionImpresora.getTamanoFuente() : 10;

        String fuente = (configuracionImpresora != null && configuracionImpresora.getFuente() != null)
                ? configuracionImpresora.getFuente() : "Courier New";

        boolean esNegrita = configuracionImpresora != null && configuracionImpresora.isTodasNegritas();

        Font font = Font.font(fuente, esNegrita ? FontWeight.BOLD : FontWeight.NORMAL, tamanoFuente);

        Text contenido = new Text(textoTicket);
        contenido.setFont(font);

        // ALINEACIÓN EXACTA: 0 deshabilita la quiebra automática en JavaFX 
        // evitando saltos indeseados dentro de las filas monoespaciadas.
        contenido.setWrappingWidth(0);

        PrinterJob job = PrinterJob.createPrinterJob(impresora);
        if (job == null) {
            return false;
        }

        job.getJobSettings().setJobName("Ticket_Venta_" + (venta != null ? venta.getId() : "0"));

        PageLayout pageLayout = impresora.createPageLayout(
                Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

        boolean impreso = job.printPage(pageLayout, contenido);
        if (impreso) {
            job.endJob();
        } else {
            job.cancelJob();
        }

        return impreso;
    }
}
