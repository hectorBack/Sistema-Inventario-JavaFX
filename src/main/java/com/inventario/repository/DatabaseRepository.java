package com.inventario.repository;

import com.inventario.model.ConexionConfig;
import com.inventario.model.InformacionBD;
import com.inventario.model.DTOs.ConexionConfigDTO;
import com.inventario.model.DTOs.InformacionBDDTO;
import java.io.File;

public interface DatabaseRepository {

    /**
     * Carga la configuración guardada en el archivo config.properties
     */
    ConexionConfig cargarConfiguracion();

    ConexionConfigDTO cargarConfiguracionDTO();

    /**
     * Guarda la configuración de conexión en el archivo config.properties
     */
    boolean guardarConfiguracion(ConexionConfig config);

    boolean guardarConfiguracionDTO(ConexionConfigDTO config);

    /**
     * Prueba si la conexión es exitosa con los parámetros dados sin guardar la
     * configuración
     */
    boolean probarConexion(ConexionConfig config);

    boolean probarConexionDTO(ConexionConfigDTO config);

    /**
     * Obtiene métricas y estadísticas del estado de la BD desde PostgreSQL
     */
    InformacionBD obtenerDiagnosticoBD();

    InformacionBDDTO obtenerDiagnosticoBDDTO();

    /**
     * Genera un respaldo (.sql o .backup) utilizando pg_dump de PostgreSQL
     */
    boolean generarRespaldo(ConexionConfig config, File archivoDestino);

    /**
     * Restaura la BD desde un archivo de respaldo utilizando psql/pg_restore
     */
    boolean restaurarRespaldo(ConexionConfig config, File archivoOrigen);

    boolean generarRespaldoDTO(ConexionConfigDTO config, File archivoDestino);

    boolean restaurarRespaldoDTO(ConexionConfigDTO config, File archivoOrigen);

    /**
     * Ejecuta el comando VACUUM ANALYZE en PostgreSQL para optimizar el
     * almacenamiento e índices
     */
    boolean optimizarBaseDatos();

}
