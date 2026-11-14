package com.example.meucontrole.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView btnSair, btnEditarRenda, txtSaldoDisponivel, txtRendaFixaVar, txtTotalDespesas, txtTotalMetas, txtDicaIA, btnVerTodos;
    private CardView cardDicaIA;
    private RecyclerView recyclerViewGastos;
    private GastoAdapter gastoAdapter;
    private AppDatabase database;
    private SharedPreferences sharedPreferences;

    private double rendaFixa = 2500.0;
    private double rendaVariavel = 1300.0;
    private double totalMetas = 0.0;
    private List<GastoEntity> listaCompletaGastos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar Views
        btnSair = findViewById(R.id.btnSair);
        btnEditarRenda = findViewById(R.id.btnEditarRenda);
        txtSaldoDisponivel = findViewById(R.id.txtSaldoDisponivel);
        txtRendaFixaVar = findViewById(R.id.txtRendaFixaVar);
        txtTotalDespesas = findViewById(R.id.txtTotalDespesas);
        txtTotalMetas = findViewById(R.id.txtTotalMetas);
        txtDicaIA = findViewById(R.id.txtDicaIA);
        cardDicaIA = findViewById(R.id.cardDicaIA);
        btnVerTodos = findViewById(R.id.btnVerTodos);
        recyclerViewGastos = findViewById(R.id.recyclerViewGastos);

        sharedPreferences = getSharedPreferences("SessaoPrefs", MODE_PRIVATE);
        rendaFixa = sharedPreferences.getFloat("rendaFixa", 2500.0f);
        rendaVariavel = sharedPreferences.getFloat("rendaVariavel", 1300.0f);

        database = AppDatabase.getInstance(this);

        // Configurar RecyclerView
        recyclerViewGastos.setLayoutManager(new LinearLayoutManager(this));
        gastoAdapter = new GastoAdapter(new ArrayList<>(), gasto -> {
            new Thread(() -> {
                AppDatabase.getInstance(this).gastoDao().apagar(gasto);
            }).start();
        });
        recyclerViewGastos.setAdapter(gastoAdapter);

        // Observar Room
        database.gastoDao().obterTodosGastos().observe(this, listaGastos -> {
            listaCompletaGastos = listaGastos != null ? listaGastos : new ArrayList<>();
            gastoAdapter.setListaGastos(listaCompletaGastos);
            atualizarInterfaceEstatisticas();
        });

        // Ações de Botões e Logout
        if (btnSair != null) {
            btnSair.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (btnEditarRenda != null) {
            btnEditarRenda.setOnClickListener(v -> mostrarDialogoEditarRenda());
        }

        if (btnVerTodos != null) {
            btnVerTodos.setOnClickListener(v -> {
                startActivity(new Intent(HomeActivity.this, GastosActivity.class));
            });
        }

        // Navegação Inferior Segura
        configurarBarraNavegacaoHome();
    }

    private void configurarBarraNavegacaoHome() {
        if (findViewById(R.id.btnMenuGastos) != null) {
            findViewById(R.id.btnMenuGastos).setOnClickListener(v -> startActivity(new Intent(this, GastosActivity.class)));
        }
        if (findViewById(R.id.btnMenuIa) != null) {
            findViewById(R.id.btnMenuIa).setOnClickListener(v -> startActivity(new Intent(this, IaVozActivity.class)));
        }
        if (findViewById(R.id.btnMenuAdicionar) != null) {
            findViewById(R.id.btnMenuAdicionar).setOnClickListener(v -> startActivity(new Intent(this, AdicionarGastoActivity.class)));
        }
        if (findViewById(R.id.btnMenuAnalises) != null) {
            findViewById(R.id.btnMenuAnalises).setOnClickListener(v -> startActivity(new Intent(this, AnalisesActivity.class)));
        }
        if (findViewById(R.id.btnMenuPerfil) != null) {
            findViewById(R.id.btnMenuPerfil).setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences != null) {
            rendaFixa = sharedPreferences.getFloat("rendaFixa", 2500.0f);
            rendaVariavel = sharedPreferences.getFloat("rendaVariavel", 1300.0f);
            atualizarInterfaceEstatisticas();
        }
    }

    private void mostrarDialogoEditarRenda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✏️ Editar Rendas");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText inputRendaFixa = new EditText(this);
        inputRendaFixa.setHint("Renda Fixa (Ex: 2500.00)");
        inputRendaFixa.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputRendaFixa.setText(String.valueOf(rendaFixa));
        layout.addView(inputRendaFixa);

        final EditText inputRendaVariavel = new EditText(this);
        inputRendaVariavel.setHint("Renda Variável (Ex: 1300.00)");
        inputRendaVariavel.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputRendaVariavel.setText(String.valueOf(rendaVariavel));
        layout.addView(inputRendaVariavel);

        builder.setView(layout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            try {
                String strFixa = inputRendaFixa.getText().toString().trim();
                String strVar = inputRendaVariavel.getText().toString().trim();

                rendaFixa = strFixa.isEmpty() ? 0.0 : Double.parseDouble(strFixa.replace(",", "."));
                rendaVariavel = strVar.isEmpty() ? 0.0 : Double.parseDouble(strVar.replace(",", "."));

                sharedPreferences.edit()
                        .putFloat("rendaFixa", (float) rendaFixa)
                        .putFloat("rendaVariavel", (float) rendaVariavel)
                        .apply();

                atualizarInterfaceEstatisticas();
                Toast.makeText(this, "Rendas atualizadas com sucesso! 💰", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, insira valores válidos.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void atualizarInterfaceEstatisticas() {
        String simboloMoeda = sharedPreferences.getString("moedaApp", "R$");

        double totalGastos = 0.0;
        for (GastoEntity g : listaCompletaGastos) {
            totalGastos += g.getValor();
        }

        double rendaTotal = rendaFixa + rendaVariavel;
        double saldoLiquido = rendaTotal - totalGastos - totalMetas;

        if (txtSaldoDisponivel != null) txtSaldoDisponivel.setText(String.format("%s %.2f", simboloMoeda, saldoLiquido));
        if (txtRendaFixaVar != null) txtRendaFixaVar.setText(String.format("%s %.0f / %s %.0f", simboloMoeda, rendaFixa, simboloMoeda, rendaVariavel));
        if (txtTotalDespesas != null) txtTotalDespesas.setText(String.format("%s %.2f", simboloMoeda, totalGastos));
        if (txtTotalMetas != null) txtTotalMetas.setText(String.format("%s %.2f", simboloMoeda, totalMetas));

        if (txtDicaIA != null) {
            if (saldoLiquido < 0) {
                txtDicaIA.setText("🚨 Alerta crítico da IA: Suas finanças estão no vermelho!");
                if (cardDicaIA != null) {
                    cardDicaIA.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
                    txtDicaIA.setTextColor(Color.parseColor("#991B1B"));
                }
            } else if (saldoLiquido < (rendaTotal * 0.1)) {
                txtDicaIA.setText("⚠️ Alerta da IA: Orçamento extremamente apertado!");
                if (cardDicaIA != null) {
                    cardDicaIA.setCardBackgroundColor(Color.parseColor("#FEF3C7"));
                    txtDicaIA.setTextColor(Color.parseColor("#92400E"));
                }
            } else if (totalGastos > (rendaTotal * 0.6)) {
                txtDicaIA.setText("📊 Atenção da IA: Suas despesas estão pesando bastante.");
                if (cardDicaIA != null) {
                    cardDicaIA.setCardBackgroundColor(Color.parseColor("#FEF3C7"));
                    txtDicaIA.setTextColor(Color.parseColor("#92400E"));
                }
            } else {
                txtDicaIA.setText("✨ Dica da IA: Orçamento sob controle! Continue assim.");
                if (cardDicaIA != null) {
                    cardDicaIA.setCardBackgroundColor(Color.parseColor("#DCFCE7"));
                    txtDicaIA.setTextColor(Color.parseColor("#166534"));
                }
            }
        }
    }
}