package com.dmo.gastos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dmo.gastos.R;
import com.dmo.gastos.model.Gasto;
import com.dmo.gastos.ui.ListaGastosActivity;
import com.dmo.gastos.ui.RelatoriosActivity;
import com.dmo.gastos.viewModel.GastoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private GastoViewModel gastoViewModel;
    private TextInputEditText etDescricao, etValor, etData;
    private AutoCompleteTextView autoCompleteCategoria, autoCompleteFormaPagamento;
    private MaterialButton btnGuardar, btnVerRelatorios, btnVerLista;
    private android.widget.TextView tvTotalGasto, tvTitulo;

    private long dataSelecionada;

    // Variável para saber se estamos editando (-1 significa que é um novo gasto)
    private int gastoIdEmEdicao = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etData = findViewById(R.id.etData);
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        autoCompleteFormaPagamento = findViewById(R.id.autoCompleteFormaPagamento);
        btnGuardar = findViewById(R.id.btnGuardar);
        tvTotalGasto = findViewById(R.id.tvTotalGasto);
        tvTitulo = findViewById(R.id.tvTitulo);
        btnVerRelatorios = findViewById(R.id.btnVerRelatorios);
        btnVerLista = findViewById(R.id.btnVerLista); // Novo botão

        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        // Painel Total
        gastoViewModel.getTotalGasto().observe(this, total -> {
            if (total != null){
                java.text.NumberFormat formatoMoeda = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault());
                tvTotalGasto.setText(formatoMoeda.format(total));
            } else {
                tvTotalGasto.setText("R$ 0,00");
            }
        });

        configurarMenuCategorias();
        configurarMenuFormaPagamento();

        // 1. VERIFICAR SE VIEMOS DA TELA DE EDIÇÃO
        Intent intent = getIntent();
        if (intent.hasExtra("ID_GASTO")) {
            gastoIdEmEdicao = intent.getIntExtra("ID_GASTO", -1);

            // Preenche os campos com os dados antigos
            etDescricao.setText(intent.getStringExtra("DESC_GASTO"));
            etValor.setText(String.valueOf(intent.getDoubleExtra("VALOR_GASTO", 0.0)));
            autoCompleteCategoria.setText(intent.getStringExtra("CAT_GASTO"), false);
            autoCompleteFormaPagamento.setText(intent.getStringExtra("PAG_GASTO"), false);

            dataSelecionada = intent.getLongExtra("DATA_GASTO", MaterialDatePicker.todayInUtcMilliseconds());

            tvTitulo.setText("Editar Gasto");
            btnGuardar.setText("Atualizar Gasto");
        } else {
            // Se for um novo gasto
            dataSelecionada = MaterialDatePicker.todayInUtcMilliseconds();
        }
        atualizarCampoDataVisual();

        // Cliques
        etData.setOnClickListener(v -> mostrarCalendario());
        btnGuardar.setOnClickListener(v -> guardarGasto());

        btnVerRelatorios.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RelatoriosActivity.class));
        });

        btnVerLista.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListaGastosActivity.class));
        });
    }

    private void mostrarCalendario() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data do gasto")
                .setSelection(dataSelecionada)
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dataSelecionada = selection;
            atualizarCampoDataVisual();
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void atualizarCampoDataVisual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        etData.setText(sdf.format(dataSelecionada));
    }

    private void configurarMenuCategorias() {
        String[] categorias = new String[]{"Casamento", "Construção", "Geral"};
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categorias);
        autoCompleteCategoria.setAdapter(adapterMenu);
    }

    private void configurarMenuFormaPagamento() {
        String[] formas = new String[]{"PIX", "Cartão de Crédito", "Cartão de Débito", "Dinheiro", "Boleto"};
        ArrayAdapter<String> adapterMenu = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, formas);
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
            Gasto gasto = new Gasto(descricao, valor, dataSelecionada, categoria, formaPagamento);

            // 2. VERIFICAR SE DEVE SALVAR OU ATUALIZAR
            if (gastoIdEmEdicao == -1) {
                gastoViewModel.inserir(gasto);
                Toast.makeText(this, "Gasto guardado!", Toast.LENGTH_SHORT).show();
            } else {
                gasto.setId(gastoIdEmEdicao); // Importante! Define o ID existente
                gastoViewModel.atualizar(gasto);
                Toast.makeText(this, "Gasto atualizado!", Toast.LENGTH_SHORT).show();

                // Fecha a tela e volta pra lista
                finish();
                return;
            }
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
        dataSelecionada = MaterialDatePicker.todayInUtcMilliseconds();
        atualizarCampoDataVisual();
        etDescricao.requestFocus();
    }
}