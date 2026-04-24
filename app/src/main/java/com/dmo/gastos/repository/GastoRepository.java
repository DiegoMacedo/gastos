package com.dmo.gastos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dmo.gastos.dao.GastoDao;
import com.dmo.gastos.database.AppDatabase;
import com.dmo.gastos.model.Gasto;

import java.util.List;

public class GastoRepository {
    private GastoDao mGastoDao;
    private LiveData<List<Gasto>> mTodosGastos;

    public GastoRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        mGastoDao = db.gastoDao();
        mTodosGastos = mGastoDao.buscarTodos();
    }
    public LiveData<List<Gasto>> getTodosGastos(){
        return mTodosGastos;
    }
    public void inserir(Gasto gasto){
        AppDatabase.databaseWriteExecutor.execute(()->{
            mGastoDao.inserir(gasto);
        });
    }
    public void atualizar(Gasto gasto){
        AppDatabase.databaseWriteExecutor.execute(()->{
            mGastoDao.atualizar(gasto);
        });
    }
    public void deletar(Gasto gasto){
        AppDatabase.databaseWriteExecutor.execute(()->{
            mGastoDao.deletar(gasto);
        });
    }
    public LiveData<List<Gasto>> buscarPorCategoria(String categoria){
        return mGastoDao.buscaPorCategoria(categoria);
    }

    public LiveData<Double> getTotalGasto(){
        return mGastoDao.getTotalGasto();
    }
    public LiveData<Double> getTotalPorCategoria(String categoria){
        return mGastoDao.getTotalPorCategoria(categoria);
    }

}
