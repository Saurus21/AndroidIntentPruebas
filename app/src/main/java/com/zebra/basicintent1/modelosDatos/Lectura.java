package com.zebra.basicintent1.modelosDatos;

import com.google.gson.annotations.SerializedName;

public class Lectura {

    @SerializedName("medidorId")
    private int medidorId;

    @SerializedName("valor")
    private double valor;

    @SerializedName("observacion")
    private String observacion;

    @SerializedName("timestamp")
    private String timestamp;

    public Lectura(int medidorId, double valor, String observacion, String timestamp) {
        this.medidorId = medidorId;
        this.valor = valor;
        this.observacion = observacion;
        this.timestamp = timestamp;
    }

    // getters y setters

    public int getMedidorId() {
        return medidorId;
    }
    public void setMedidorId(int medidorId) {
        this.medidorId = medidorId;
    }

    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getObservacion() {
        return observacion;
    }
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}