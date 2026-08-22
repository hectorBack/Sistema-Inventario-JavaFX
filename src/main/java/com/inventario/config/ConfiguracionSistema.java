package com.inventario.config;

import com.inventario.model.OpcionesHabilitadas;
import com.inventario.repository.Impl.OpcionesHabilitadasRepositoryImpl;
import com.inventario.repository.OpcionesHabilitadasRepository;

public class ConfiguracionSistema {

    private static ConfiguracionSistema instancia;
    private OpcionesHabilitadas opciones;
    private final OpcionesHabilitadasRepository repository;

    private ConfiguracionSistema() {
        this.repository = new OpcionesHabilitadasRepositoryImpl();
        cargarOpciones();
    }

    public static synchronized ConfiguracionSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionSistema();
        }
        return instancia;
    }

    public void cargarOpciones() {
        this.opciones = repository.obtenerOpciones();
        if (this.opciones == null) {
            this.opciones = new OpcionesHabilitadas(); // Valores por defecto
        }
    }

    public OpcionesHabilitadas getOpciones() {
        return opciones;
    }

}
