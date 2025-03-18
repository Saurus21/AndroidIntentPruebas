package com.zebra.basicintent1.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScannedDataDao {

    @Insert
    void insert(ScannedData scannedData);

    @Query("SELECT * FROM scanned_data")
    List<ScannedData> getAllScannedData();
}
