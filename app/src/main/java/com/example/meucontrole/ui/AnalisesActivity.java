package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalisesActivity extends AppCompatActivity {

    private TextView btnVoltar, txtGastoVsRenda, txtPorcentagemUso, txtMaiorGasto, txtMediaDiaria, txtResumoPagamentos;
    private ProgressBar progressOrcamento;
    private PieChart pieChartCategorias;
    private MaterialButton btnBaixarPdf;
    private AppDatabase database;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    private List<GastoEntity> listaGastosAtual = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_analises);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar layout de Análises: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializar Firebase com segurança (Resolve o crash do FirebaseApp)
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inicializar Views com proteção
        btnVoltar = findViewById(R.id.btnVoltar);
        txtGastoVsRenda = findViewById(R.id.txtGastoVsRenda);
        txtPorcentagemUso = findViewById(R.id.txtPorcentagemUso);
        txtMaiorGasto = findViewById(R.id.txtMaiorGasto);
        txtMediaDiaria = findViewById(R.id.txtMediaDiaria);
        txtResumoPagamentos = findViewById(R.id.txtResumoPagamentos);
        progressOrcamento = findViewById(R.id.progressOrcamento);
        pieChartCategorias = findViewById(R.id.pieChartCategorias);
        btnBaixarPdf = findViewById(R.id.btnBaixarPdf);

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        database = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);

        if (btnBaixarPdf != null) {
            btnBaixarPdf.setOnClickListener(v -> gerarRelatorioPdfReal());
        }

        // Observar Room com segurança
        if (database != null && database.gastoDao() != null) {
            database.gastoDao().obterTodosGastos().observe(this, listaGastos -> {
                listaGastosAtual = listaGastos != null ? listaGastos : new ArrayList<>();
                calcularEAtualizarDashboard(listaGastosAtual);
            });
        }

        configurarBarraNavegacao();
    }

    private void calcularEAtualizarDashboard(List<GastoEntity> listaGastos) {
        if (listaGastos == null || listaGastos.isEmpty()) {
            if (txtGastoVsRenda != null) txtGastoVsRenda.setText("Nenhum gasto registado ainda.");
            if (txtPorcentagemUso != null) txtPorcentagemUso.setText("0%");
            if (progressOrcamento != null) progressOrcamento.setProgress(0);
            if (txtMaiorGasto != null) txtMaiorGasto.setText("R$ 0,00");
            if (txtMediaDiaria != null) txtMediaDiaria.setText("R$ 0,00");
            if (txtResumoPagamentos != null) txtResumoPagamentos.setText("Sem dados para exibir.");
            if (pieChartCategorias != null) {
                pieChartCategorias.clear();
                pieChartCategorias.invalidate();
            }
            return;
        }

        float rendaFixa = sharedPreferences.getFloat("rendaFixa", 2500.0f);
        float rendaVariavel = sharedPreferences.getFloat("rendaVariavel", 1300.0f);
        double rendaTotal = rendaFixa + rendaVariavel;

        double totalGastos = 0.0;
        double maiorValorGasto = 0.0;
        Map<String, Double> mapCategorias = new HashMap<>();
        Map<String, Double> mapPagamentos = new HashMap<>();

        for (GastoEntity g : listaGastos) {
            double v = g.getValor();
            totalGastos += v;
            if (v > maiorValorGasto) maiorValorGasto = v;

            String cat = g.getCategoria() != null ? g.getCategoria() : "Outros";
            mapCategorias.put(cat, mapCategorias.getOrDefault(cat, 0.0) + v);

            String pag = g.getTipoPagamento() != null ? g.getTipoPagamento() : "PIX";
            mapPagamentos.put(pag, mapPagamentos.getOrDefault(pag, 0.0) + v);
        }

        int porcentagem = rendaTotal > 0 ? (int) ((totalGastos / rendaTotal) * 100) : 0;
        if (porcentagem > 100) porcentagem = 100;

        if (progressOrcamento != null) progressOrcamento.setProgress(porcentagem);
        if (txtGastoVsRenda != null) txtGastoVsRenda.setText(String.format("Gasto R$ %.2f de R$ %.2f", totalGastos, rendaTotal));
        if (txtPorcentagemUso != null) txtPorcentagemUso.setText(porcentagem + "%");

        if (txtMaiorGasto != null) txtMaiorGasto.setText(String.format("R$ %.2f", maiorValorGasto));
        double mediaDiaria = totalGastos / 30.0;
        if (txtMediaDiaria != null) txtMediaDiaria.setText(String.format("R$ %.2f / dia", mediaDiaria));

        // Gráfico de Pizza protegido contra falhas de biblioteca
        if (pieChartCategorias != null) {
            try {
                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                for (Map.Entry<String, Double> entry : mapCategorias.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
                }

                PieDataSet dataSet = new PieDataSet(pieEntries, "Categorias");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSet.setValueTextSize(12f);
                dataSet.setValueTextColor(Color.WHITE);

                PieData pieData = new PieData(dataSet);
                pieChartCategorias.setData(pieData);
                pieChartCategorias.getDescription().setEnabled(false);
                pieChartCategorias.setCenterText("Gastos");
                pieChartCategorias.setCenterTextSize(14f);
                pieChartCategorias.animateY(1000);
                pieChartCategorias.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StringBuilder sbPag = new StringBuilder();
        for (Map.Entry<String, Double> entry : mapPagamentos.entrySet()) {
            sbPag.append(String.format("• %s: R$ %.2f\n", entry.getKey(), entry.getValue()));
        }
        if (txtResumoPagamentos != null) txtResumoPagamentos.setText(sbPag.toString().trim());
    }

    private void gerarRelatorioPdfReal() {
        String nomeUsuario = sharedPreferences.getString("nomeUsuario", "Gabriela Santos");
        String emailSimulado = sharedPreferences.getString("emailSimulado", "");
        FirebaseUser currentUser = mAuth != null ? mAuth.getCurrentUser() : null;
        String emailFinal = !emailSimulado.isEmpty() ? emailSimulado : (currentUser != null && currentUser.getEmail() != null ? currentUser.getEmail() : "gabriela@email.com");

        String moeda = sharedPreferences.getString("moedaApp", "R$");
        float rendaFixa = sharedPreferences.getFloat("rendaFixa", 2500.0f);
        float rendaVariavel = sharedPreferences.getFloat("rendaVariavel", 1300.0f);
        float rendaTotal = rendaFixa + rendaVariavel;

        double totalDespesas = 0;
        StringBuilder linhasTabela = new StringBuilder();

        for (GastoEntity g : listaGastosAtual) {
            totalDespesas += g.getValor();
            String badgeClass = "badge-outros";
            String cat = g.getCategoria() != null ? g.getCategoria() : "Outros";

            if (cat.equalsIgnoreCase("Alimentação")) badgeClass = "badge-alim";
            else if (cat.equalsIgnoreCase("Transporte")) badgeClass = "badge-trans";
            else if (cat.equalsIgnoreCase("Lazer")) badgeClass = "badge-lazer";

            linhasTabela.append("<tr>")
                    .append("<td><span class=\"badge ").append(badgeClass).append("\">").append(cat).append("</span></td>")
                    .append("<td>").append(g.getDescricao() != null ? g.getDescricao() : "").append("</td>")
                    .append("<td>").append(g.getData() != null ? g.getData() : "").append("</td>")
                    .append("<td style=\"text-align: right; color: #EF4444; font-weight: bold;\">")
                    .append(String.format("%s %.2f", moeda, g.getValor()))
                    .append("</td></tr>");
        }

        double saldoDisponivel = rendaTotal - totalDespesas;
        double percentualGasto = rendaTotal > 0 ? (totalDespesas / rendaTotal) * 100 : 0;

        String htmlContent = "<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\"><title>Relatório Financeiro</title>" +
                "<style>" +
                "body { font-family: 'Helvetica', Arial, sans-serif; color: #1E293B; padding: 24px; background: #ffffff; }" +
                ".header-banner { background: linear-gradient(135deg, #1E63F5 0%, #3B82F6 100%); color: white; padding: 22px; border-radius: 12px; margin-bottom: 20px; }" +
                ".header-banner h1 { margin: 0 0 4px 0; font-size: 22pt; font-weight: bold; }" +
                ".header-banner p { margin: 0; font-size: 10.5pt; opacity: 0.9; }" +
                ".kpi-container { display: flex; gap: 12px; margin-bottom: 20px; }" +
                ".kpi-card { flex: 1; background: #F8F9FA; border: 1px solid #E2E8F0; border-radius: 8px; padding: 14px; border-left: 4px solid #1E63F5; }" +
                ".kpi-card.success { border-left-color: #10B981; background: #F0FDF4; }" +
                ".kpi-card.danger { border-left-color: #EF4444; background: #FEF2F2; }" +
                ".kpi-label { font-size: 9pt; color: #64748B; text-transform: uppercase; margin-bottom: 4px; }" +
                ".kpi-value { font-size: 15pt; font-weight: bold; color: #1E293B; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 10pt; }" +
                "th, td { padding: 10px 12px; text-align: left; border-bottom: 1px solid #E2E8F0; }" +
                "th { background-color: #F1F5F9; color: #1E293B; font-weight: bold; }" +
                ".badge { display: inline-block; padding: 3px 8px; border-radius: 4px; font-size: 8.5pt; font-weight: bold; }" +
                ".badge-alim { background: #FEF3C7; color: #92400E; }" +
                ".badge-trans { background: #DBEAFE; color: #1E40AF; }" +
                ".badge-lazer { background: #FCE7F3; color: #9D174D; }" +
                ".badge-outros { background: #F1F5F9; color: #475569; }" +
                ".ai-callout { background-color: #DCFCE7; border: 1px solid #BBF7D0; border-left: 4px solid #166534; padding: 14px; border-radius: 8px; font-size: 10pt; color: #166534; margin-top: 20px; }" +
                "</style></head><body>" +
                "<div class=\"header-banner\"><h1>MeuControle</h1><p>Relatório Analítico Mensal • Extrato Consolidado</p></div>" +
                "<div style=\"display: flex; justify-content: space-between; margin-bottom: 16px; font-size: 10.5pt;\">" +
                "<div><strong>Utilizador:</strong> " + nomeUsuario + "<br><strong>E-mail:</strong> " + emailFinal + "</div>" +
                "<div style=\"text-align: right;\"><strong>Moeda:</strong> " + moeda + "</div>" +
                "</div>" +
                "<div class=\"kpi-container\">" +
                "<div class=\"kpi-card\"><div class=\"kpi-label\">Renda Total</div><div class=\"kpi-value\" style=\"color: #1E63F5;\">" + String.format("%s %.2f", moeda, rendaTotal) + "</div></div>" +
                "<div class=\"kpi-card danger\"><div class=\"kpi-label\">Total Despesas</div><div class=\"kpi-value\" style=\"color: #EF4444;\">" + String.format("%s %.2f", moeda, totalDespesas) + "</div></div>" +
                "<div class=\"kpi-card success\"><div class=\"kpi-label\">Saldo Disponível</div><div class=\"kpi-value\" style=\"color: #10B981;\">" + String.format("%s %.2f", moeda, saldoDisponivel) + "</div></div>" +
                "</div>" +
                "<h3 style=\"margin-top: 24px; color: #1E293B; font-size: 13pt;\">Detalhamento por Categorias</h3>" +
                "<table><thead><tr><th>Categoria</th><th>Descrição</th><th>Data</th><th style=\"text-align: right;\">Valor</th></tr></thead><tbody>" +
                (linhasTabela.length() > 0 ? linhasTabela.toString() : "<tr><td colspan=\"4\" style=\"text-align:center; color:#64748B;\">Nenhum gasto registado ainda.</td></tr>") +
                "</tbody></table>" +
                "<div class=\"ai-callout\"><strong>✨ Análise Inteligente da IA:</strong> Suas despesas representam " + String.format("%.1f", percentualGasto) + "% da sua renda total. " +
                (saldoDisponivel < 0 ? "⚠️ Atenção: O seu saldo encontra-se negativo!" : "Continue a gerir com sabedoria as suas finanças!") +
                "</div>" +
                "</body></html>";

        WebView webView = new WebView(this);
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);

        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Relatorio_MeuControle");

        if (printManager != null) {
            printManager.print("Relatorio_MeuControle", printAdapter, new PrintAttributes.Builder().build());
            Toast.makeText(this, "A preparar relatório em PDF... 📄", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erro ao gerar PDF neste dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarBarraNavegacao() {
        if (findViewById(R.id.btnMenuInicio) != null) {
            findViewById(R.id.btnMenuInicio).setOnClickListener(v -> {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btnMenuGastos) != null) {
            findViewById(R.id.btnMenuGastos).setOnClickListener(v -> {
                startActivity(new Intent(this, GastosActivity.class));
                finish();
            });
        }
        if (findViewById(R.id.btnMenuAdicionar) != null) {
            findViewById(R.id.btnMenuAdicionar).setOnClickListener(v -> {
                startActivity(new Intent(this, AdicionarGastoActivity.class));
            });
        }
        if (findViewById(R.id.btnMenuPerfil) != null) {
            findViewById(R.id.btnMenuPerfil).setOnClickListener(v -> {
                startActivity(new Intent(this, PerfilActivity.class));
                finish();
            });
        }
    }
}