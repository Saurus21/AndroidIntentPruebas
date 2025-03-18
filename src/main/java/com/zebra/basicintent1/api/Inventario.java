package com.zebra.basicintent1.api;

import com.google.gson.annotations.SerializedName;

public class Inventario {
    @SerializedName("invrId")
    private int invrId;

    @SerializedName("invrCodigoBarra")
    private String invrCodigoBarra;

    @SerializedName("invrFecha")
    private String invrFecha;

    @SerializedName("invrHh")
    private int invrHh;

    @SerializedName("invrMm")
    private int invrMm;

    //getters y setters
    public int getId() {
        return invrId;
    }
    public void setId(int invrId) {
        this.invrId = invrId;
    }

    public String getCodigoBarra() {
        return invrCodigoBarra;
    }
    public void setCodigoBarra(String invrCodigoBarra) {
        this.invrCodigoBarra = invrCodigoBarra;
    }

    public String getFecha() {
        return invrFecha;
    }
    public void setFecha(String invrFecha) {
        this.invrFecha = invrFecha;
    }

    public int getHh() {
        return invrHh;
    }
    public void setHh(int invrHh) {
        this.invrHh = invrHh;
    }

    public int getMm() {
        return invrMm;
    }
    public void setMm(int invrMm) {
        this.invrMm = invrMm;
    }
}
