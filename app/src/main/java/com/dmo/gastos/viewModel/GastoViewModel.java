package com.dmo.gastos.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dmo.gastos.model.Gasto;
import com.dmo.gastos.repository.GastoRepository;

import java.util.List;

public class GastoViewModel extends AndroidViewModel {
    private final GastoRepository mRepository;
    private final LiveData<List<Gasto>> mTodosGastos;

    public GastoViewModel(@NonNull Application application) {
        super(application);
        mRepository = new GastoRepository(application);
        mTodosGastos = mRepository.getTodosGastos();
    }

    public LiveData<List<Gasto>> getTodosGastos() {
        return mTodosGastos;
    }

    public void inserir(Gasto gasto) {
        mRepository.inserir(gasto);
    }

    public void atualizar(Gasto gasto) {
        mRepository.atualizar(gasto);
    }

    public void deletar(Gasto gasto) {
        mRepository.deletar(gasto);
    }

    public LiveData<List<Gasto>> buscarPorCategoria(String categoria) {
        return mRepository.buscarPorCategoria(categoria);
    }

}
