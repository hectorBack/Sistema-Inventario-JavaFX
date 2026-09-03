package com.inventario.repository;

import com.inventario.model.Empresa;

public interface EmpresaRepository {

    Empresa obtenerConfiguracion();

    boolean actualizarLogoPath(String logoPath);

    boolean guardarConfiguracion(Empresa empresa);
}
