package com.inventario.repository;

import com.inventario.model.ArticulosPrecargados;
import com.inventario.model.DTOs.ArticulosPrecargadosDTO;
import java.io.File;
import java.util.List;

public interface ArticulosPrecargadosRepository {

    /**
     * Lee y parsea un archivo CSV o Excel con la lista de artículos a
     * precargar.
     */
    List<ArticulosPrecargados> leerArticulosDesdeArchivo(File archivo);

    /**
     * Inserta en lote (batch) la lista de artículos precargados en la base de
     * datos PostgreSQL.
     *
     * @param articulos Lista de artículos validados a insertar.
     * @param modoDuplicados 0 = Omitir existentes, 1 = Actualizar existentes.
     * @return El número de registros insertados o procesados con éxito.
     */
    int guardarArticulosEnLote(List<ArticulosPrecargados> articulos, int modoDuplicados);

    /**
     * Genera un archivo CSV de plantilla de ejemplo con la estructura adecuada.
     */
    boolean generarPlantillaEjemplo(File archivoDestino);

    /**
     * Obtiene una lista de artículos precargados según un catálogo o giro
     * comercial base (ej. Abarrotes).
     */
    List<ArticulosPrecargados> obtenerCatalogoBasePorGiro(String giro);

    List<ArticulosPrecargadosDTO> leerArticulosDesdeArchivoDTO(File archivo);

    int guardarArticulosEnLoteDTO(List<ArticulosPrecargadosDTO> articulos, int modoDuplicados);

    List<ArticulosPrecargadosDTO> obtenerCatalogoBasePorGiroDTO(String giro);

}
