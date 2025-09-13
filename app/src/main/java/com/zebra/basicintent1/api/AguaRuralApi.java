package com.zebra.basicintent1.api;

import com.zebra.basicintent1.modelosDatos.Lectura;
import com.zebra.basicintent1.modelosDatos.Ruta;

import java.math.BigInteger;
import java.util.List;

import retrofit2.http.*;
import retrofit2.Call;

public interface AguaRuralApi {

    @GET("rutas/pendientes")
    Call<List<Ruta>> getRutasAsignadas(
            @Query("id_tecnico") String iddTecnico,
            @Query("fecha_desde") String fechaDesde,
            @Query("fecha_hasta") String fechaHasta
    );

    @POST("lecturas/registrar")
    Call<Void> enviarLectura(@Body Lectura nuevaLectura);

    @POST("lecturas/registrar-lote")
    Call<Void> enviarLecturasEnLote(@Body List<Lectura> lecturas);
}
