package com.zebra.basicintent1.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lecturas_pendientes")
public class LecturaPendiente {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_ruta")
    public String idRuta;

    public int medidorId;
    public String serialMedidor;
    public double valor;
    public String observacion;
    public String timestamp;
    public boolean sincronizado = false;

    public LecturaPendiente() {}

    public LecturaPendiente(
            String idRuta,
            int medidorId,
            String serialMedidor,
            double valor,
            String observacion,
            String timestamp) {
        this.idRuta = idRuta;
        this.medidorId = medidorId;
        this.serialMedidor = serialMedidor;
        this.valor = valor;
        this.observacion = observacion;
        this.timestamp = timestamp;
    }

    // getters y setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getIdRuta() {
        return idRuta;
    }
    public void setIdRuta(String idRuta) {
        this.idRuta = idRuta;
    }

    public int getMedidorId() {
        return medidorId;
    }
    public void setMedidorId(int medidorId) {
        this.medidorId = medidorId;
    }

    public String getSerialMedidor() {
        return serialMedidor;
    }
    public void setSerialMedidor(String serialMedidor) {
        this.serialMedidor = serialMedidor;
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
