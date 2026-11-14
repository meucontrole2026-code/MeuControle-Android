package com.example.meucontrole.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class EsqueciSenhaActivity extends AppCompatActivity {

    private TextInputEditText inputEmailRecuperacao;
    private Button btnEnviarEmail;
    private TextView txtVoltarLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        inputEmailRecuperacao = findViewById(R.id.inputEmailRecuperacao);
        btnEnviarEmail = findViewById(R.id.btnEnviarEmail);
        txtVoltarLogin = findViewById(R.id.txtVoltarLogin);

        // Inicializar a instância do Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnEnviarEmail.setOnClickListener(v -> enviarLinkRecuperacao());
        txtVoltarLogin.setOnClickListener(v -> finish());
    }

    private void enviarLinkRecuperacao() {
        String email = inputEmailRecuperacao.getText() != null ? inputEmailRecuperacao.getText().toString().trim() : "";

        if (email.isEmpty()) {
            inputEmailRecuperacao.setError("Por favor, informe o seu e-mail.");
            inputEmailRecuperacao.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmailRecuperacao.setError("Formato de e-mail inválido.");
            inputEmailRecuperacao.requestFocus();
            return;
        }

        // Executa o envio oficial do link de redefinição de senha via Firebase Auth
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "E-mail de recuperação enviado com sucesso! Verifique a sua caixa de entrada.", Toast.LENGTH_LONG).show();
                        finish(); // Retorna para a tela de login
                    } else {
                        Toast.makeText(this, "Erro: E-mail não encontrado ou não registado no sistema.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}