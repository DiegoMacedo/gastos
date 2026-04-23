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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private GastoViewModel gastoViewModel;
    private TextInputEditText etDescricao, etValor, etData; // etData adicionado
    private AutoCompleteTextView autoCompleteCategoria, autoCompleteFormaPagamento;
    private MaterialButton btnGuardar;

    private RecyclerView recyclerView;
    private GastoAdapter adapter;

    // Variável que guarda a data real (em milissegundos) que vai para o banco de dados
    private long dataSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etData = findViewById(R.id.etData); // Ligação do novo campo
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

        // INICIALIZA A DATA: Por padrão, a data selecionada é a de "hoje"
        dataSelecionada = MaterialDatePicker.todayInUtcMilliseconds();
        atualizarCampoDataVisual();

        // Ouve o clique no campo da data para abrir o calendário
        etData.setOnClickListener(v -> mostrarCalendario());

        btnGuardar.setOnClickListener(v -> guardarGasto());
    }

    // Método que constrói e exibe o calendário bonito do Material Design
    private void mostrarCalendario() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data do gasto")
                .setSelection(dataSelecionada) // Abre o calendário na data já escolhida
                .build();

        // O que acontece quando o utilizador clica em "OK" no calendário
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dataSelecionada = selection; // Atualiza o milissegundo com a nova escolha
            atualizarCampoDataVisual();  // Atualiza o texto que o utilizador vê
        });

        // Exibe o calendário no ecrã
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    // Transforma o número feio (milissegundos) numa data legível (ex: 21/04/2026)
    private void atualizarCampoDataVisual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        // O MaterialDatePicker usa UTC, então precisamos informar isso ao formatador para evitar bugs de fuso horário
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

            // AQUI MUDOU: Em vez de capturar a data exata de agora, usamos a 'dataSelecionada' pelo calendário
            Gasto novoGasto = new Gasto(descricao, valor, dataSelecionada, categoria, formaPagamento);

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

        // Volta a data para a de "hoje" após guardar
        dataSelecionada = MaterialDatePicker.todayInUtcMilliseconds();
        atualizarCampoDataVisual();

        etDescricao.requestFocus();
    }
}
