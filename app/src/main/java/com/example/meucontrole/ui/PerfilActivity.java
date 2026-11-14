package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.example.meucontrole.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.Executor;

public class PerfilActivity extends AppCompatActivity {

    private TextView btnVoltar, txtNomePerfil, txtEmailPerfil;
    private ImageView imgFotoPerfil;
    private FrameLayout containerFoto;

    private LinearLayout menuAlterarEmail, menuAlterarSenha, menuExcluirConta;
    private LinearLayout menuBiometria, menuBloqueio, menuPoliticaPriv, menuSolicitarDados;
    private LinearLayout menuNotificacoes, menuTema, menuMoeda, menuVencimentos, menuLembretes;
    private LinearLayout menuCentralAjuda, menuTermosUso, menuPoliticaInfo, menuSobreApp, menuVersaoApp, menuSairConta;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<String> selecionarImagemLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}

                    try {
                        imgFotoPerfil.setImageURI(uri);
                        sharedPreferences.edit().putString("fotoPerfilUri", uri.toString()).apply();
                        Toast.makeText(this, "Foto de perfil atualizada! 📸", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);
        aplicarTemaSalvo();

        setContentView(R.layout.activity_perfil);

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            btnVoltar = findViewById(R.id.btnVoltar);
            containerFoto = findViewById(R.id.containerFoto);
            imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
            txtNomePerfil = findViewById(R.id.txtNomePerfil);
            txtEmailPerfil = findViewById(R.id.txtEmailPerfil);

            menuAlterarEmail = findViewById(R.id.menuAlterarEmail);
            menuAlterarSenha = findViewById(R.id.menuAlterarSenha);
            menuExcluirConta = findViewById(R.id.menuExcluirConta);

            menuBiometria = findViewById(R.id.menuBiometria);
            menuBloqueio = findViewById(R.id.menuBloqueio);
            menuPoliticaPriv = findViewById(R.id.menuPoliticaPriv);
            menuSolicitarDados = findViewById(R.id.menuSolicitarDados);

            menuNotificacoes = findViewById(R.id.menuNotificacoes);
            menuTema = findViewById(R.id.menuTema);
            menuMoeda = findViewById(R.id.menuMoeda);
            menuVencimentos = findViewById(R.id.menuVencimentos);
            menuLembretes = findViewById(R.id.menuLembretes);

            menuCentralAjuda = findViewById(R.id.menuCentralAjuda);
            menuTermosUso = findViewById(R.id.menuTermosUso);
            menuPoliticaInfo = findViewById(R.id.menuPoliticaInfo);
            menuSobreApp = findViewById(R.id.menuSobreApp);
            menuVersaoApp = findViewById(R.id.menuVersaoApp);
            menuSairConta = findViewById(R.id.menuSairConta);

            if (btnVoltar != null) {
                btnVoltar.setOnClickListener(v -> finish());
            }

            atualizarEmailTela();
            atualizarDadosTela();

            View.OnClickListener acaoAbrirGaleria = v -> {
                try {
                    selecionarImagemLauncher.launch("image/*");
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao abrir galeria", Toast.LENGTH_SHORT).show();
                }
            };

            if (containerFoto != null) containerFoto.setOnClickListener(acaoAbrirGaleria);
            if (imgFotoPerfil != null) imgFotoPerfil.setOnClickListener(acaoAbrirGaleria);

            if (menuAlterarEmail != null) menuAlterarEmail.setOnClickListener(v -> mostrarDialogoSimularAlterarEmail());
            if (menuAlterarSenha != null) menuAlterarSenha.setOnClickListener(v -> mostrarDialogoSimularAlterarSenha());
            if (menuExcluirConta != null) menuExcluirConta.setOnClickListener(v -> mostrarDialogoSimularExcluirConta());

            if (menuBiometria != null) menuBiometria.setOnClickListener(v -> configurarBiometriaReal());
            if (menuBloqueio != null) menuBloqueio.setOnClickListener(v -> mostrarDialogoBloqueioAutomatico());
            if (menuPoliticaPriv != null) menuPoliticaPriv.setOnClickListener(v -> mostrarDialogoPoliticaPrivacidade());
            if (menuSolicitarDados != null) menuSolicitarDados.setOnClickListener(v -> mostrarDialogoSolicitarExclusaoDados());

            if (menuNotificacoes != null) menuNotificacoes.setOnClickListener(v -> mostrarDialogoNotificacoes());
            if (menuTema != null) menuTema.setOnClickListener(v -> mostrarDialogoTema());
            if (menuMoeda != null) menuMoeda.setOnClickListener(v -> mostrarDialogoMoeda());
            if (menuVencimentos != null) menuVencimentos.setOnClickListener(v -> mostrarDialogoVencimentos());
            if (menuLembretes != null) menuLembretes.setOnClickListener(v -> mostrarDialogoLembretes());

            if (menuCentralAjuda != null) menuCentralAjuda.setOnClickListener(v -> mostrarCentralAjuda());
            if (menuTermosUso != null) menuTermosUso.setOnClickListener(v -> mostrarTermosUso());
            if (menuPoliticaInfo != null) menuPoliticaInfo.setOnClickListener(v -> mostrarPoliticaPrivacidadeInfo());
            if (menuSobreApp != null) menuSobreApp.setOnClickListener(v -> mostrarSobreApp());
            if (menuVersaoApp != null) menuVersaoApp.setOnClickListener(v -> mostrarVersaoApp());

            if (menuSairConta != null) {
                menuSairConta.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Encerrar Sessão")
                            .setMessage("Deseja realmente sair da sua conta?")
                            .setPositiveButton("Sair", (dialog, which) -> {
                                if (mAuth != null) {
                                    mAuth.signOut();
                                }
                                Toast.makeText(this, "Sessão encerrada com sucesso.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                });
            }

            // Inicializar Barra Inferior
            configurarBarraNavegacaoPerfil();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void configurarBarraNavegacaoPerfil() {
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
        if (findViewById(R.id.btnMenuAnalises) != null) {
            findViewById(R.id.btnMenuAnalises).setOnClickListener(v -> {
                startActivity(new Intent(this, AnalisesActivity.class));
                finish();
            });
        }
    }

    private void atualizarEmailTela() {
        if (txtEmailPerfil != null) {
            try {
                String emailSalvo = sharedPreferences.getString("emailSimulado", "");
                FirebaseUser currentUser = mAuth != null ? mAuth.getCurrentUser() : null;

                if (!emailSalvo.isEmpty()) {
                    txtEmailPerfil.setText(emailSalvo);
                } else if (currentUser != null && currentUser.getEmail() != null) {
                    txtEmailPerfil.setText(currentUser.getEmail());
                } else {
                    txtEmailPerfil.setText("gabriela@email.com");
                }
            } catch (Exception e) {
                txtEmailPerfil.setText("gabriela@email.com");
            }
        }
    }

    private void atualizarDadosTela() {
        String nomeSalvo = sharedPreferences.getString("nomeUsuario", "Gabriela Santos");
        String fotoUriStr = sharedPreferences.getString("fotoPerfilUri", "");

        if (txtNomePerfil != null) {
            txtNomePerfil.setText(nomeSalvo);
        }

        if (!fotoUriStr.isEmpty() && imgFotoPerfil != null) {
            try {
                imgFotoPerfil.setImageURI(Uri.parse(fotoUriStr));
            } catch (Exception e) {
                sharedPreferences.edit().remove("fotoPerfilUri").apply();
                imgFotoPerfil.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
    }

    private void mostrarDialogoNotificacoes() {
        boolean ativadas = sharedPreferences.getBoolean("notificacoesAtivas", true);
        CharSequence[] opcoes = {"Ativar Notificações", "Desativar Notificações"};

        new AlertDialog.Builder(this)
                .setTitle("🔔 Notificações (Atual: " + (ativadas ? "Ativadas" : "Desativadas") + ")")
                .setItems(opcoes, (dialog, which) -> {
                    boolean ativar = (which == 0);
                    sharedPreferences.edit().putBoolean("notificacoesAtivas", ativar).apply();
                    Toast.makeText(this, ativar ? "Notificações ativadas! 🔔" : "Notificações desativadas.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aplicarTemaSalvo() {
        String modoTema = sharedPreferences.getString("modoTema", "sistema");
        if (modoTema.equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (modoTema.equals("escuro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void mostrarDialogoTema() {
        String[] temas = {"Tema Claro ☀️", "Tema Escuro 🌙", "Automático (Sistema) 📱"};

        new AlertDialog.Builder(this)
                .setTitle("🎨 Escolher Tema do Aplicativo")
                .setItems(temas, (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (which == 0) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putString("modoTema", "claro");
                        Toast.makeText(this, "Tema Claro ativado! ☀️", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putString("modoTema", "escuro");
                        Toast.makeText(this, "Tema Escuro ativado! 🌙", Toast.LENGTH_SHORT).show();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        editor.putString("modoTema", "sistema");
                        Toast.makeText(this, "Tema Automático ativado! 📱", Toast.LENGTH_SHORT).show();
                    }
                    editor.apply();
                    recreate();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoMoeda() {
        String[] moedas = {"Real brasileiro - R$", "Dólar americano - $", "Euro - €"};

        new AlertDialog.Builder(this)
                .setTitle("💵 Configurar Moeda Principal")
                .setItems(moedas, (dialog, which) -> {
                    String simboloMoeda = (which == 0) ? "R$" : (which == 1) ? "$" : "€";
                    sharedPreferences.edit().putString("moedaApp", simboloMoeda).apply();
                    Toast.makeText(this, "Moeda definida para: " + moedas[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoVencimentos() {
        String[] tempos = {"No dia do vencimento", "1 dia antes", "2 dias antes", "1 semana antes"};

        new AlertDialog.Builder(this)
                .setTitle("📅 Notificações de Vencimentos")
                .setItems(tempos, (dialog, which) -> {
                    String tempoEscolhido = tempos[which];
                    sharedPreferences.edit().putString("avisoVencimento", tempoEscolhido).apply();
                    Toast.makeText(this, "Avisos ajustados para: " + tempoEscolhido, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoLembretes() {
        String[] frequencias = {"Diariamente (às 20:00)", "Apenas nos dias úteis", "Semanalmente (Segundas)", "Personalizado"};

        new AlertDialog.Builder(this)
                .setTitle("⏰ Lembretes Financeiros")
                .setItems(frequencias, (dialog, which) -> {
                    String freqEscolhida = frequencias[which];
                    sharedPreferences.edit().putString("lembreteFinanceiro", freqEscolhida).apply();
                    Toast.makeText(this, "Lembrete configurado: " + freqEscolhida, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void configurarBiometriaReal() {
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(PerfilActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    boolean estadoAtual = sharedPreferences.getBoolean("biometriaAtiva", false);
                    boolean novoEstado = !estadoAtual;
                    sharedPreferences.edit().putBoolean("biometriaAtiva", novoEstado).apply();

                    String msg = novoEstado ? "Acesso por biometria ativado com sucesso! 🛡️" : "Acesso por biometria desativado.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(), "Erro de autenticação: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Autenticação biométrica falhou.", Toast.LENGTH_SHORT).show();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Confirmação de Identidade")
                    .setSubtitle("Autentique-se para alterar a configuração de biometria")
                    .setNegativeButtonText("Cancelar")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "O seu dispositivo não suporta ou não tem biometria configurada.", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDialogoBloqueioAutomatico() {
        String[] tempos = {"Imediatamente", "Após 1 minuto", "Após 5 minutos", "Nunca (Desativado)"};

        new AlertDialog.Builder(this)
                .setTitle("⏱️ Configurar Bloqueio Automático")
                .setItems(tempos, (dialog, which) -> {
                    String selecionado = tempos[which];
                    sharedPreferences.edit().putString("bloqueioAutomatico", selecionado).apply();
                    Toast.makeText(this, "Bloqueio definido para: " + selecionado, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoPoliticaPrivacidade() {
        new AlertDialog.Builder(this)
                .setTitle("📄 Política de Privacidade")
                .setMessage("O MeuControle preza pela segurança e privacidade dos seus dados financeiros.")
                .setPositiveButton("Compreendido", null)
                .show();
    }

    private void mostrarDialogoSolicitarExclusaoDados() {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Solicitar Exclusão de Dados")
                .setMessage("Deseja enviar um pedido formal para apagar permanentemente todos os seus registos?")
                .setPositiveButton("Sim, Solicitar", (dialog, which) -> {
                    sharedPreferences.edit().putBoolean("dadosSolicitadosExclusao", true).apply();
                    Toast.makeText(this, "Pedido registado com sucesso.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarCentralAjuda() {
        new AlertDialog.Builder(this)
                .setTitle("❓ Central de Ajuda")
                .setMessage("Bem-vindo à Central de Ajuda do MeuControle!")
                .setPositiveButton("Fechar", null)
                .show();
    }

    private void mostrarTermosUso() {
        new AlertDialog.Builder(this).setTitle("📜 Termos de Uso").setPositiveButton("Compreendido", null).show();
    }

    private void mostrarPoliticaPrivacidadeInfo() {
        new AlertDialog.Builder(this).setTitle("📄 Política de Privacidade").setPositiveButton("Fechar", null).show();
    }

    private void mostrarSobreApp() {
        new AlertDialog.Builder(this).setTitle("ℹ️ Sobre o Aplicativo").setPositiveButton("OK", null).show();
    }

    private void mostrarVersaoApp() {
        new AlertDialog.Builder(this).setTitle("🔄 Versão").setPositiveButton("Fechar", null).show();
    }

    private void mostrarDialogoSimularAlterarEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✉️ Alterar E-mail");
        final EditText input = new EditText(this);
        input.setHint("Novo E-mail");
        builder.setView(input);
        builder.setPositiveButton("Atualizar", (d, w) -> {
            if (!input.getText().toString().trim().isEmpty()) {
                sharedPreferences.edit().putString("emailSimulado", input.getText().toString().trim()).apply();
                atualizarEmailTela();
                Toast.makeText(this, "E-mail atualizado!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null).show();
    }

    private void mostrarDialogoSimularAlterarSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🔒 Alterar Senha");
        Toast.makeText(this, "Funcionalidade simulada com sucesso.", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoSimularExcluirConta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚠️ Excluir Conta");
        builder.setMessage("Deseja eliminar a sua conta?");
        builder.setPositiveButton("Sim", (d, w) -> finishAffinity());
        builder.setNegativeButton("Cancelar", null).show();
    }
}