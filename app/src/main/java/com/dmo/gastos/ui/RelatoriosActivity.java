package com.dmo.gastos.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dmo.gastos.R;
import com.dmo.gastos.viewModel.GastoViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class RelatoriosActivity extends AppCompatActivity {

    private GastoViewModel gastoViewModel;
    private TextView tvTotalCasamento, tvTotalConstrucao, tvTotalGeral;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);

        //Encontrar os campos na tela
        tvTotalCasamento = findViewById(R.id.tvCasamento);
        tvTotalConstrucao = findViewById(R.id.tvTotalConstrucao);
        tvTotalGeral = findViewById(R.id.tvTotalGeral);

        //Inicializar o ViewModel
        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        //Formatador de Moeda
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.getDefault());

        //Observar o Total de CASAMENTO
        gastoViewModel.getTotalPorCategoria("Casamento").observe(this, total -> {
            if (total != null) {
                tvTotalCasamento.setText(formatoMoeda.format(total));
            } else {
                tvTotalCasamento.setText("R$ 0,00");
            }
        });

        //observar o Total de CONSTRUÇÃO
        gastoViewModel.getTotalPorCategoria("Construção").observe(this, total -> {
            if (total != null) {
                tvTotalConstrucao.setText(formatoMoeda.format(total));
            } else {
                tvTotalConstrucao.setText("R$ 0,00");
            }
        });

        //Observar o Total de GERAL
        gastoViewModel.getTotalPorCategoria("Geral").observe(this, total -> {
            if (total != null) {
                tvTotalGeral.setText(formatoMoeda.format(total));
            } else {
                tvTotalGeral.setText("R$ 0,00");
            }
        });
    }
}
