package com.zebra.basicintent1.notaVenta;

import com.google.gson.annotations.SerializedName;

public class PickingListResponse {
    @SerializedName("id_doc")
    private Long idDoc;

    @SerializedName("nro_doc")
    private String nroDoc;

    // getetrs y setters
    public Long getIdDoc() {
        return idDoc;
    }
    public void setIdDoc(Long idDoc) {
        this.idDoc = idDoc;
    }

    public String getNroDoc() {
        return nroDoc;
    }
    public void setNroDoc(String nroDoc) {
        this.nroDoc = nroDoc;
    }
}