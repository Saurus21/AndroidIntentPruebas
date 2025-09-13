package com.zebra.basicintent1.database;

import androidx.room.*;
import java.util.List;

@Dao
public interface LecturaPendienteDao {

    // insertar una nueva lectura en la base de datos local
    @Insert
    void insert(LecturaPendiente lectura);

    @Update
    void update(LecturaPendiente lectura);

    @Delete
    int delete(LecturaPendiente lectura);

    // obtener todas las lecturas pendientes para una ruta especifica
    @Query("SELECT * FROM lecturas_pendientes WHERE id_ruta = :idRuta")
    List<LecturaPendiente> getLecturasPorRuta(String idRuta);

    //obtener todas las lecturas que aun no han sido sincronizadas
    @Query("SELECT * FROM lecturas_pendientes WHERE sincronizado = 0")
    List<LecturaPendiente> getLecturasNoSincronizadas();

    // eliminar todas las lecturas que ya fueron sincronizadas
    @Query("DELETE FROM lecturas_pendientes WHERE sincronizado = 1")
    void deleteSincronizadas();

}
