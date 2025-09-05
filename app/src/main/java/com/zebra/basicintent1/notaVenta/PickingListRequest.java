package com.zebra.basicintent1.notaVenta;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PickingListRequest {
    @SerializedName("rut_empresa")
    private String rutEmpresa;

    @SerializedName("id_sucursal")
    private String idSucursal;

    @SerializedName("id_nv")
    private Long idNotaVenta;

    @SerializedName("cajas")
    private List<String> cajas;

    // getters y setters

    private String getRutEmpresa() {
        return rutEmpresa;
    }
    private void setRutEmpresa(String rutEmpresa) {
        this.rutEmpresa = rutEmpresa;
    }

    private String getIdSucursal() {
        return idSucursal;
    }
    private void setIdSucursal(String idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Long getIdNotaVenta() {
        return idNotaVenta;
    }
    public void setIdNotaVenta(Long idNotaVenta) {
        this.idNotaVenta = idNotaVenta;
    }

    private List<String> getCajas() {
        return cajas;
    }
    private void setCajas(List<String> cajas) {
        this.cajas = cajas;
    }
}
