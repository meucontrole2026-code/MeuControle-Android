package com.example.meucontrole.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GastoEntity.class}, version = 2, exportSchema = false) // <--- Alterado de 1 para 2
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract GastoDao gastoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "meu_controle_db"
            ).fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}