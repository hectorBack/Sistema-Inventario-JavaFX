package com.inventario.util.Ventas;

import com.inventario.model.Cliente;
import com.inventario.repository.ClienteRepository;
import java.util.List;
import java.util.Optional;

public class ClienteHelper {

    /**
     * Busca al cliente genérico en la base de datos de forma flexible. Si no
     * existe, lo registra automáticamente para evitar asociar ventas al cliente
     * incorrecto.
     */
    public static Cliente obtenerOCrearClienteGeneral(ClienteRepository clienteRepository) {
        List<Cliente> clientesActivos = clienteRepository.listarActivos();

        // 1. Buscamos coincidencias insensibles a tildes y mayúsculas
        Optional<Cliente> clienteGeneralOpt = clientesActivos.stream()
                .filter(c -> {
                    if (c.getNombre() == null) {
                        return false;
                    }
                    String nombreLimpio = c.getNombre().toLowerCase()
                            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
                    return nombreLimpio.contains("publico")
                            || nombreLimpio.contains("general")
                            || nombreLimpio.contains("mostrador");
                })
                .findFirst();

        if (clienteGeneralOpt.isPresent()) {
            return clienteGeneralOpt.get();
        }

        // 2. Si no existe ningún cliente genérico en la BD, lo crea automáticamente
        Cliente nuevoClienteGeneral = new Cliente();
        nuevoClienteGeneral.setNombre("Público en General");
        nuevoClienteGeneral.setEstado("ACTIVO");

        if (clienteRepository.guardar(nuevoClienteGeneral)) {
            return clienteRepository.listarActivos().stream()
                    .filter(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase("Público en General"))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
