package com.zebra.basicintent1.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "productos_escaneados",
        indices = {@Index(value = {"codigo", "nota_venta_id"}, unique = true)})

public class ProductoEscaneadoPendiente {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "codigo")
    public String codigo;

    @ColumnInfo(name = "nota_venta_id")
    public String notaVentaId;

    @ColumnInfo(name = "fecha_escaneo")
    public long fechaEscaneo;

    @ColumnInfo(name = "procesado")
    public boolean procesado = false;

    @ColumnInfo(name = "intentos", defaultValue = "0")
    public int intentos = 0;

    // constructor
    public ProductoEscaneadoPendiente(String codigo, String notaVentaId) {
        this.codigo = codigo;
        this.notaVentaId = notaVentaId;
        this.fechaEscaneo = System.currentTimeMillis();
    }
}
