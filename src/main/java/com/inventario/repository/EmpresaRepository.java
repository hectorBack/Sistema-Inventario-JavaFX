package com.inventario.repository;

import com.inventario.model.Empresa;
import com.inventario.model.DTOs.EmpresaDTO;

public interface EmpresaRepository {

    Empresa obtenerConfiguracion();

    EmpresaDTO obtenerConfiguracionDTO();

    boolean actualizarLogoPath(String logoPath);

    boolean guardarConfiguracion(Empresa empresa);

    boolean guardarConfiguracionDTO(EmpresaDTO empresa);
}
