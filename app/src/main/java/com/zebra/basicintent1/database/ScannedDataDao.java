package com.zebra.basicintent1.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScannedDataDao {

    @Insert
    void insert(ScannedData scannedData);

    @Query("SELECT * FROM scanned_data")
    LiveData<List<ScannedData>> getAllScannedData();

    @Query("SELECT * FROM scanned_data WHERE sync_status = 0") // 0 = no sincronizado
    List<ScannedData> getUnsyncedData();

    @Query("UPDATE scanned_data SET sync_status = :status WHERE id = :id")
    void updateSyncStatus(int id, int status);

    @Query("SELECT COUNT(*) FROM scanned_data WHERE data = :barcode")
    int existsLocally(String barcode);
}
