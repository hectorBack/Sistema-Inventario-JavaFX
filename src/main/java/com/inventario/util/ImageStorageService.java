package com.inventario.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageStorageService {

    private static final String DIRECTORIO_BASE = System.getProperty("user.home")
            + File.separator + ".gestion_inventario"
            + File.separator + "assets";

    public static String guardarLogoLocalmente(File archivoOrigen) throws IOException {
        if (archivoOrigen == null || !archivoOrigen.exists()) {
            return null;
        }

        Path destinoDir = Paths.get(DIRECTORIO_BASE);
        if (!Files.exists(destinoDir)) {
            Files.createDirectories(destinoDir);
        }

        String nombreArchivo = archivoOrigen.getName();
        String extension = "";
        int i = nombreArchivo.lastIndexOf('.');
        if (i > 0) {
            extension = nombreArchivo.substring(i);
        }

        Path destinoPath = destinoDir.resolve("logo_empresa" + extension);
        Files.copy(archivoOrigen.toPath(), destinoPath, StandardCopyOption.REPLACE_EXISTING);

        return destinoPath.toString();
    }

    public static boolean eliminarLogoLocal(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            return false;
        }
        try {
            Path path = Paths.get(rutaArchivo);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
