package com.inventario.repository;

import com.inventario.model.OpcionesHabilitadas;

public interface OpcionesHabilitadasRepository {
    
    OpcionesHabilitadas obtenerOpciones();
    boolean guardarOActualizar(OpcionesHabilitadas opciones);

}
