package com.dmo.gastos.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dmo.gastos.model.Gasto;

import java.util.List;

@Dao
public interface GastoDao {
    @Insert
    void inserir(Gasto gasto);

    @Update
    void atualizar(Gasto gasto);

    @Delete
    void deletar(Gasto gasto);

    @Query("SELECT * FROM gastos ORDER BY data DESC")
    LiveData<List<Gasto>> buscarTodos();

    @Query("SELECT * FROM gastos WHERE categoria = :cat ORDER BY data DESC")
    LiveData<List<Gasto>> buscaPorCategoria(String cat);

    @Query("SELECT * FROM gastos WHERE data BETWEEN :inicio AND :fim ORDER BY data ASC")
    LiveData<List<Gasto>> buscarPorPeriodo(long inicio, long fim);

    @Query("SELECT SUM(valor) FROM gastos WHERE categoria = :cat")
    LiveData<Double> getTotalPorCategoria(String cat);
}
