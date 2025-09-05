package com.zebra.basicintent1.api;

import com.zebra.basicintent1.notaVenta.StockProducto;
import com.zebra.basicintent1.notaVenta.NotaVenta;
import com.zebra.basicintent1.notaVenta.PickingListRequest;
import com.zebra.basicintent1.notaVenta.PickingListResponse;

import java.math.BigInteger;
import java.util.List;

import retrofit2.http.*;
import retrofit2.Call;

public interface InventarioApi {
    @Headers("Content-Type: application/json")
    @GET("user")
    Call<Void> authenticate();

    @GET("resource/inventario")
    Call<List<Inventario>> getInventario();

    @GET("resource/inventario/existe/{codigoBarra}")
    Call<Boolean> existeCodigoBarra(@Path("codigoBarra") String codigoBarra);

    @Headers("Content-Type: application/json")
    @POST("resource/inventario")
    Call<Inventario> addInventario(@Body Inventario inventario);

    @POST("resource/inventario/batch")
    Call<Void> addInventarios(@Body List<Inventario> inventarios);

    @PUT("resource/inventario/id/{id}")
    Call<Inventario> updateInventario(@Path("id") Long id, @Body Inventario inventario);

    // nuevos endpoints
    @GET("resource/ventas/por-estado-y-fecha/dto")
    Call<List<NotaVenta>> getNotasVentaPendiente(
            @Query("estado") String estado,
            @Query("fechaDesde") String fechaDesde,
            @Query("fechaHasta") String fechaHasta
    );

    @Headers("Content-Type: application/json")
    @POST("resource/inventario/generarPicking")
    Call<PickingListResponse> generarPickingList(@Body PickingListRequest request);

    /*
    @Headers("Content-Type: application/json")
    @POST("tomaInventario")
    Call<TomaInventarioResponse> generarTomaInventario(@Body TomaInventarioRequest request);
    */

    // consultar stock de producto con detalle
    @GET("resource/inventario/stock")
    Call<List<StockProducto>> verificarProducto(
            @Query("rutEmpresa") BigInteger rutEmpresa,
            @Query("nroUm") String nroUm
    );

}
