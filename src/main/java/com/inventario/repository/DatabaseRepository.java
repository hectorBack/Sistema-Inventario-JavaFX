package com.inventario.repository;

import com.inventario.model.ConexionConfig;
import com.inventario.model.InformacionBD;
import java.io.File;

public interface DatabaseRepository {

    /**
     * Carga la configuración guardada en el archivo config.properties
     */
    ConexionConfig cargarConfiguracion();

    /**
     * Guarda la configuración de conexión en el archivo config.properties
     */
    boolean guardarConfiguracion(ConexionConfig config);

    /**
     * Prueba si la conexión es exitosa con los parámetros dados sin guardar la
     * configuración
     */
    boolean probarConexion(ConexionConfig config);

    /**
     * Obtiene métricas y estadísticas del estado de la BD desde PostgreSQL
     */
    InformacionBD obtenerDiagnosticoBD();

    /**
     * Genera un respaldo (.sql o .backup) utilizando pg_dump de PostgreSQL
     */
    boolean generarRespaldo(ConexionConfig config, File archivoDestino);

    /**
     * Restaura la BD desde un archivo de respaldo utilizando psql/pg_restore
     */
    boolean restaurarRespaldo(ConexionConfig config, File archivoOrigen);

    /**
     * Ejecuta el comando VACUUM ANALYZE en PostgreSQL para optimizar el
     * almacenamiento e índices
     */
    boolean optimizarBaseDatos();

}
