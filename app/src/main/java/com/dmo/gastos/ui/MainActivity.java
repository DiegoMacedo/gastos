package com.dmo.gastos.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmo.gastos.R;
import com.dmo.gastos.adapter.GastoAdapter;
import com.dmo.gastos.model.Gasto;
import com.dmo.gastos.viewModel.GastoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private GastoViewModel gastoViewModel;
    private TextInputEditText etDescricao, etValor;
    private AutoCompleteTextView autoCompleteCategoria;
    private AutoCompleteTextView autoCompleteFormaPagamento; // Nova variável adicionada
    private MaterialButton btnGuardar;
    private RecyclerView recyclerView;
    private GastoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        autoCompleteFormaPagamento = findViewById(R.id.autoCompleteFormaPagamento);
        btnGuardar = findViewById(R.id.btnGuardar);

        recyclerView = findViewById(R.id.recyclerViewGastos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GastoAdapter();
        recyclerView.setAdapter(adapter);

        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);
        gastoViewModel.getTodosGastos().observe(this, gastos -> {
            adapter.setGastos(gastos);
        });

        configurarMenuCategorias();
        configurarMenuFormaPagamento();
        btnGuardar.setOnClickListener(v -> guardarGasto());
    }

    private void configurarMenuCategorias() {
        String[] categorias = new String[]{"Casamento", "Construção", "Geral"};
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );
        autoCompleteCategoria.setAdapter(adapterMenu);
    }

    private void configurarMenuFormaPagamento() {
        String[] formas = new String[]{"PIX", "Cartão de Crédito", "Cartão de Débito", "Dinheiro", "Boleto"};
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                formas
        );
        autoCompleteFormaPagamento.setAdapter(adapterMenu);
    }

    private void guardarGasto() {
        String descricao = etDescricao.getText() != null ? etDescricao.getText().toString().trim() : "";
        String valorString = etValor.getText() != null ? etValor.getText().toString().trim() : "";
        String categoria = autoCompleteCategoria.getText().toString().trim();
        String formaPagamento = autoCompleteFormaPagamento.getText().toString().trim();

        if (descricao.isEmpty() || valorString.isEmpty() || categoria.isEmpty() || formaPagamento.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double valor = Double.parseDouble(valorString);
            long dataAtual = new Date().getTime();

            Gasto novoGasto = new Gasto(descricao, valor, formaPagamento, dataAtual, categoria);
            gastoViewModel.inserir(novoGasto);

            Toast.makeText(this, "Gasto guardado!", Toast.LENGTH_SHORT).show();
            limparCampos();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido.", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCampos() {
        etDescricao.setText("");
        etValor.setText("");
        autoCompleteCategoria.setText("");
        autoCompleteFormaPagamento.setText("");
        etDescricao.requestFocus();
    }
}
