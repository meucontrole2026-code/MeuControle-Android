package com.example.meucontrole.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.meucontrole.R;

public class TermosPrivacidadeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_privacidade);

        Toolbar toolbar = findViewById(R.id.toolbarLegal);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView txtConteudo = findViewById(R.id.txtConteudoLegal);

        // Recebe o parâmetro enviado pela SettingsActivity para saber qual texto carregar
        String tipo = getIntent().getStringExtra("tipo");

        if ("termos".equals(tipo)) {
            setTitle("Termos de Uso");
            txtConteudo.setText(
                    "TERMOS DE USO - MEUCONTROLE\n\n" +
                            "1. Aceitação dos Termos\n" +
                            "Ao utilizar o aplicativo MeuControle, o utilizador concorda em cumprir e estar vinculado aos seguintes termos e condições de uso.\n\n" +
                            "2. Finalidade Acadêmica e Pessoal\n" +
                            "O MeuControle é uma aplicação de gestão financeira pessoal desenvolvida no âmbito do curso superior de Análise e Desenvolvimento de Sistemas (ADS).\n\n" +
                            "3. Armazenamento Local e Seguro\n" +
                            "Todas as informações financeiras, dados de registo e credenciais são mantidas exclusivamente no armazenamento interno do dispositivo do utilizador, protegidas por criptografia avançada (AES-256).\n\n" +
                            "4. Responsabilidade do Utilizador\n" +
                            "O utilizador é responsável por zelar pela segurança do seu dispositivo, bem como pela confidencialidade da sua palavra-passe e autenticação biométrica."
            );
        } else {
            setTitle("Política de Privacidade");
            txtConteudo.setText(
                    "POLÍTICA DE PRIVACIDADE - MEUCONTROLE\n\n" +
                            "1. Compromisso com a Privacidade\n" +
                            "O MeuControle valoriza a segurança e a privacidade dos dados dos seus utilizadores. Esta política explica como as informações são tratadas.\n\n" +
                            "2. Ausência de Compartilha de Dados\n" +
                            "• O aplicativo não envia, compartilha ou armazena quaisquer dados pessoais ou financeiros em servidores externos ou nuvem.\n" +
                            "• Todo o fluxo de dados opera de forma offline e autónoma no banco de dados local Room.\n\n" +
                            "3. Segurança de Acesso\n" +
                            "Implementamos mecanismos de proteção rigorosos, incluindo validação estrita de senhas, bloqueio temporário contra força bruta e integração com leitores biométricos nativos do sistema operativo.\n\n" +
                            "4. Atualizações\n" +
                            "Esta política poderá ser revista durante o ciclo de evolução do projeto acadêmico."
            );
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}