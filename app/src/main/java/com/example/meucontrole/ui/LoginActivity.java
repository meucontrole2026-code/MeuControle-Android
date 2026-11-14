package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.example.meucontrole.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputSenha;
    private CheckBox checkLembrar;
    private Button btnLogin;
    private MaterialButton btnBiometria;
    private ProgressBar progressBar;
    private TextView txtCriarConta, txtEsqueciSenha, txtTermosLogin, txtPrivacidadeLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialização dos componentes do layout XML
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        checkLembrar = findViewById(R.id.checkLembrar);
        btnLogin = findViewById(R.id.btnLogin);
        btnBiometria = findViewById(R.id.btnBiometria);
        progressBar = findViewById(R.id.progressBar);
        txtCriarConta = findViewById(R.id.txtCriarConta);
        txtEsqueciSenha = findViewById(R.id.txtEsqueciSenha);
        txtTermosLogin = findViewById(R.id.txtTermosLogin);
        txtPrivacidadeLogin = findViewById(R.id.txtPrivacidadeLogin);

        sharedPreferences = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);

        // Recuperar e-mail salvo se "Lembrar-me" estava ativo
        String emailSalvo = sharedPreferences.getString("emailSalvo", "");
        if (!TextUtils.isEmpty(emailSalvo)) {
            inputEmail.setText(emailSalvo);
            checkLembrar.setChecked(true);
        }

        // Ação do Botão Entrar
        btnLogin.setOnClickListener(v -> validarEfetuarLogin());

        // --- FUNCIONALIDADE 1: AUTENTICAÇÃO BIOMÉTRICA ---
        btnBiometria.setOnClickListener(v -> iniciarAutenticacaoBiometrica());

        // Navegação para Tela de Cadastro
        txtCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // --- FUNCIONALIDADE 2: ESQUECI MINHA SENHA (Abre a tela dedicada) ---
        txtEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, EsqueciSenhaActivity.class);
            startActivity(intent);
        });

        // Termos de Uso
        txtTermosLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, TermosPrivacidadeActivity.class);
            intent.putExtra("tipo", "termos");
            startActivity(intent);
        });

        // Política de Privacidade
        txtPrivacidadeLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, TermosPrivacidadeActivity.class);
            intent.putExtra("tipo", "privacidade");
            startActivity(intent);
        });
    }

    private void validarEfetuarLogin() {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
        String senha = inputSenha.getText() != null ? inputSenha.getText().toString().trim() : "";

        if (email.isEmpty()) {
            inputEmail.setError("Por favor, informe o e-mail.");
            inputEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("E-mail num formato inválido.");
            inputEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            inputSenha.setError("Por favor, informe a senha.");
            inputSenha.requestFocus();
            return;
        }

        String emailCadastrado = sharedPreferences.getString("emailCadastrado", "ana.silva@email.com");
        String senhaCadastrada = sharedPreferences.getString("senhaCadastrada", "Admin@123");

        if (email.equals(emailCadastrado) && senha.equals(senhaCadastrada)) {

            if (checkLembrar.isChecked()) {
                sharedPreferences.edit().putString("emailSalvo", email).apply();
            } else {
                sharedPreferences.edit().remove("emailSalvo").apply();
            }

            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                btnLogin.setText("Entrar");

                Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }, 1200);

        } else {
            Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_LONG).show();
        }
    }

    // Método responsável pela Biometria Real
    private void iniciarAutenticacaoBiometrica() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Autenticação biométrica bem-sucedida!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Erro na autenticação: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Biometria não reconhecida. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acesso Biométrico - MeuControle")
                .setSubtitle("Confirme a sua impressão digital para entrar")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}