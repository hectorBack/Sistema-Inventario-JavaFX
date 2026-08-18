package com.inventario.util.HistorialVentas;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

public class ReportePrinterManager {

    public static boolean imprimirNodo(Node nodo) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            Printer printer = job.getPrinter();
            PageLayout pageLayout = printer.createPageLayout(
                    Paper.A4,
                    PageOrientation.PORTRAIT,
                    Printer.MarginType.DEFAULT
            );

            // Ajustar escala si el nodo supera el tamaño de la página
            double scaleX = pageLayout.getPrintableWidth() / nodo.getBoundsInParent().getWidth();
            double scaleY = pageLayout.getPrintableHeight() / nodo.getBoundsInParent().getHeight();
            double scale = Math.min(scaleX, scaleY);

            if (scale < 1.0) {
                nodo.getTransforms().add(new Scale(scale, scale));
            }

            boolean success = job.printPage(pageLayout, nodo);
            if (success) {
                job.endJob();
            }

            // Revertir escala aplicada
            if (scale < 1.0) {
                nodo.getTransforms().clear();
            }

            return success;
        }
        return false;
    }
}
