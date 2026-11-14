package com.example.meucontrole.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;
import com.google.android.material.card.MaterialCardView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IaVozActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_VOICE = 100;
    private static final int REQUEST_CODE_CUPOM = 200;
    private TextView txtStatusVoz;
    private MaterialCardView cardBtnOuvir, cardCupomFiscal, cardSmartwatch, cardCarteiraVirtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia_voz);

        txtStatusVoz = findViewById(R.id.txtStatusVoz);
        cardBtnOuvir = findViewById(R.id.cardBtnOuvir);
        cardCupomFiscal = findViewById(R.id.cardCupomFiscal);
        cardSmartwatch = findViewById(R.id.cardSmartwatch);
        cardCarteiraVirtual = findViewById(R.id.cardCarteiraVirtual);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        cardBtnOuvir.setOnClickListener(v -> iniciarReconhecimentoVoz());
        cardCupomFiscal.setOnClickListener(v -> selecionarImagemCupomFiscal());
        cardSmartwatch.setOnClickListener(v -> abrirConfiguracoesNotificacao("Smartwatch"));
        cardCarteiraVirtual.setOnClickListener(v -> abrirConfiguracoesNotificacao("Carteira Virtual"));
    }

    private void iniciarReconhecimentoVoz() {
        Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Diga a transação (ex: Ganhei 200 reais de salário ou Gastei 48.50 no mercado)");

        intent.putExtra(android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L);

        try {
            startActivityForResult(intent, REQUEST_CODE_VOICE);
        } catch (Exception e) {
            Toast.makeText(this, "Reconhecimento de voz não suportado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selecionarImagemCupomFiscal() {
        Toast.makeText(this, "Selecione a foto do cupom fiscal...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
            startActivityForResult(Intent.createChooser(intent, "Selecionar Cupom Fiscal"), REQUEST_CODE_CUPOM);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir a galeria.", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirConfiguracoesNotificacao(String tipo) {
        Toast.makeText(this, "Ative o acesso para detetar transações do " + tipo, Toast.LENGTH_LONG).show();
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir as configurações.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_VOICE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> resultados = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            if (resultados != null && !resultados.isEmpty()) {
                String textoFalado = resultados.get(0);
                txtStatusVoz.setText("Compreendido: \"" + textoFalado + "\"");
                processarComandoVoz(textoFalado);
            }
        } else if (requestCode == REQUEST_CODE_CUPOM && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                processarOCRRealCupom(imageUri);
            }
        }
    }

    private void processarOCRRealCupom(Uri imageUri) {
        txtStatusVoz.setText("A ler o cupom fiscal com IA...");
        try {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            com.google.mlkit.vision.text.TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String textoLido = visionText.getText();
                        analisarTextoCupomReal(textoLido);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erro ao ler o cupom: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        txtStatusVoz.setText("Falha na leitura do cupom fiscal.");
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar a imagem.", Toast.LENGTH_SHORT).show();
        }
    }

    private void analisarTextoCupomReal(String textoOCR) {
        String estabelecimento = "Estabelecimento";
        double maiorValor = 0.0;

        String[] linhas = textoOCR.split("\n");
        if (linhas.length > 0 && !linhas[0].trim().isEmpty()) {
            estabelecimento = linhas[0].trim();
            if (estabelecimento.length() > 25) {
                estabelecimento = estabelecimento.substring(0, 25);
            }
        }

        Pattern pattern = Pattern.compile("(\\d+[,.]\\d{2})");
        Matcher matcher = pattern.matcher(textoOCR);

        while (matcher.find()) {
            try {
                String valorStr = matcher.group(1).replace(",", ".");
                double v = Double.parseDouble(valorStr);
                if (v > maiorValor && v < 10000.0) {
                    maiorValor = v;
                }
            } catch (Exception ignored) {}
        }

        if (maiorValor == 0.0) {
            maiorValor = 15.00;
        }

        String categoria = "Alimentação";
        String tLower = estabelecimento.toLowerCase();
        if (tLower.contains("farmacia") || tLower.contains("drogaria") || tLower.contains("saude")) {
            categoria = "Saúde";
        } else if (tLower.contains("posto") || tLower.contains("uber") || tLower.contains("gasolina")) {
            categoria = "Transporte";
        }

        final double valorFinal = maiorValor;
        final String estabFinal = estabelecimento;
        final String catFinal = categoria;
        final String dataHoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        final String horaAgora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            GastoEntity novoGasto = new GastoEntity(estabFinal, valorFinal, catFinal, "Cupom Fiscal", dataHoje, horaAgora, "DESPESA");
            AppDatabase.getInstance(getApplicationContext()).gastoDao().inserir(novoGasto);

            runOnUiThread(() -> {
                Toast.makeText(this, "Cupom lido com sucesso! 📸", Toast.LENGTH_SHORT).show();
                txtStatusVoz.setText("Lido: " + estabFinal + " - R$ " + String.format("%.2f", valorFinal) + " guardado!");
            });
        }).start();
    }

    private void processarComandoVoz(String texto) {
        txtStatusVoz.setText("Processando voz: \"" + texto + "\"...");

        double valorCalculado = 0.0;

        // --- EXTRATOR DE VALOR ROBUSTO POR REGEX ---
        try {
            Pattern patternValor = Pattern.compile("(\\d+([.,]\\d{1,2})?)");
            Matcher matcherValor = patternValor.matcher(texto.replace("R$", "").replace("reais", "").replace("real", ""));
            if (matcherValor.find()) {
                String valStr = matcherValor.group(1).replace(",", ".");
                valorCalculado = Double.parseDouble(valStr);
            }
        } catch (Exception e) {
            // Fallback caso falhe o regex
            try {
                String limpo = texto.replaceAll("[^0-9,.]", "").replace(",", ".");
                if (!limpo.isEmpty()) {
                    valorCalculado = Double.parseDouble(limpo);
                }
            } catch (Exception ignored) {}
        }

        // Deteção automática de RECEITA vs DESPESA por palavras-chave
        String t = texto.toLowerCase();
        String tipoDetectado = "DESPESA";
        if (t.contains("ganhei") || t.contains("recebi") || t.contains("pix de") ||
                t.contains("salário") || t.contains("salario") || t.contains("depósito") ||
                t.contains("deposito") || t.contains("venda") || t.contains("recebimento")) {
            tipoDetectado = "RECEITA";
        }

        String categoriaCalculada = "Outros";
        String descricaoCalculada = "Transação";

        if ("RECEITA".equals(tipoDetectado)) {
            categoriaCalculada = "Salário/Entrada";
            descricaoCalculada = "Entrada / Salário";

            // Tenta extrair a descrição específica se disser "ganhei X de [origem]"
            if (t.contains(" de ")) {
                try {
                    String[] partes = t.split(" de ");
                    if (partes.length > 1) {
                        String origem = partes[1].trim();
                        if (!origem.isEmpty()) {
                            descricaoCalculada = "Entrada: " + origem.substring(0, 1).toUpperCase() + origem.substring(1);
                        }
                    }
                } catch (Exception ignored) {}
            }
        } else {
            if (t.contains("uber") || t.contains("táxi") || t.contains("gasolina") || t.contains("transporte")) {
                categoriaCalculada = "Transporte";
                descricaoCalculada = t.contains("uber") ? "Uber" : "Transporte";
            } else if (t.contains("almoço") || t.contains("jantar") || t.contains("mercado") || t.contains("supermercado") || t.contains("comida")) {
                categoriaCalculada = "Alimentação";
                descricaoCalculada = t.contains("mercado") ? "Mercado" : "Alimentação";
            } else if (t.contains("farmácia") || t.contains("médico") || t.contains("saúde")) {
                categoriaCalculada = "Saúde";
                descricaoCalculada = "Farmácia";
            } else {
                String limpoTexto = texto.replaceAll("(?i)(gastei|ganhei|recebi|reais|real|no|na|com|de|por|R\\$)", "").trim();
                if (!limpoTexto.isEmpty()) {
                    limpoTexto = limpoTexto.replaceAll("[0-9.,]", "").trim();
                    descricaoCalculada = !limpoTexto.isEmpty() ? limpoTexto.substring(0, 1).toUpperCase() + limpoTexto.substring(1) : categoriaCalculada;
                } else {
                    descricaoCalculada = categoriaCalculada;
                }
            }
        }

        String pagamentoCalculado = "PIX";
        if (t.contains("virtual") || t.contains("cartão")) {
            pagamentoCalculado = "Cartão Virtual";
        } else if (t.contains("smartwatch") || t.contains("relógio")) {
            pagamentoCalculado = "Smartwatch";
        }

        final double valorFinal = valorCalculado;
        final String categoriaFinal = categoriaCalculada;
        final String descricaoFinal = descricaoCalculada;
        final String pagamentoFinal = pagamentoCalculado;
        final String tipoFinal = tipoDetectado;
        final String dataHoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        final String horaAgora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            GastoEntity novoGasto = new GastoEntity(descricaoFinal, valorFinal, categoriaFinal, pagamentoFinal, dataHoje, horaAgora, tipoFinal);
            AppDatabase.getInstance(getApplicationContext()).gastoDao().inserir(novoGasto);

            runOnUiThread(() -> {
                String msgToast = "RECEITA".equals(tipoFinal) ? "Receita registada por voz! 💰" : "Gasto registado por voz! 🚀";
                Toast.makeText(this, msgToast, Toast.LENGTH_SHORT).show();
                txtStatusVoz.setText("Pronto! \"" + descricaoFinal + "\" de R$ " + String.format("%.2f", valorFinal) + " guardado.");
            });
        }).start();
    }
}