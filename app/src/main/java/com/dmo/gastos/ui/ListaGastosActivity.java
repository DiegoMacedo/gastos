package com.dmo.gastos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmo.gastos.R;
import com.dmo.gastos.adapter.GastoAdapter;
import com.dmo.gastos.model.Gasto;
import com.dmo.gastos.viewModel.GastoViewModel;

public class ListaGastosActivity extends AppCompatActivity {

    private GastoViewModel gastoViewModel;
    private GastoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gastos);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLista);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GastoAdapter();
        recyclerView.setAdapter(adapter);

        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        gastoViewModel.getTodosGastos().observe(this, gastos -> adapter.setGastos(gastos));

        adapter.setOnItemClickListener(gasto -> {
            Intent intent = new Intent(ListaGastosActivity.this, MainActivity.class);
            intent.putExtra("ID_GASTO", gasto.getId());
            intent.putExtra("DESC_GASTO", gasto.getDescricao());
            intent.putExtra("VALOR_GASTO", gasto.getValor());
            intent.putExtra("DATA_GASTO", gasto.getData());
            intent.putExtra("CAT_GASTO", gasto.getCategoria());
            intent.putExtra("PAG_GASTO", gasto.getFormaPagamento());
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Gasto gastoDeletado = adapter.getGastoAt(position);

                gastoViewModel.deletar(gastoDeletado);
                Toast.makeText(ListaGastosActivity.this, "Gasto apagado", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }
}
