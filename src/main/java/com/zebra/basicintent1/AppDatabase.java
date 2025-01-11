package com.zebra.basicintent1;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ScannedData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ScannedDataDao scannedDataDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "scanned_app_database")
                            .fallbackToDestructiveMigration() // Agrega esta línea para migraciones destructivas
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
