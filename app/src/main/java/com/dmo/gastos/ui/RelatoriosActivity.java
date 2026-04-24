package com.dmo.gastos.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dmo.gastos.R;
import com.dmo.gastos.viewModel.GastoViewModel;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class RelatoriosActivity extends AppCompatActivity {

    private GastoViewModel gastoViewModel;
    private TextView tvTotalCasamento, tvTotalConstrucao, tvTotalGeral;
    private MaterialButton btnGerarPdf;

    private String valorCasamento = "R$ 0,00";
    private String valorConstrucao = "R$ 0,00";
    private String valorGeral = "R$ 0,00";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);

        //Encontrar os campos na tela
        tvTotalCasamento = findViewById(R.id.tvCasamento);
        tvTotalConstrucao = findViewById(R.id.tvTotalConstrucao);
        tvTotalGeral = findViewById(R.id.tvTotalGeral);
        btnGerarPdf = findViewById(R.id.btnGerarPdf);

        //Inicializar o ViewModel
        gastoViewModel = new ViewModelProvider(this).get(GastoViewModel.class);

        //Formatador de Moeda
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.getDefault());

        //Observar o Total de CASAMENTO
        gastoViewModel.getTotalPorCategoria("Casamento").observe(this, total -> {
            valorCasamento = (total != null) ? formatoMoeda.format(total) : "R$ 0,00";
            tvTotalCasamento.setText(valorCasamento);
        });

        //observar o Total de CONSTRUÇÃO
        gastoViewModel.getTotalPorCategoria("Construção").observe(this, total -> {
            valorConstrucao = (total != null) ? formatoMoeda.format(total) : "R$ 0,00";
            tvTotalConstrucao.setText(valorConstrucao);
        });

        //Observar o Total de GERAL
        gastoViewModel.getTotalPorCategoria("Geral").observe(this, total -> {
            valorGeral = (total != null) ? formatoMoeda.format(total) : "R$ 0,00";
            tvTotalGeral.setText(valorGeral);
        });
        btnGerarPdf.setOnClickListener(v -> gerarRelatorioPDF());
    }

    private void gerarRelatorioPDF() {
        PdfDocument documento = new PdfDocument();

        // Configurar o documento PDF
        PdfDocument.PageInfo detalhesDaPagina = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page pagina = documento.startPage(detalhesDaPagina);

        Canvas canvas = pagina.getCanvas();
        Paint pincel = new Paint();

        pincel.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        pincel.setTextSize(24);
        pincel.setColor(Color.BLACK);
        canvas.drawText("Relatório de Gastos do Projeto", 100, 100, pincel); // X=100, Y=100

        // Subtítulo
        pincel.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        pincel.setTextSize(16);
        pincel.setColor(Color.DKGRAY);
        canvas.drawText("Resumo Financeiro por Categoria", 100, 130, pincel);

        // Desenhar Linha Separadora
        canvas.drawLine(100, 150, 495, 150, pincel);

        // Escrever os Dados
        pincel.setColor(Color.BLACK);
        pincel.setTextSize(18);

        canvas.drawText("Total Casamento:", 100, 200, pincel);
        canvas.drawText(valorCasamento, 350, 200, pincel);

        canvas.drawText("Total Construção:", 100, 250, pincel);
        canvas.drawText(valorConstrucao, 350, 250, pincel);

        pincel.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Total Geral:", 100, 320, pincel);
        canvas.drawText(valorGeral, 350, 320, pincel);

        // Terminar a edição da página
        documento.finishPage(pagina);

        // 4. Guardar o ficheiro no telemóvel
        try {
            // Guarda na pasta Documentos interna do aplicativo
            File pastaDocumentos = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File arquivoPdf = new File(pastaDocumentos, "Resumo_Gastos.pdf");

            documento.writeTo(new FileOutputStream(arquivoPdf));

            Toast.makeText(this, "PDF Gerado com Sucesso!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Caminho: " + arquivoPdf.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar o PDF.", Toast.LENGTH_SHORT).show();
        }

        // Fechar o documento para libertar memória
        documento.close();

    }

}
