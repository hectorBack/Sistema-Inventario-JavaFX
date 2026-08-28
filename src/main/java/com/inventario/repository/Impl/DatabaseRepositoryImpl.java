package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.ConexionConfig;
import com.inventario.model.InformacionBD;
import com.inventario.repository.DatabaseRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseRepositoryImpl implements DatabaseRepository {

    private static final String ARCHIVO_CONFIG = "config.properties";

    @Override
    public ConexionConfig cargarConfiguracion() {
        Properties prop = new Properties();
        ConexionConfig config = new ConexionConfig();

        File file = new File(ARCHIVO_CONFIG);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
                config.setHost(prop.getProperty("db.host", "localhost"));
                config.setPuerto(prop.getProperty("db.puerto", "5432"));
                config.setBaseDatos(prop.getProperty("db.nombre", "Inventario"));
                config.setUsuario(prop.getProperty("db.usuario", "postgres"));
                config.setContrasena(prop.getProperty("db.password", ""));
                config.setRutaPgDump(prop.getProperty("db.ruta_pgdump", "C:\\Program Files\\PostgreSQL\\15\\bin"));
            } catch (IOException e) {
                System.out.println("Error al cargar config.properties: " + e.getMessage());
            }
        }
        return config;
    }

    @Override
    public boolean guardarConfiguracion(ConexionConfig config) {
        Properties prop = new Properties();
        prop.setProperty("db.host", config.getHost());
        prop.setProperty("db.puerto", config.getPuerto());
        prop.setProperty("db.nombre", config.getBaseDatos());
        prop.setProperty("db.usuario", config.getUsuario());
        prop.setProperty("db.password", config.getContrasena());
        prop.setProperty("db.ruta_pgdump", config.getRutaPgDump() != null ? config.getRutaPgDump() : "");

        try (FileOutputStream fos = new FileOutputStream(ARCHIVO_CONFIG)) {
            prop.store(fos, "Configuración de la Base de Datos PostgreSQL");
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar en config.properties: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean probarConexion(ConexionConfig config) {
        String url = String.format("jdbc:postgresql://%s:%s/%s", config.getHost(), config.getPuerto(), config.getBaseDatos());
        try (Connection conn = DriverManager.getConnection(url, config.getUsuario(), config.getContrasena())) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.out.println("Fallo al probar la conexión: " + e.getMessage());
            return false;
        }
    }

    @Override
    public InformacionBD obtenerDiagnosticoBD() {
        InformacionBD info = new InformacionBD();

        try (Connection conn = ConexionDB.getConexion()) {
            if (conn == null || conn.isClosed()) {
                info.setEstadoConexion(false);
                return info;
            }

            info.setEstadoConexion(true);

            try (Statement stmt = conn.createStatement()) {
                // Versión de PostgreSQL
                ResultSet rsVersion = stmt.executeQuery("SELECT version();");
                if (rsVersion.next()) {
                    String fullVersion = rsVersion.getString(1);
                    // Extraer solo la primera parte (ej. PostgreSQL 15.2)
                    info.setMotorVersion(fullVersion.split(",")[0]);
                }

                // Tamaño de la Base de Datos
                ConexionConfig cfg = cargarConfiguracion();
                ResultSet rsTamanio = stmt.executeQuery(
                        String.format("SELECT pg_size_pretty(pg_database_size('%s'));", cfg.getBaseDatos())
                );
                if (rsTamanio.next()) {
                    info.setTamanioBD(rsTamanio.getString(1));
                }

                // Conteo de Cajeros
                ResultSet rsCajeros = stmt.executeQuery("SELECT COUNT(*) FROM cajeros;");
                if (rsCajeros.next()) {
                    info.setTotalCajeros(String.valueOf(rsCajeros.getInt(1)));
                }

                // Conteo de Productos (ajusta el nombre de tu tabla si difiere)
                try {
                    ResultSet rsProd = stmt.executeQuery("SELECT COUNT(*) FROM productos;");
                    if (rsProd.next()) {
                        info.setTotalProductos(String.valueOf(rsProd.getInt(1)));
                    }
                } catch (Exception e) {
                    info.setTotalProductos("N/A");
                }

                // Conteo de Ventas (ajusta el nombre de tu tabla si difiere)
                try {
                    ResultSet rsVentas = stmt.executeQuery("SELECT COUNT(*) FROM ventas;");
                    if (rsVentas.next()) {
                        info.setTotalVentas(String.valueOf(rsVentas.getInt(1)));
                    }
                } catch (Exception e) {
                    info.setTotalVentas("N/A");
                }

            }
        } catch (Exception e) {
            System.out.println("Error obteniendo diagnóstico: " + e.getMessage());
            info.setEstadoConexion(false);
        }

        return info;
    }

    @Override
    public boolean generarRespaldo(ConexionConfig config, File archivoDestino) {
        String rutaPgDump = config.getRutaPgDump();
        String ejecutable = (rutaPgDump != null && !rutaPgDump.isEmpty())
                ? new File(rutaPgDump, "pg_dump").getAbsolutePath()
                : "pg_dump";

        ProcessBuilder pb = new ProcessBuilder(
                ejecutable,
                "-h", config.getHost(),
                "-p", config.getPuerto(),
                "-U", config.getUsuario(),
                "-F", "c", // Formato personalizado (comprimido)
                "-b",
                "-v",
                "-f", archivoDestino.getAbsolutePath(),
                config.getBaseDatos()
        );

        pb.environment().put("PGPASSWORD", config.getContrasena());

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            System.out.println("Error al generar respaldo con pg_dump: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restaurarRespaldo(ConexionConfig config, File archivoOrigen) {
        String rutaPgDump = config.getRutaPgDump();
        String ejecutable = (rutaPgDump != null && !rutaPgDump.isEmpty())
                ? new File(rutaPgDump, "pg_restore").getAbsolutePath()
                : "pg_restore";

        ProcessBuilder pb = new ProcessBuilder(
                ejecutable,
                "-h", config.getHost(),
                "-p", config.getPuerto(),
                "-U", config.getUsuario(),
                "-d", config.getBaseDatos(),
                "-v",
                "--clean", // Limpia la estructura previa antes de restaurar
                archivoOrigen.getAbsolutePath()
        );

        pb.environment().put("PGPASSWORD", config.getContrasena());

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            System.out.println("Error al restaurar respaldo con pg_restore: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean optimizarBaseDatos() {
        try (Connection conn = ConexionDB.getConexion(); Statement stmt = conn.createStatement()) {

            // VACUUM ANALYZE limpia espacio muerto y reindexa estadísticas en PostgreSQL
            stmt.execute("VACUUM ANALYZE;");
            return true;
        } catch (Exception e) {
            System.out.println("Error ejecutando optimización (VACUUM ANALYZE): " + e.getMessage());
            return false;
        }
    }

}
