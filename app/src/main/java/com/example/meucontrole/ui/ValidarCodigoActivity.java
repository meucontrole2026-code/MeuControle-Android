package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meucontrole.R;
import com.google.android.material.textfield.TextInputEditText;

public class ValidarCodigoActivity extends AppCompatActivity {

    private TextInputEditText inputCodigo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_codigo);

        inputCodigo = findViewById(R.id.inputCodigo);
        Button btnConfirmarCodigo = findViewById(R.id.btnConfirmarCodigo);

        sharedPreferences = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);

        btnConfirmarCodigo.setOnClickListener(v -> validarCodigo());
    }

    private void validarCodigo() {
        String codigoDigitado = inputCodigo.getText() != null ? inputCodigo.getText().toString().trim() : "";
        String codigoSalvo = sharedPreferences.getString("codigoRecuperacao", "");

        if (codigoDigitado.isEmpty() || codigoDigitado.length() < 4) {
            inputCodigo.setError("Insira o código completo de 4 dígitos.");
            inputCodigo.requestFocus();
            return;
        }

        if (codigoDigitado.equals(codigoSalvo)) {
            Toast.makeText(this, "Código validado com sucesso!", Toast.LENGTH_SHORT).show();
            // Avança para a tela de redefinição de senha
            Intent intent = new Intent(ValidarCodigoActivity.this, NovaSenhaActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Código incorreto. Tente novamente.", Toast.LENGTH_LONG).show();
            inputCodigo.setError("Código inválido");
            inputCodigo.requestFocus();
        }
    }
}