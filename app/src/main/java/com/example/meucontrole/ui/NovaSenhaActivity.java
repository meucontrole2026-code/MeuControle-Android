package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.google.android.material.textfield.TextInputEditText;

public class NovaSenhaActivity extends AppCompatActivity {

    private TextInputEditText inputNovaSenha;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_senha);

        inputNovaSenha = findViewById(R.id.inputNovaSenha);
        Button btnSalvarNovaSenha = findViewById(R.id.btnSalvarNovaSenha);

        sharedPreferences = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);

        btnSalvarNovaSenha.setOnClickListener(v -> salvarNovaSenha());
    }

    private void salvarNovaSenha() {
        String novaSenha = inputNovaSenha.getText() != null ? inputNovaSenha.getText().toString().trim() : "";

        if (novaSenha.isEmpty()) {
            inputNovaSenha.setError("Por favor, insira a nova palavra-passe.");
            inputNovaSenha.requestFocus();
            return;
        }

        if (novaSenha.length() < 6) {
            inputNovaSenha.setError("A senha deve ter pelo menos 6 caracteres.");
            inputNovaSenha.requestFocus();
            return;
        }

        // Regex rigorosa: exige mín. 6 chars, 1 letra maiúscula, 1 número e 1 caractere especial
        String regexSenha = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$";

        if (!novaSenha.matches(regexSenha)) {
            inputNovaSenha.setError("A senha precisa conter pelo menos 1 letra maiúscula, 1 número e 1 caractere especial.");
            inputNovaSenha.requestFocus();
            return;
        }

        // Atualiza a senha no SharedPreferences
        sharedPreferences.edit().putString("senhaCadastrada", novaSenha).apply();

        Toast.makeText(this, "Senha alterada com sucesso! Faça login.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(NovaSenhaActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}