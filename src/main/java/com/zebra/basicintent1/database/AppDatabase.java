package com.zebra.basicintent1.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

@Database(entities = {ScannedData.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ScannedDataDao scannedDataDao();
    private static volatile AppDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE scanned_data ADD COLUMN timestamp TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "scanned_app_database")
                            .addMigrations(MIGRATION_1_2)
                            //.fallbackToDestructiveMigration() // agregar esta línea para migraciones destructivas
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
