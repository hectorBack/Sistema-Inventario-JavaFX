package com.inventario.util;

import com.inventario.model.DTOs.ConfiguracionMonedaDTO;
import com.inventario.repository.ConfiguracionMonedaRepository;
import com.inventario.repository.Impl.ConfiguracionMonedaRepositoryImpl;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatoMonedaUtil {
    
    private static String simbolo = "$";
    private static String separadorMiles = ",";
    private static String separadorDecimal = ".";
    private static final ConfiguracionMonedaRepository repository = new ConfiguracionMonedaRepositoryImpl();

    static {
        cargarConfiguracion();
    }

    /**
     * Carga o recarga la configuración desde la base de datos.
     * Llama a este método tras guardar cambios en ConfiguracionMonedaController.
     */
    public static void cargarConfiguracion() {
        ConfiguracionMonedaDTO dto = repository.obtenerConfiguracion();
        if (dto != null) {
            simbolo = valorConfigurado(dto.getSimboloMoneda(), "$" );
            separadorMiles = valorConfigurado(dto.getSeparadorMiles(), ",");
            separadorDecimal = valorConfigurado(dto.getSeparadorDecimal(), ".");
        }
    }

    /**
     * Formatea un valor numérico según la configuración del sistema.
     * Ejemplo con 1234.50 -> "$ 1,234.50"
     */
    public static String formatear(double valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator(separadorMiles.charAt(0));
        simbolos.setDecimalSeparator(separadorDecimal.charAt(0));

        DecimalFormat df = new DecimalFormat("#,##0.00", simbolos);
        return simbolo + " " + df.format(valor);
    }

    public static String formatear(BigDecimal valor) {
        return formatear(valor == null ? 0.0 : valor.doubleValue());
    }

    private static String valorConfigurado(String valor, String valorPorDefecto) {
        return valor == null || valor.trim().isEmpty() ? valorPorDefecto : valor.trim();
    }

    public static String getSimbolo() {
        return simbolo;
    }

}
