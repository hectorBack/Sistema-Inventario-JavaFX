package com.inventario.util.Ventas;

public class CodigoBarrasParser {

    public static class ResultadoParseo {

        private final String codigo;
        private final double cantidad;

        public ResultadoParseo(String codigo, double cantidad) {
            this.codigo = codigo;
            this.cantidad = cantidad;
        }

        public String getCodigo() {
            return codigo;
        }

        public double getCantidad() {
            return cantidad;
        }
    }

    public static ResultadoParseo parsear(String entrada) throws NumberFormatException {
        if (entrada == null || entrada.trim().isEmpty()) {
            return new ResultadoParseo("", 1.0);
        }

        String entradaLimpia = entrada.trim();
        if (!entradaLimpia.contains("*")) {
            return new ResultadoParseo(entradaLimpia, 1.0);
        }

        String[] partes = entradaLimpia.split("\\*");
        if (partes.length != 2) {
            throw new NumberFormatException("Formato multiplicador inválido");
        }

        String parte1 = partes[0].trim();
        String parte2 = partes[1].trim();

        if (parte1.length() < 5 && parte2.length() >= 5) {
            return new ResultadoParseo(parte2, Double.parseDouble(parte1));
        } else {
            return new ResultadoParseo(parte1, Double.parseDouble(parte2));
        }
    }
}
