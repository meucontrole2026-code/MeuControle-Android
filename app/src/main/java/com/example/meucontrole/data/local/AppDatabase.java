package com.example.meucontrole.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Certifica-te de que a versão aqui está atualizada (ex: se estava 1, passa para 2, ou deixa o número e usa o fallback)
@Database(entities = {GastoEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract GastoDao gastoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "meu_controle_db"
                    )
                    .fallbackToDestructiveMigration() // <--- ADICIONA ESTA LINHA AQUI
                    .allowMainThreadQueries() // (se usares queries na main thread)
                    .build();
        }
        return INSTANCE;
    }
}