package com.zebra.basicintent1.modelosDatos;

import java.io.Serializable;

public class Medidor implements Serializable {
    private int id;
    private String serial;
    private String direccion;
    private double ultimaLectura;

    // getters y setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getUltimaLectura() {
        return ultimaLectura;
    }
    public void setUltimaLectura(double ultimaLectura) {
        this.ultimaLectura = ultimaLectura;
    }
}
