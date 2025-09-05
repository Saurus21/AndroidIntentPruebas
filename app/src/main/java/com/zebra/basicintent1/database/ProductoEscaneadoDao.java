package com.zebra.basicintent1.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductoEscaneadoDao {
    @Transaction
    default long insertIfNotExists(ProductoEscaneadoPendiente producto) {
        if (existeProducto(producto.codigo, producto.notaVentaId) == 0) {
            return insert(producto);
        }
        return -1;
    }

    @Insert
    long insert(ProductoEscaneadoPendiente producto);

    @Query("SELECT * FROM productos_escaneados WHERE nota_venta_id = :notaVentaId AND procesado = 0")
    List<ProductoEscaneadoPendiente> getPendientesPorNota(String notaVentaId);

    @Update
    void update(ProductoEscaneadoPendiente producto);

    @Query("DELETE FROM productos_escaneados WHERE nota_venta_id = :notaVentaId")
    void borrarPorNota(String notaVentaId);

    @Query("SELECT COUNT(*) FROM productos_escaneados WHERE nota_venta_id = :notaVentaId AND procesado = 0")
    int contarPendientesPorNota(String notaVentaId);

    @Query("SELECT COUNT(*) FROM productos_escaneados WHERE codigo = :codigo AND nota_venta_id = :notaVentaId")
    int existeProducto(String codigo, String notaVentaId);

    @Delete
    int delete(ProductoEscaneadoPendiente producto);
}