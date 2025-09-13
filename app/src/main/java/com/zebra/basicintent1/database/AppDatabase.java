package com.zebra.basicintent1.database;

import androidx.room.Database;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.util.Log;

import android.content.Context;

@Database(
        entities = {
                LecturaPendiente.class
        },
        version = 8, // Versión actualizada
        exportSchema = true // Mantener para historial de migraciones
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LecturaPendienteDao lecturaPendienteDao();

    private static volatile AppDatabase INSTANCE;

    // Migraciones declaradas como static final
    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // eliminar tablas anteriores
            database.execSQL("DROP TABLE IF EXISTS scanned_data");
            database.execSQL("DROP TABLE IF EXISTS productos_escaneados");

            // crear la nueva tabla
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `lecturas_pendientes` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`medidorId` INTEGER NOT NULL, " +
                            "`serialMedidor` TEXT, " +
                            "`valor` REAL NOT NULL, " +
                            "`observacion` TEXT, " +
                            "`timestamp` TEXT, " +
                            "`sincronizado` INTEGER NOT NULL DEFAULT 0)"
            );
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE lecturas_pendientes ADD COLUMN idRuta TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "recoleccion_agua.db"
                            )
                            .addMigrations(
                                    MIGRATION_6_7,
                                    MIGRATION_7_8
                            )

                            // Descomentar solo para desarrollo/debug:
                            // .fallbackToDestructiveMigrationOnDowngrade()

                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    Log.d("DB_INIT", "Database opened. Version: " + db.getVersion());
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}