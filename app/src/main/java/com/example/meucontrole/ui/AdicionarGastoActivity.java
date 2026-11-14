package com.example.meucontrole.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdicionarGastoActivity extends AppCompatActivity {

    private TextView txtValorDisplay, txtDataSelecionada, txtPagamentoSelecionado;
    private EditText inputDescricao;
    private LinearLayout layoutData, layoutPagamento;

    private TextView[] categoryViews;
    private String categoriaSelecionada = "Alimentação";
    private String formaPagamento = "Débito";
    private String dataAtualStr = "";

    private StringBuilder valorEmCents = new StringBuilder("0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_gasto);

        txtValorDisplay = findViewById(R.id.txtValorDisplay);
        txtDataSelecionada = findViewById(R.id.txtDataSelecionada);
        txtPagamentoSelecionado = findViewById(R.id.txtPagamentoSelecionado);
        inputDescricao = findViewById(R.id.inputDescricao);
        layoutData = findViewById(R.id.layoutData);
        layoutPagamento = findViewById(R.id.layoutPagamento);

        dataAtualStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txtDataSelecionada.setText(dataAtualStr);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        setupCategories();
        setupKeypad();

        layoutData.setOnClickListener(v -> mostrarDatePicker());
        layoutPagamento.setOnClickListener(v -> mostrarDialogoPagamento());
        findViewById(R.id.btnSalvarGasto).setOnClickListener(v -> salvarGasto());
    }

    private void setupCategories() {
        int[] catIds = {
                R.id.catAlimentacao, R.id.catMoradia, R.id.catTransporte,
                R.id.catSaude, R.id.catLazer, R.id.catCompras,
                R.id.catEducacao, R.id.catContas, R.id.catOutros
        };
        String[] catNames = {
                "Alimentação", "Moradia", "Transporte",
                "Saúde", "Lazer", "Compras",
                "Educação", "Contas", "Outros"
        };

        categoryViews = new TextView[catIds.length];
        for (int i = 0; i < catIds.length; i++) {
            final int index = i;
            categoryViews[i] = findViewById(catIds[i]);

            if (i == 0) categoryViews[i].setSelected(true);

            categoryViews[i].setOnClickListener(v -> {
                for (TextView tv : categoryViews) tv.setSelected(false);
                categoryViews[index].setSelected(true);
                categoriaSelecionada = catNames[index];
            });
        }
    }

    private void setupKeypad() {
        int[] btnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : btnIds) {
            ButtonViewHelper(id);
        }

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (!valorEmCents.toString().contains(".")) {
                valorEmCents.append(".");
                atualizarDisplayValor();
            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (valorEmCents.length() > 1) {
                valorEmCents.deleteCharAt(valorEmCents.length() - 1);
            } else {
                valorEmCents.setLength(0);
                valorEmCents.append("0");
            }
            atualizarDisplayValor();
        });
    }

    private void ButtonViewHelper(int id) {
        findViewById(id).setOnClickListener(v -> {
            com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) v;
            String digit = btn.getText().toString();

            if (valorEmCents.toString().equals("0")) {
                valorEmCents.setLength(0);
            }
            int dotIndex = valorEmCents.indexOf(".");
            if (dotIndex != -1 && (valorEmCents.length() - dotIndex > 2)) {
                return;
            }

            valorEmCents.append(digit);
            atualizarDisplayValor();
        });
    }

    private void atualizarDisplayValor() {
        try {
            double valor = Double.parseDouble(valorEmCents.toString().replace(",", "."));
            DecimalFormat df = new DecimalFormat("#,##0.00");
            txtValorDisplay.setText("R$ " + df.format(valor));
        } catch (Exception e) {
            txtValorDisplay.setText("R$ 0,00");
        }
    }

    private void mostrarDatePicker() {
        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            dataAtualStr = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);
            txtDataSelecionada.setText(dataAtualStr);
        }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void mostrarDialogoPagamento() {
        String[] opcoes = {"Débito", "Crédito", "Cartão Virtual", "Smartwatch / Aproximação", "PIX", "Dinheiro"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forma de Pagamento");
        builder.setItems(opcoes, (dialog, which) -> {
            formaPagamento = opcoes[which];
            txtPagamentoSelecionado.setText(formaPagamento + " ▼");
        });
        builder.show();
    }

    private void salvarGasto() {
        String descricao = inputDescricao.getText() != null ? inputDescricao.getText().toString().trim() : "";

        double valor = 0.0;
        try {
            valor = Double.parseDouble(valorEmCents.toString().replace(",", "."));
        } catch (NumberFormatException e) {
            valor = 0.0;
        }

        if (valor <= 0) {
            Toast.makeText(this, "Insira um valor válido para o gasto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descricao.isEmpty()) {
            descricao = categoriaSelecionada;
        }

        String dataGasto = txtDataSelecionada.getText().toString();
        String horarioGasto = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        final double valorFinal = valor;
        final String descFinal = descricao;

        new Thread(() -> {
            GastoEntity novoGasto = new GastoEntity(descFinal, valorFinal, categoriaSelecionada, formaPagamento, dataGasto, horarioGasto);
            AppDatabase.getInstance(getApplicationContext()).gastoDao().inserir(novoGasto);

            runOnUiThread(() -> {
                Toast.makeText(this, "Gasto adicionado com sucesso! ✅", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}