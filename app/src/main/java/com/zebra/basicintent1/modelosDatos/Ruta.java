package com.zebra.basicintent1.modelosDatos;

import java.io.Serializable;
import java.util.List;

public class Ruta implements Serializable {
    private int id;
    private String fechaAsignada;
    private String nombreRuta;
    private List<Medidor> medidores;

    // getters y setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFechaAsignada() {
        return fechaAsignada;
    }
    public void setFechaAsignada(String fechaAsignada) {
        this.fechaAsignada = fechaAsignada;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }
    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public List<Medidor> getMedidores() {
        return medidores;
    }
    public void setMedidores(List<Medidor> medidores) {
        this.medidores = medidores;
    }
}
