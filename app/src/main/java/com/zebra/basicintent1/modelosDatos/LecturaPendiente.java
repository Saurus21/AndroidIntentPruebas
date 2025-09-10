package com.zebra.basicintent1.modelosDatos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lecturas_pendientes")
public class LecturaPendiente {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public int medidorId;
    public String serialMedidor;
    public double valor;
    public String observacion;
    public String timestamp;
    public boolean sincronizado = false;

    public LecturaPendiente(int medidorId, String serialMedidor, double valor, String observacion, String timestamp) {
        this.medidorId = medidorId;
        this.serialMedidor = serialMedidor;
        this.valor = valor;
        this.observacion = observacion;
        this.timestamp = timestamp;
    }
}
