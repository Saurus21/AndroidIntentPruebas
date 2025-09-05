package com.zebra.basicintent1.database;

import androidx.room.Database;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;

import android.content.Context;

import com.zebra.basicintent1.notaVenta.ProductoEscaneado;

@Database(
        entities = {
                ScannedData.class,
                ProductoEscaneadoPendiente.class
        },
        version = 6, // Versión actualizada
        exportSchema = true // Mantener para historial de migraciones
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ScannedDataDao scannedDataDao();
    public abstract ProductoEscaneadoDao productoEscaneadoDao();

    private static volatile AppDatabase INSTANCE;

    // Migraciones declaradas como static final
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE scanned_data ADD COLUMN timestamp TEXT");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Añadir columna de sincronización con valor por defecto
            database.execSQL(
                    "ALTER TABLE scanned_data ADD COLUMN sync_status INTEGER NOT NULL DEFAULT 0"
            );
        }
    };

    // Nueva migración para la tabla de productos escaneados
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS productos_escaneados");
            // Crear tabla para productos escaneados pendientes
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS productos_escaneados (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "codigo TEXT NOT NULL, " +
                            "nota_venta_id TEXT NOT NULL, " +
                            "fecha_escaneo INTEGER NOT NULL, " +
                            "procesado INTEGER NOT NULL DEFAULT 0, " +
                            "intentos INTEGER NOT NULL DEFAULT 0)"
            );
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "scanned_app_database.db"
                            )
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_3,
                                    MIGRATION_3_4
                            )
                            // Descomentar solo para desarrollo/debug:
                            // .fallbackToDestructiveMigrationOnDowngrade()
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("DB_INIT", "Database created");
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    Log.d("DB_INIT", "Database opened. Version: " + db.getVersion());
                                    // Verifica que la tabla existe
                                    Cursor cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='productos_escaneados'");
                                    Log.d("DB_INIT", "Tabla existe: " + (cursor.getCount() > 0));
                                    cursor.close();
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}