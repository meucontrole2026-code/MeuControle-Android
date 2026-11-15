package com.example.meucontrole.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdicionarGastoActivity extends AppCompatActivity {

    private TextView btnVoltar, txtValorDisplay, txtDataSelecionada, txtPagamentoSelecionado, txtTituloHeader;
    private EditText inputDescricao;
    private RadioGroup rgTipoTransacao;
    private MaterialButton btnSalvarGasto;
    private View layoutData, layoutPagamento;

    private String valorAtual = "0.00";
    private String categoriaSelecionada = "Outros";
    private String tipoPagamentoSelecionado = "Débito";
    private String dataSelecionadaStr;
    private TextView categoriaAnteriorSelecionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_gasto);

        // Inicializar componentes
        btnVoltar = findViewById(R.id.btnVoltar);
        txtValorDisplay = findViewById(R.id.txtValorDisplay);
        inputDescricao = findViewById(R.id.inputDescricao);
        rgTipoTransacao = findViewById(R.id.rgTipoTransacao);
        txtDataSelecionada = findViewById(R.id.txtDataSelecionada);
        txtPagamentoSelecionado = findViewById(R.id.txtPagamentoSelecionado);
        btnSalvarGasto = findViewById(R.id.btnSalvarGasto);
        layoutData = findViewById(R.id.layoutData);
        layoutPagamento = findViewById(R.id.layoutPagamento);

        // Ajustar o título do cabeçalho para "Adicionar gasto"
        txtTituloHeader = findViewById(R.id.txtTituloHeader); // Se adicionares o ID no XML, ou definimos dinamicamente se houver ID

        // Data atual por defeito
        dataSelecionadaStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        if (txtDataSelecionada != null) {
            txtDataSelecionada.setText(dataSelecionadaStr);
        }

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        // Tornar Data e Pagamento clicáveis
        if (layoutData != null) {
            layoutData.setOnClickListener(v -> mostrarSeletorData());
        }

        if (layoutPagamento != null) {
            layoutPagamento.setOnClickListener(v -> mostrarSeletorPagamento());
        }

        // Configurar botões do teclado numérico
        configurarTecladoNumerico();

        // Configurar seleção de categorias
        configurarCategorias();

        if (btnSalvarGasto != null) {
            btnSalvarGasto.setOnClickListener(v -> salvarTransacao());
        }
    }

    private void mostrarSeletorData() {
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dataSelecionadaStr = sdf.format(c.getTime());
            if (txtDataSelecionada != null) {
                txtDataSelecionada.setText(dataSelecionadaStr);
            }
        }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void mostrarSeletorPagamento() {
        final String[] opcoesPagamento = {"Débito", "Crédito", "PIX", "Cartão Virtual", "Dinheiro"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("💳 Selecione o Pagamento");
        builder.setItems(opcoesPagamento, (dialog, which) -> {
            tipoPagamentoSelecionado = opcoesPagamento[which];
            if (txtPagamentoSelecionado != null) {
                txtPagamentoSelecionado.setText(tipoPagamentoSelecionado + " ▼");
            }
        });
        builder.show();
    }

    private void configurarTecladoNumerico() {
        int[] idsBotoes = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener listener = v -> {
            MaterialButton b = (MaterialButton) v;
            String textoBotao = b.getText().toString();

            if (valorAtual.equals("0.00")) {
                if (textoBotao.equals(".")) {
                    valorAtual = "0.";
                } else {
                    valorAtual = textoBotao;
                }
            } else {
                if (textoBotao.equals(".") && valorAtual.contains(".")) return;
                valorAtual += textoBotao;
            }
            atualizarDisplayValor();
        };

        for (int id : idsBotoes) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(listener);
        }

        View btnDelete = findViewById(R.id.btnDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (valorAtual.length() > 1) {
                    valorAtual = valorAtual.substring(0, valorAtual.length() - 1);
                } else {
                    valorAtual = "0.00";
                }
                atualizarDisplayValor();
            });
        }
    }

    private void atualizarDisplayValor() {
        if (txtValorDisplay != null) {
            txtValorDisplay.setText("R$ " + valorAtual);
        }
    }

    private void configurarCategorias() {
        int[] idsCats = {
                R.id.catSalario, R.id.catAlimentacao, R.id.catMoradia,
                R.id.catTransporte, R.id.catSaude, R.id.catLazer,
                R.id.catCompras, R.id.catEducacao, R.id.catContas, R.id.catOutros
        };

        for (int id : idsCats) {
            TextView catView = findViewById(id);
            if (catView != null) {
                catView.setOnClickListener(v -> {
                    if (categoriaAnteriorSelecionada != null) {
                        categoriaAnteriorSelecionada.setBackgroundResource(R.drawable.bg_chip_selector);
                        categoriaAnteriorSelecionada.setTextColor(Color.parseColor("#1E293B"));
                    }

                    catView.setBackgroundColor(Color.parseColor("#1E63F5"));
                    catView.setTextColor(Color.WHITE);
                    categoriaAnteriorSelecionada = catView;

                    categoriaSelecionada = catView.getText().toString().replaceAll("[^a-zA-ZÀ-ú/ ]", "").trim();

                    if (categoriaSelecionada.equalsIgnoreCase("Salário/Entrada") || categoriaSelecionada.equalsIgnoreCase("Salário")) {
                        if (rgTipoTransacao != null) {
                            rgTipoTransacao.check(R.id.rbReceita);
                        }
                    }

                    Toast.makeText(this, "Categoria: " + categoriaSelecionada, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void salvarTransacao() {
        String descricao = inputDescricao != null ? inputDescricao.getText().toString().trim() : "";
        if (descricao.isEmpty()) {
            descricao = categoriaSelecionada;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorAtual);
        } catch (NumberFormatException e) {
            valor = 0.0;
        }

        if (valor <= 0) {
            Toast.makeText(this, "Insira um valor válido!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = "DESPESA";
        if (rgTipoTransacao != null && rgTipoTransacao.getCheckedRadioButtonId() == R.id.rbReceita) {
            tipo = "RECEITA";
        }

        String horaAtual = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        GastoEntity novaTransacao = new GastoEntity(
                descricao,
                valor,
                categoriaSelecionada,
                tipoPagamentoSelecionado,
                dataSelecionadaStr,
                horaAtual,
                tipo
        );

        new Thread(() -> {
            AppDatabase.getInstance(getApplicationContext()).gastoDao().inserir(novaTransacao);
            runOnUiThread(() -> {
                Toast.makeText(this, "Adicionado com sucesso! 🚀", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}