package com.zebra.basicintent1.notaVenta;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockProducto {

    @SerializedName("id")
    private Long id;

    @SerializedName("pallet")
    private String pallet;

    @SerializedName("cdg_bodega")
    private String cdgBodega;

    @SerializedName("cdg_item")
    private String cdgItem;

    @SerializedName("unmd_item")
    private String unmdItem;

    @SerializedName("unmd_ref")
    private String unmdRef;

    @SerializedName("nro_um")
    private String nroUm;

    @SerializedName("p_1")
    private String p1;

    @SerializedName("p_2")
    private String p2;

    @SerializedName("p_3")
    private String p3;

    @SerializedName("p_4")
    private String p4;

    @SerializedName("p_5")
    private String p5;

    @SerializedName("p_6")
    private String p6;

    @SerializedName("p_8")
    private String p8;

    @SerializedName("p_9")
    private String p9;

    @SerializedName("qty_item")
    private BigDecimal qtyItem;

    @SerializedName("qty_ref")
    private BigDecimal qtyRef;

    // constructores
    public StockProducto() {
    }

    // getters y setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPallet() {
        return pallet;
    }
    public void setPallet(String pallet) {
        this.pallet = pallet;
    }

    public String getCdgBodega() {
        return cdgBodega;
    }
    public void setCdgBodega(String cdgBodega) {
        this.cdgBodega = cdgBodega;
    }

    public String getCdgItem() {
        return cdgItem;
    }
    public void setCdgItem(String cdgItem) {
        this.cdgItem = cdgItem;
    }

    public String getUnmdItem() {
        return unmdItem;
    }
    public void setUnmdItem(String unmdItem) {
        this.unmdItem = unmdItem;
    }

    public String getUnmdRef() {
        return unmdRef;
    }
    public void setUnmdRef(String unmdRef) {
        this.unmdRef = unmdRef;
    }

    public String getNroUm() {
        return nroUm;
    }
    public void setNroUm(String nroUm) {
        this.nroUm = nroUm;
    }

    public String getP1() {
        return p1;
    }
    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }
    public void setP2(String p2) {
        this.p2 = p2;
    }

    public String getP3() {
        return p3;
    }
    public void setP3(String p3) {
        this.p3 = p3;
    }

    public String getP4() {
        return p4;
    }
    public void setP4(String p4) {
        this.p4 = p4;
    }

    public String getP5() {
        return p5;
    }
    public void setP5(String p5) {
        this.p5 = p5;
    }

    public String getP6() {
        return p6;
    }
    public void setP6(String p6) {
        this.p6 = p6;
    }

    public String getP8() {
        return p8;
    }
    public void setP8(String p8) {
        this.p8 = p8;
    }

    public String getP9() {
        return p9;
    }
    public void setP9(String p9) {
        this.p9 = p9;
    }

    public BigDecimal getQtyItem() {
        return qtyItem;
    }
    public void setQtyItem(BigDecimal qtyItem) {
        this.qtyItem = qtyItem;
    }

    public BigDecimal getQtyRef() {
        return qtyRef;
    }
    public void setQtyRef(BigDecimal qtyRef) {
        this.qtyRef = qtyRef;
    }

    // metodo toString
    @Override
    public String toString() {
        return "StockProducto{" +
                "id=" + id +
                ", pallet='" + pallet + '\'' +
                ", cdgBodega='" + cdgBodega + '\'' +
                ", cdgItem='" + cdgItem + '\'' +
                ", unmdItem='" + unmdItem + '\'' +
                ", unmdRef='" + unmdRef + '\'' +
                ", nroUm='" + nroUm + '\'' +
                ", p1='" + p1 + '\'' +
                ", p2='" + p2 + '\'' +
                ", p3='" + p3 + '\'' +
                ", p4='" + p4 + '\'' +
                ", p5='" + p5 + '\'' +
                ", p6='" + p6 + '\'' +
                ", p8='" + p8 + '\'' +
                ", p9='" + p9 + '\'' +
                ", qtyItem=" + qtyItem +
                ", qtyRef=" + qtyRef +
                '}';
    }
}
