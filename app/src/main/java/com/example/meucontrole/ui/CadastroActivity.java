package com.example.meucontrole.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText inputNome, inputEmailCadastro, inputSenhaCadastro;
    private ProgressBar progressBarCadastro;
    private Button btnCadastrar;
    private CheckBox checkTermos;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_cadastro);

        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar o Firebase.", Toast.LENGTH_LONG).show();
        }

        inputNome = findViewById(R.id.inputNome);
        inputEmailCadastro = findViewById(R.id.inputEmailCadastro);
        inputSenhaCadastro = findViewById(R.id.inputSenhaCadastro);
        progressBarCadastro = findViewById(R.id.progressBarCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        checkTermos = findViewById(R.id.checkTermos);
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

        // Validação rigorosa por Regex (Maiúscula, número e caractere especial)
        String regexSenha = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$";

        if (!senha.matches(regexSenha)) {
            inputSenhaCadastro.setError("A senha precisa conter pelo menos 1 letra maiúscula, 1 número e 1 caractere especial.");
            inputSenhaCadastro.requestFocus();
            return;
        }

        // Validação de Aceite Obrigatório dos Termos de Uso
        if (checkTermos != null && !checkTermos.isChecked()) {
            Toast.makeText(this, "Deve aceitar os Termos de Uso e a Política de Privacidade para continuar.", Toast.LENGTH_LONG).show();
            checkTermos.requestFocus();
            return;
        }

        if (mAuth == null) {
            Toast.makeText(this, "Erro: Firebase não configurado corretamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar progresso
        progressBarCadastro.setVisibility(View.VISIBLE);
        btnCadastrar.setEnabled(false);
        btnCadastrar.setText("");

        // Criar conta no Firebase
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome)
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                                ocultarProgresso();
                                Toast.makeText(this, "Conta criada com sucesso! 🚀", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(CadastroActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                        }
                    } else {
                        ocultarProgresso();
                        String erro = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                        Toast.makeText(this, "Erro ao criar conta: " + erro, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void ocultarProgresso() {
        if (progressBarCadastro != null) progressBarCadastro.setVisibility(View.GONE);
        if (btnCadastrar != null) {
            btnCadastrar.setEnabled(true);
            btnCadastrar.setText("Registar Conta");
        }
    }
}