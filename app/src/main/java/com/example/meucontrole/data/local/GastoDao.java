package com.example.meucontrole.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GastoDao {

    @Insert
    void inserir(GastoEntity gasto);

    @Delete
    void apagar(GastoEntity gasto);

    @Query("SELECT * FROM gastos_tabela ORDER BY id DESC")
    LiveData<List<GastoEntity>> obterTodosGastos();

    @Query("SELECT SUM(valor) FROM gastos_tabela")
    LiveData<Double> obterTotalGastos();
}