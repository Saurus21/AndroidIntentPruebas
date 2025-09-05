package com.zebra.basicintent1.notaVenta;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ProductoEscaneado implements Serializable {
    private String codigo;

    @PrimaryKey(autoGenerate = true)
    public int id;

    public ProductoEscaneado(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }
}