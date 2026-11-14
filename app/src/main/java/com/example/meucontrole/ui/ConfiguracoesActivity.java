package com.example.meucontrole.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfiguracoesActivity extends AppCompatActivity {

    private TextView btnVoltar;
    private LinearLayout menuEditarPerfil, menuAlterarEmail, menuAlterarSenha, menuExcluirConta;
    private LinearLayout menuBiometria, menuBloqueio, menuPoliticaPrivacidade, menuSolicitarExclusao;
    private LinearLayout menuNotificacoesApp, menuTema, menuMoeda, menuVencimentos, menuLembretes;
    private LinearLayout menuCentralAjuda, menuTermosUso, menuPoliticaInfo, menuSobreApp, menuVersaoApp;
    private LinearLayout menuSairConta;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar botões e menus
        btnVoltar = findViewById(R.id.btnVoltar);

        // Minha Conta
        menuEditarPerfil = findViewById(R.id.menuEditarPerfil);
        menuAlterarEmail = findViewById(R.id.menuAlterarEmail);
        menuAlterarSenha = findViewById(R.id.menuAlterarSenha);
        menuExcluirConta = findViewById(R.id.menuExcluirConta);

        // Segurança e Privacidade
        menuBiometria = findViewById(R.id.menuBiometria);
        menuBloqueio = findViewById(R.id.menuBloqueio);
        menuPoliticaPrivacidade = findViewById(R.id.menuPoliticaPrivacidade);
        menuSolicitarExclusao = findViewById(R.id.menuSolicitarExclusao);

        // Preferências
        menuNotificacoesApp = findViewById(R.id.menuNotificacoesApp);
        menuTema = findViewById(R.id.menuTema);
        menuMoeda = findViewById(R.id.menuMoeda);
        menuVencimentos = findViewById(R.id.menuVencimentos);
        menuLembretes = findViewById(R.id.menuLembretes);

        // Ajuda e Informações
        menuCentralAjuda = findViewById(R.id.menuCentralAjuda);
        menuTermosUso = findViewById(R.id.menuTermosUso);
        menuPoliticaInfo = findViewById(R.id.menuPoliticaInfo);
        menuSobreApp = findViewById(R.id.menuSobreApp);
        menuVersaoApp = findViewById(R.id.menuVersaoApp);

        // Sair
        menuSairConta = findViewById(R.id.menuSairConta);

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        // --- MINHA CONTA ---
        if (menuEditarPerfil != null) {
            menuEditarPerfil.setOnClickListener(v -> Toast.makeText(this, "Editar Perfil", Toast.LENGTH_SHORT).show());
        }

        if (menuAlterarEmail != null) {
            menuAlterarEmail.setOnClickListener(v -> Toast.makeText(this, "Funcionalidade de alterar e-mail em breve", Toast.LENGTH_SHORT).show());
        }

        if (menuAlterarSenha != null) {
            menuAlterarSenha.setOnClickListener(v -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getEmail() != null) {
                    mAuth.sendPasswordResetEmail(user.getEmail())
                            .addOnSuccessListener(unused -> Toast.makeText(this, "E-mail de redefinição de senha enviado!", Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        }

        if (menuExcluirConta != null) {
            menuExcluirConta.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("⚠️ Excluir Conta")
                        .setMessage("Tem certeza que deseja excluir a sua conta permanentemente? Esta ação não pode ser desfeita.")
                        .setPositiveButton("Excluir", (dialog, which) -> {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.delete()
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_SHORT).show();
                                            finishAffinity();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Erro: Reautenticação necessária.", Toast.LENGTH_LONG).show());
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }

        // --- SEGURANÇA E PRIVACIDADE ---
        if (menuBiometria != null) menuBiometria.setOnClickListener(v -> Toast.makeText(this, "Acesso biométrico alternado", Toast.LENGTH_SHORT).show());
        if (menuBloqueio != null) menuBloqueio.setOnClickListener(v -> Toast.makeText(this, "Bloqueio automático configurado", Toast.LENGTH_SHORT).show());
        if (menuPoliticaPrivacidade != null) menuPoliticaPrivacidade.setOnClickListener(v -> Toast.makeText(this, "Abrindo Política de Privacidade...", Toast.LENGTH_SHORT).show());
        if (menuSolicitarExclusao != null) menuSolicitarExclusao.setOnClickListener(v -> Toast.makeText(this, "Solicitação de dados enviada", Toast.LENGTH_SHORT).show());

        // --- PREFERÊNCIAS DO APLICATIVO ---
        if (menuNotificacoesApp != null) menuNotificacoesApp.setOnClickListener(v -> Toast.makeText(this, "Notificações alteradas", Toast.LENGTH_SHORT).show());
        if (menuTema != null) menuTema.setOnClickListener(v -> Toast.makeText(this, "Tema do aplicativo alterado", Toast.LENGTH_SHORT).show());
        if (menuMoeda != null) menuMoeda.setOnClickListener(v -> Toast.makeText(this, "Moeda definida como Real (R$)", Toast.LENGTH_SHORT).show());
        if (menuVencimentos != null) menuVencimentos.setOnClickListener(v -> Toast.makeText(this, "Avisos de vencimento configurados", Toast.LENGTH_SHORT).show());
        if (menuLembretes != null) {
            menuLembretes.setOnClickListener(v -> Toast.makeText(this, "Lembretes financeiros", Toast.LENGTH_SHORT).show());
        }

        // --- AJUDA E INFORMAÇÕES ---
        if (menuCentralAjuda != null) menuCentralAjuda.setOnClickListener(v -> Toast.makeText(this, "Abrindo Central de Ajuda...", Toast.LENGTH_SHORT).show());
        if (menuTermosUso != null) menuTermosUso.setOnClickListener(v -> Toast.makeText(this, "Abrindo Termos de Uso...", Toast.LENGTH_SHORT).show());
        if (menuPoliticaInfo != null) menuPoliticaInfo.setOnClickListener(v -> Toast.makeText(this, "Política de privacidade", Toast.LENGTH_SHORT).show());
        if (menuSobreApp != null) menuSobreApp.setOnClickListener(v -> Toast.makeText(this, "MeuControle - Gestão Financeira Inteligente", Toast.LENGTH_LONG).show());
        if (menuVersaoApp != null) menuVersaoApp.setOnClickListener(v -> Toast.makeText(this, "Versão 1.0 (Build 2026)", Toast.LENGTH_SHORT).show());

        // --- SAIR DA CONTA (Com Confirmação) ---
        if (menuSairConta != null) {
            menuSairConta.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Encerrar Sessão")
                        .setMessage("Deseja realmente sair da sua conta?")
                        .setPositiveButton("Sair", (dialog, which) -> {
                            mAuth.signOut();
                            Toast.makeText(this, "Sessão encerrada com sucesso.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }
    }
}