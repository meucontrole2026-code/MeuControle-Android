package com.example.meucontrole.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.google.android.material.textfield.TextInputEditText;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText inputNome, inputEmailCadastro, inputSenhaCadastro;
    private ProgressBar progressBarCadastro;
    private Button btnCadastrar;
    private CheckBox checkTermos; // 1. Variável do CheckBox dos Termos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inputNome = findViewById(R.id.inputNome);
        inputEmailCadastro = findViewById(R.id.inputEmailCadastro);
        inputSenhaCadastro = findViewById(R.id.inputSenhaCadastro);
        progressBarCadastro = findViewById(R.id.progressBarCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        checkTermos = findViewById(R.id.checkTermos); // 2. Inicialização do ID do XML
        TextView txtIrParaLogin = findViewById(R.id.txtIrParaLogin);

        btnCadastrar.setOnClickListener(v -> validarEfetuarCadastro());
        txtIrParaLogin.setOnClickListener(v -> finish());
    }

    private void validarEfetuarCadastro() {
        String nome = inputNome.getText() != null ? inputNome.getText().toString().trim() : "";
        String email = inputEmailCadastro.getText() != null ? inputEmailCadastro.getText().toString().trim() : "";
        String senha = inputSenhaCadastro.getText() != null ? inputSenhaCadastro.getText().toString().trim() : "";

        if (nome.isEmpty()) {
            inputNome.setError("Por favor, informe o seu nome.");
            inputNome.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            inputEmailCadastro.setError("Por favor, informe o e-mail.");
            inputEmailCadastro.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmailCadastro.setError("Formato de e-mail inválido.");
            inputEmailCadastro.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            inputSenhaCadastro.setError("Por favor, insira a palavra-passe.");
            inputSenhaCadastro.requestFocus();
            return;
        }

        if (senha.length() < 6) {
            inputSenhaCadastro.setError("A senha deve ter pelo menos 6 caracteres.");
            inputSenhaCadastro.requestFocus();
            return;
        }

        // Validação rigorosa por Regex
        String regexSenha = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$";

        if (!senha.matches(regexSenha)) {
            inputSenhaCadastro.setError("A senha precisa conter pelo menos 1 letra maiúscula, 1 número e 1 caractere especial.");
            inputSenhaCadastro.requestFocus();
            return;
        }

        // 3. Validação de Aceite Obrigatório dos Termos de Uso e Privacidade
        if (checkTermos != null && !checkTermos.isChecked()) {
            Toast.makeText(this, "Deve aceitar os Termos de Uso e a Política de Privacidade para continuar.", Toast.LENGTH_LONG).show();
            checkTermos.requestFocus();
            return;
        }

        // Mostra o indicador de carregamento
        progressBarCadastro.setVisibility(View.VISIBLE);
        btnCadastrar.setEnabled(false);
        btnCadastrar.setText("");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBarCadastro.setVisibility(View.GONE);
            btnCadastrar.setEnabled(true);
            btnCadastrar.setText("Registar Conta");

            // Utiliza o mesmo nome de chave "SessaoSeguraPrefs" se estiver a usar encriptação, ou SharedPreferences padrão
            SharedPreferences prefs = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);
            prefs.edit().putString("emailCadastrado", email).putString("senhaCadastrada", senha).apply();

            Toast.makeText(this, "Conta criada com sucesso! Faça login.", Toast.LENGTH_LONG).show();
            finish();
        }, 1500);
    }
}