package com.inventario.repository;

import com.inventario.model.OpcionesHabilitadas;
import com.inventario.model.DTOs.OpcionesHabilitadasDTO;

public interface OpcionesHabilitadasRepository {
    
    OpcionesHabilitadas obtenerOpciones();
    OpcionesHabilitadasDTO obtenerOpcionesDTO();
    boolean guardarOActualizar(OpcionesHabilitadas opciones);
    boolean guardarOActualizarDTO(OpcionesHabilitadasDTO opciones);

}
