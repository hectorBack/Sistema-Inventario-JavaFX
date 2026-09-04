package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.ArticulosPrecargados;
import com.inventario.model.DTOs.ArticulosPrecargadosDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.ArticulosPrecargadosRepository;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticulosPrecargadosRepositoryImpl implements ArticulosPrecargadosRepository {

    @Override
    public List<ArticulosPrecargadosDTO> leerArticulosDesdeArchivoDTO(File archivo) {
        List<ArticulosPrecargadosDTO> resultado = new ArrayList<>();
        for (ArticulosPrecargados articulo : leerArticulosDesdeArchivo(archivo)) {
            resultado.add(DTOMapper.toDTO(articulo));
        }
        return resultado;
    }

    @Override
    public int guardarArticulosEnLoteDTO(List<ArticulosPrecargadosDTO> articulos, int modoDuplicados) {
        List<ArticulosPrecargados> modelos = new ArrayList<>();
        for (ArticulosPrecargadosDTO articulo : articulos) {
            modelos.add(DTOMapper.toModel(articulo));
        }
        return guardarArticulosEnLote(modelos, modoDuplicados);
    }

    @Override
    public List<ArticulosPrecargadosDTO> obtenerCatalogoBasePorGiroDTO(String giro) {
        List<ArticulosPrecargadosDTO> resultado = new ArrayList<>();
        for (ArticulosPrecargados articulo : obtenerCatalogoBasePorGiro(giro)) {
            resultado.add(DTOMapper.toDTO(articulo));
        }
        return resultado;
    }

    @Override
    public List<ArticulosPrecargados> leerArticulosDesdeArchivo(File archivo) {
        List<ArticulosPrecargados> lista = new ArrayList<>();

        if (archivo == null || !archivo.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Omitir cabecera (encabezados de columna)
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linea.split(",");
                if (campos.length >= 5) {
                    try {
                        String codigo = campos[0].trim().replaceAll("^\"|\"$", "");
                        String nombre = campos[1].trim().replaceAll("^\"|\"$", "");
                        String descripcion = campos.length > 2 ? campos[2].trim().replaceAll("^\"|\"$", "") : "";
                        double precioCompra = Double.parseDouble(campos[3].trim());
                        double precioVenta = Double.parseDouble(campos[4].trim());
                        int stock = campos.length > 5 ? Integer.parseInt(campos[5].trim()) : 0;
                        String categoria = campos.length > 6 ? campos[6].trim().replaceAll("^\"|\"$", "") : "General";

                        ArticulosPrecargados articulo = new ArticulosPrecargados(
                                codigo, nombre, descripcion, precioCompra, precioVenta, stock, categoria
                        );

                        // Validación simple en memoria
                        if (codigo.isEmpty() || nombre.isEmpty()) {
                            articulo.setValido(false);
                            articulo.setMensajeError("Código y nombre son obligatorios");
                        }

                        lista.add(articulo);
                    } catch (NumberFormatException e) {
                        ArticulosPrecargados artError = new ArticulosPrecargados();
                        artError.setValido(false);
                        artError.setMensajeError("Error de formato numérico en la fila");
                        lista.add(artError);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public int guardarArticulosEnLote(List<ArticulosPrecargados> articulos, int modoDuplicados) {
        // 1. Sentencia para insertar o actualizar productos usando las llaves foráneas
        String sql;
        if (modoDuplicados == 1) { // Modo Actualizar si ya existe el código de barras
            sql = "INSERT INTO productos (codigo_barras, nombre, descripcion, precio_compra, precio, stock, id_categoria, id_proveedor) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (codigo_barras) DO UPDATE SET "
                    + "nombre = EXCLUDED.nombre, "
                    + "descripcion = EXCLUDED.descripcion, "
                    + "precio_compra = EXCLUDED.precio_compra, "
                    + "precio = EXCLUDED.precio, "
                    + "stock = productos.stock + EXCLUDED.stock, "
                    + "id_categoria = EXCLUDED.id_categoria, "
                    + "id_proveedor = EXCLUDED.id_proveedor;";
        } else { // Modo Omitir si ya existe
            sql = "INSERT INTO productos (codigo_barras, nombre, descripcion, precio_compra, precio, stock, id_categoria, id_proveedor) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (codigo_barras) DO NOTHING;";
        }

        int totalInsertados = 0;

        try (Connection conn = ConexionDB.getConexion()) {
            if (conn == null) {
                return 0;
            }

            conn.setAutoCommit(false); // Transacción manual para optimizar el lote

            // 2. Obtener o crear los IDs correspondientes para categoría y proveedor
            int proveedorIdDefecto = obtenerOCrearProveedorId(conn, "PROVEEDOR GENERAL");

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (ArticulosPrecargados art : articulos) {
                    if (!art.isValido()) {
                        continue;
                    }

                    // Mapear dinámicamente la categoría del artículo o asignar la de por defecto
                    String nombreCat = (art.getCategoria() != null && !art.getCategoria().isEmpty())
                            ? art.getCategoria() : "General";
                    int categoriaId = obtenerOCrearCategoriaId(conn, nombreCat);

                    pstmt.setString(1, art.getCodigoBarras());
                    pstmt.setString(2, art.getNombre());
                    pstmt.setString(3, art.getDescripcion());
                    pstmt.setDouble(4, art.getPrecioCompra());
                    pstmt.setDouble(5, art.getPrecioVenta());
                    pstmt.setDouble(6, (double) art.getStockInicial());
                    pstmt.setInt(7, categoriaId);
                    pstmt.setInt(8, proveedorIdDefecto);

                    pstmt.addBatch();
                }

                int[] resultados = pstmt.executeBatch();
                conn.commit(); // Confirmar la transacción

                for (int res : resultados) {
                    if (res >= 0 || res == PreparedStatement.SUCCESS_NO_INFO) {
                        totalInsertados++;
                    }
                }
            } catch (SQLException e) {
                conn.rollback(); // Revertir cambios en caso de error
                System.out.println("Error al ejecutar el guardado en lote: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión durante el guardado: " + e.getMessage());
        }

        return totalInsertados;
    }

    @Override
    public boolean generarPlantillaEjemplo(File archivoDestino) {
        String cabecera = "CodigoBarras,Nombre,Descripcion,PrecioCompra,PrecioVenta,StockInicial,Categoria\n";
        String filaEjemplo1 = "750100011111,Sabritas Sal 45g,Papas fritas con sal,15.00,18.50,24,Botanas\n";
        String filaEjemplo2 = "750100022222,Coca Cola 600ml,Refresco no retornable,14.50,18.00,30,Bebidas\n";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoDestino))) {
            bw.write(cabecera);
            bw.write(filaEjemplo1);
            bw.write(filaEjemplo2);
            return true;
        } catch (IOException e) {
            System.out.println("Error al generar plantilla CSV: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ArticulosPrecargados> obtenerCatalogoBasePorGiro(String giro) {
        List<ArticulosPrecargados> lista = new ArrayList<>();

        if ("Abarrotes".equalsIgnoreCase(giro)) {
            lista.add(new ArticulosPrecargados("75010001", "Aceite 1L", "Aceite vegetal comestible", 32.00, 38.00, 12, "Abarrotes"));
            lista.add(new ArticulosPrecargados("75010002", "Arroz 1kg", "Arroz grano entero", 18.00, 24.00, 20, "Abarrotes"));
            lista.add(new ArticulosPrecargados("75010003", "Frijol Negro 1kg", "Frijol en bolsa", 22.00, 28.50, 15, "Abarrotes"));
        } else if ("Papelería".equalsIgnoreCase(giro)) {
            lista.add(new ArticulosPrecargados("75020001", "Cuaderno Profesional", "Cuaderno raya 100 hojas", 25.00, 35.00, 50, "Papelería"));
            lista.add(new ArticulosPrecargados("75020002", "Lápiz del No. 2", "Lápiz de grafito tradicional", 3.50, 6.00, 100, "Papelería"));
        }

        return lista;
    }

    // --- MÉTODOS AUXILIARES PARA CONSULTAR / CREAR RELACIONES ---
    private int obtenerOCrearCategoriaId(Connection conn, String nombreCategoria) throws SQLException {
        String selectSql = "SELECT id FROM categorias WHERE LOWER(nombre) = LOWER(?);";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nombreCategoria);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // Si no existe, la crea dinámicamente
        String insertSql = "INSERT INTO categorias (nombre) VALUES (?) RETURNING id;";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, nombreCategoria);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 1; // ID fallback por si acaso
    }

    private int obtenerOCrearProveedorId(Connection conn, String nombreProveedor) throws SQLException {
        String selectSql = "SELECT id FROM proveedores WHERE LOWER(nombre) = LOWER(?);";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nombreProveedor);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // Si no existe, crea el proveedor por defecto
        String insertSql = "INSERT INTO proveedores (nombre) VALUES (?) RETURNING id;";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, nombreProveedor);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 1; // ID fallback por si acaso
    }

}
