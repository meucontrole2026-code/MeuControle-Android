package com.example.meucontrole.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BancoNotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        // Filtra notificações de aplicativos de bancos comuns
        if (packageName.contains("nubank") || packageName.contains("itau") || packageName.contains("bradesco") || packageName.contains("santander") || packageName.contains("c6")) {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;

            String title = extras.getString(Notification.EXTRA_TITLE);
            String text = extras.getString(Notification.EXTRA_TEXT);

            if (text != null) {
                String textoLower = text.toLowerCase();
                // Verifica se a notificação é de uma compra aprovada ou pagamento
                if (textoLower.contains("compra") || textoLower.contains("aprovada") || textoLower.contains("gasto") || textoLower.contains("pix enviado")) {
                    processarNotificacaoBanco(title, text);
                }
            }
        }
    }

    private void processarNotificacaoBanco(String title, String text) {
        try {
            // Extrai o valor numérico do texto (ex: "R$ 35,00" -> 35.00)
            String apenasNumeros = text.replaceAll("[^0-9,]", "").replace(",", ".");
            double valorCalculado = 0.0;
            if (!apenasNumeros.isEmpty()) {
                valorCalculado = Double.parseDouble(apenasNumeros);
            }

            if (valorCalculado > 0) {
                String descricaoCalculada = (title != null && !title.isEmpty()) ? title : "Compra Automática";
                if (descricaoCalculada.toLowerCase().contains("cartão") || descricaoCalculada.toLowerCase().contains("aprovação")) {
                    descricaoCalculada = "Estabelecimento";
                }

                String categoriaCalculada = "Outros";
                String t = text.toLowerCase();
                if (t.contains("uber") || t.contains("99") || t.contains("posto") || t.contains("transporte")) {
                    categoriaCalculada = "Transporte";
                } else if (t.contains("mercado") || t.contains("supermercado") || t.contains("ifood") || t.contains("padaria")) {
                    categoriaCalculada = "Alimentação";
                } else if (t.contains("farmácia") || t.contains("drogaria")) {
                    categoriaCalculada = "Saúde";
                }

                String dataHoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String horaAgora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                String pagamento = "Cartão Virtual";

                // Declaração de variáveis finais (effectively final) para uso seguro na lambda
                final double valorFinal = valorCalculado;
                final String descricaoFinal = descricaoCalculada;
                final String categoriaFinal = categoriaCalculada;
                final String pagamentoFinal = pagamento;
                final String dataFinal = dataHoje;
                final String horaFinal = horaAgora;

                // Grava automaticamente na base de dados Room em thread secundária
                new Thread(() -> {
                    GastoEntity novoGasto = new GastoEntity(descricaoFinal, valorFinal, categoriaFinal, pagamentoFinal, dataFinal, horaFinal);
                    AppDatabase.getInstance(getApplicationContext()).gastoDao().inserir(novoGasto);
                }).start();

            }
        } catch (Exception e) {
            Log.e("BancoListener", "Erro ao processar notificação bancária", e);
        }
    }
}