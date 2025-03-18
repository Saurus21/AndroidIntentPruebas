package com.zebra.basicintent1.api;

import java.util.List;

import retrofit2.http.*;
import retrofit2.Call;

public interface InventarioApi {

    @GET("user")
    Call<Void> authenticate();

    @GET("resource/inventario")
    Call<List<Inventario>> getInventario();

    @GET("resource/inventario/{codigo_barra}")
    Call<List<Inventario>> getScannedDataPorCodigo(@Path("codigo_barra") String codigoBarra);

    @POST("resource/inventario")
    Call<Inventario> addInventario(@Body Inventario inventario);

    @PUT("resource/inventario/{id}")
    Call<Inventario> updateInventario(@Path("id") Long id, @Body Inventario inventario);
}
