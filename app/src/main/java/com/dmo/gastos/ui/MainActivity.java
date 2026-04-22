package com.dmo.gastos.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dmo.gastos.R;
import com.dmo.gastos.model.Gasto;
import com.dmo.gastos.viewModel.GastoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private GastoViewModel gastoViewModel;
    private TextInputEditText etDescricao, etValor;
    private AutoCompleteTextView autoCompleteCategoria;
    private MaterialButton btnGuardar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria);
        btnGuardar = findViewById(R.id.btnGuardar);

        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        // 4. Configurar o menu suspenso de Categorias
        configurarMenuCategorias();

        // 5. Configurar a ação do botão "Guardar"
        btnGuardar.setOnClickListener(v -> guardarGasto());
    }

    private void configurarMenuCategorias() {
        // Criamos uma lista de opções
        String[] categorias = new String[]{"Casamento", "Construção", "Geral"};

        // Criamos um adaptador para ligar a lista ao visual do Android
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );

        // Ligamos o adaptador ao nosso campo na tela
        autoCompleteCategoria.setAdapter(adapter);
    }

    private void guardarGasto() {
        // Pegar no texto que o utilizador digitou
        String descricao = etDescricao.getText() != null ? etDescricao.getText().toString().trim() : "";
        String valorString = etValor.getText() != null ? etValor.getText().toString().trim() : "";
        String categoria = autoCompleteCategoria.getText().toString().trim();

        // Validação: Não permitir guardar se faltar informação
        if (descricao.isEmpty() || valorString.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return; // Interrompe a função aqui
        }

        try {
            // Converter o texto do valor para um número decimal (Double)
            double valor = Double.parseDouble(valorString);

            // Pegar na data e hora atuais do telemóvel (em milissegundos)
            long dataAtual = new Date().getTime();

            // Ainda não temos o campo "Forma de Pagamento" no ecrã, vamos pôr um padrão por agora
            String formaPagamentoPadrao = "A definir";

            // Criar o objeto Gasto!
            Gasto novoGasto = new Gasto(descricao, valor, formaPagamentoPadrao, dataAtual, categoria);

            // Pedir ao ViewModel para guardar o gasto
            gastoViewModel.inserir(novoGasto);

            // Dar um feedback ao utilizador e limpar os campos
            Toast.makeText(this, "Gasto guardado com sucesso!", Toast.LENGTH_SHORT).show();
            limparCampos();

        } catch (NumberFormatException e) {
            // Se o utilizador digitar letras no valor (apesar do teclado numérico), tratamos o erro
            Toast.makeText(this, "Valor inválido.", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCampos() {
        etDescricao.setText("");
        etValor.setText("");
        autoCompleteCategoria.setText("");
        etDescricao.requestFocus(); // Voltar a colocar o cursor (piscar) no campo de descrição
    }
}

