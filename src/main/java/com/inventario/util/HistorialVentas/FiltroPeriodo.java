package com.inventario.util.HistorialVentas;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public enum FiltroPeriodo {
    HOY("Hoy"),
    AYER("Ayer"),
    ESTA_SEMANA("Esta semana"),
    SEMANA_PASADA("La semana pasada"),
    DEL_MES("Del mes"),
    PERIODO_PERSONALIZADO("Periodo personalizado");

    private final String etiqueta;

    FiltroPeriodo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    /**
     * Devuelve un arreglo de LocalDate de tamaño 2: [fechaInicio, fechaFin] Si
     * es PERIODO_PERSONALIZADO devuelve null para indicar que debe usarse los
     * DatePicker.
     */
    public LocalDate[] obtenerRangoFechas() {
        LocalDate hoy = LocalDate.now();

        switch (this) {
            case HOY:
                return new LocalDate[]{hoy, hoy};

            case AYER:
                LocalDate ayer = hoy.minusDays(1);
                return new LocalDate[]{ayer, ayer};

            case ESTA_SEMANA:
                LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate finSemana = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                return new LocalDate[]{inicioSemana, finSemana};

            case SEMANA_PASADA:
                LocalDate inicioSemanaPasada = hoy.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate finSemanaPasada = hoy.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                return new LocalDate[]{inicioSemanaPasada, finSemanaPasada};

            case DEL_MES:
                LocalDate inicioMes = hoy.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate finMes = hoy.with(TemporalAdjusters.lastDayOfMonth());
                return new LocalDate[]{inicioMes, finMes};

            case PERIODO_PERSONALIZADO:
            default:
                return null;
        }
    }
}
