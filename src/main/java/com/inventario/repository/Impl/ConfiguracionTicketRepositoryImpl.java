package com.inventario.repository.Impl;

import com.inventario.config.ConexionDB;
import com.inventario.model.ConfiguracionTicket;
import com.inventario.model.DTOs.ConfiguracionTicketDTO;
import com.inventario.model.DTOs.DTOMapper;
import com.inventario.repository.ConfiguracionTicketRepository;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;

public class ConfiguracionTicketRepositoryImpl implements ConfiguracionTicketRepository {

    @Override
    public ConfiguracionTicketDTO obtenerConfiguracionDTO() {
        String sql = "SELECT * FROM configuracion_ticket WHERE id = 1 LIMIT 1";

        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Array arrayEncabezado = rs.getArray("lineas_encabezado");
                Array arrayPie = rs.getArray("lineas_pie");

                String[] encabezadoArr = arrayEncabezado != null ? (String[]) arrayEncabezado.getArray() : new String[0];
                String[] pieArr = arrayPie != null ? (String[]) arrayPie.getArray() : new String[0];

                ConfiguracionTicket config = new ConfiguracionTicket(
                        rs.getInt("id"),
                        FXCollections.observableArrayList(Arrays.asList(encabezadoArr)),
                        FXCollections.observableArrayList(Arrays.asList(pieArr)),
                        rs.getBoolean("incluir_precio_unitario"),
                        rs.getBoolean("imprimir_descripcion_completa")
                );

                return DTOMapper.toDTO(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuración por defecto si aún no existe en la BD
        return new ConfiguracionTicketDTO(
                1,
                List.of("MI ABARROTE S.A.", "CALLE 123", "TEL: 555-0000"),
                List.of("¡GRACIAS POR SU COMPRA!", "CONSERVE SU TICKET"),
                false,
                false
        );
    }

    @Override
    public boolean guardarOActualizarDTO(ConfiguracionTicketDTO dto) {
        ConfiguracionTicket config = DTOMapper.toModel(dto);

        String sqlSelect = "SELECT id FROM configuracion_ticket WHERE id = ?";
        String sqlInsert = "INSERT INTO configuracion_ticket (id, lineas_encabezado, lineas_pie, incluir_precio_unitario, imprimir_descripcion_completa) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE configuracion_ticket SET lineas_encabezado=?, lineas_pie=?, incluir_precio_unitario=?, imprimir_descripcion_completa=? WHERE id=?";

        try (Connection conn = ConexionDB.getConexion()) {
            boolean existe = false;

            try (PreparedStatement psSel = conn.prepareStatement(sqlSelect)) {
                psSel.setInt(1, config.getId() == 0 ? 1 : config.getId());
                try (ResultSet rs = psSel.executeQuery()) {
                    if (rs.next()) {
                        existe = true;
                    }
                }
            }

            // Convertir las listas a java.sql.Array compatible con PostgreSQL
            String[] arrEncabezado = config.getLineasEncabezado().toArray(new String[0]);
            String[] arrPie = config.getLineasPie().toArray(new String[0]);

            Array sqlArrayEncabezado = conn.createArrayOf("text", arrEncabezado);
            Array sqlArrayPie = conn.createArrayOf("text", arrPie);

            if (existe) {
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setArray(1, sqlArrayEncabezado);
                    ps.setArray(2, sqlArrayPie);
                    ps.setBoolean(3, config.isIncluirPrecioUnitario());
                    ps.setBoolean(4, config.isImprimirDescripcionCompleta());
                    ps.setInt(5, config.getId() == 0 ? 1 : config.getId());
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setInt(1, 1);
                    ps.setArray(2, sqlArrayEncabezado);
                    ps.setArray(3, sqlArrayPie);
                    ps.setBoolean(4, config.isIncluirPrecioUnitario());
                    ps.setBoolean(5, config.isImprimirDescripcionCompleta());
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
