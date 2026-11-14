package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.BackupManager;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Utiliza o armazenamento seguro ou SharedPreferences padrão
        sharedPreferences = getSharedPreferences("SessaoSeguraPrefs", MODE_PRIVATE);

        Button btnExportarBackup = findViewById(R.id.btnExportarBackup);
        Button btnExportarPdf = findViewById(R.id.btnExportarPdf);
        Switch switchBiometria = findViewById(R.id.switchBiometria);
        Switch switchModoEscuro = findViewById(R.id.switchModoEscuro);
        Button btnAjuda = findViewById(R.id.btnAjuda);
        Button btnTermos = findViewById(R.id.btnTermos);
        Button btnPrivacidade = findViewById(R.id.btnPrivacidade);

        // --- 1. MODO CLARO / ESCURO ---
        boolean modoEscuroAtivo = sharedPreferences.getBoolean("modoEscuro", false);
        if (switchModoEscuro != null) {
            switchModoEscuro.setChecked(modoEscuroAtivo);
            switchModoEscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("modoEscuro", isChecked).apply();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        // --- 2. BACKUP E PDF ---
        if (btnExportarBackup != null) {
            btnExportarBackup.setOnClickListener(v -> {
                BackupManager.exportarBancoDeDados(this);
            });
        }

        if (btnExportarPdf != null) {
            btnExportarPdf.setOnClickListener(v -> {
                Toast.makeText(this, "A gerar relatório em PDF...", Toast.LENGTH_SHORT).show();
            });
        }

        // --- 3. BIOMETRIA ---
        if (switchBiometria != null) {
            boolean biometriaAtiva = sharedPreferences.getBoolean("biometriaAtiva", false);
            switchBiometria.setChecked(biometriaAtiva);
            switchBiometria.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("biometriaAtiva", isChecked).apply();
                Toast.makeText(this, "Biometria " + (isChecked ? "ativada" : "desativada"), Toast.LENGTH_SHORT).show();
            });
        }

        // --- 4. AJUDA E SUPORTE ---
        if (btnAjuda != null) {
            btnAjuda.setOnClickListener(v -> enviarEmailSuporte());
        }

        // --- 5. TERMOS DE USO ---
        if (btnTermos != null) {
            btnTermos.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, TermosPrivacidadeActivity.class);
                intent.putExtra("tipo", "termos");
                startActivity(intent);
            });
        }

        // --- 6. POLÍTICA DE PRIVACIDADE ---
        if (btnPrivacidade != null) {
            btnPrivacidade.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, TermosPrivacidadeActivity.class);
                intent.putExtra("tipo", "privacidade");
                startActivity(intent);
            });
        }
    }

    // --- MÉTODOS AUXILIARES FORA DO onCreate ---

    private void enviarEmailSuporte() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:suporte.meucontrole@email.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suporte - Aplicativo MeuControle (ADS)");
        intent.putExtra(Intent.EXTRA_TEXT, "Olá, preciso de ajuda com o seguinte problema:");
        try {
            startActivity(Intent.createChooser(intent, "Enviar e-mail via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Nenhum cliente de e-mail encontrado no dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}