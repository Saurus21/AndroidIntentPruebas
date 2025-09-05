package com.zebra.basicintent1.notaVenta;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class NotaVenta implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("fchDoc")
    private String fecha;

    @SerializedName("nroDoc")
    private int numero;

    @SerializedName("estado")
    private String estado;

    @SerializedName("usuarioNombre")
    private String usuarioNombre;

    @SerializedName("rutEmpresa")
    private BigInteger rutEmpresa;

    @SerializedName("clienteNombre")
    private String clienteNombre;

    @SerializedName("detalles")
    private List<Detalle> detalles;

    // constructor vacio (necesario para Retrofit)
    public NotaVenta() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) { this.numero = numero; }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public BigInteger getRutEmpresa() {
        return rutEmpresa;
    }
    public void setRutEmpresa(BigInteger rutEmpresa) { this.rutEmpresa = rutEmpresa; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public List<Detalle> getDetalles() { return detalles; }
    public void setDetalles(List<Detalle> detalles) { this.detalles = detalles; }


    // clase interna de detalle
    public static class Detalle implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("qtyItem")
        private double cantidad;

        @SerializedName("vlrUnd")
        private double precioUnitario;

        @SerializedName("vlrRef")
        private double precioReferencia;

        @SerializedName("mntNeto")
        private double montoNeto;

        @SerializedName("unidadMedida")
        private String unidadMedida;

        @SerializedName("p1")
        private String p1;

        @SerializedName("p2")
        private String p2;

        @SerializedName("productos")
        private List<Producto> productos;

        public Detalle() {
            // constructor vacio (necesario para Retrofit)
        }

        // getter setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getCantidad() {
            return cantidad;
        }

        public void setCantidad(double cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public double getPrecioReferencia() {
            return precioReferencia;
        }

        public void setPrecioReferencia(double precioReferencia) {
            this.precioReferencia = precioReferencia;
        }

        public double getMontoNeto() {
            return montoNeto;
        }

        public void setMontoNeto(double montoNeto) {
            this.montoNeto = montoNeto;
        }

        public String getUnidadMedida() {
            return unidadMedida;
        }
        public void setUnidadMedida(String unidadMedida) {
            this.unidadMedida = unidadMedida;
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

        public List<Producto> getProductos() {
            return productos;
        }
    }

    // clase interna de producto
    public static class Producto implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("codigo")
        private String codigo;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("tipo")
        private int tipo;

        @SerializedName("vigente")
        private String vigente;

        @SerializedName("unidadMedidaBasica")
        private String unidadMedidaBasica;

        public Producto() {
            // constructor vacio
        }

        // getters setters

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }
        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public int getTipo() { return tipo; }
        public void setTipo(int tipo) { this.tipo = tipo; }

        public String getVigente() { return vigente; }
        public void setVigente(String vigente) { this.vigente = vigente; }

        public String getUnidadMedidaBasica() {
            return unidadMedidaBasica;
        }
        public void setUnidadMedidaBasica(String unidadMedidaBasica) {
            this.unidadMedidaBasica = unidadMedidaBasica;
        }
    }
}