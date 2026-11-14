package com.example.meucontrole.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.AppDatabase;
import com.example.meucontrole.data.local.GastoEntity;
import java.util.ArrayList;
import java.util.List;

public class GastosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGastosFiltro;
    private GastoAdapter gastoAdapter;
    private TextView txtResumoCabecalho;

    private TextView filtroTodas, filtroAlimentacao, filtroTransporte, filtroLazer;
    private TextView filtroSaude, filtroEducacao, filtroMoradia, filtroVestuario, filtroOutros;

    private List<GastoEntity> listaCompleta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);

        txtResumoCabecalho = findViewById(R.id.txtResumoCabecalho);
        recyclerViewGastosFiltro = findViewById(R.id.recyclerViewGastosFiltro);

        filtroTodas = findViewById(R.id.filtroTodas);
        filtroAlimentacao = findViewById(R.id.filtroAlimentacao);
        filtroTransporte = findViewById(R.id.filtroTransporte);
        filtroLazer = findViewById(R.id.filtroLazer);
        filtroSaude = findViewById(R.id.filtroSaude);
        filtroEducacao = findViewById(R.id.filtroEducacao);
        filtroMoradia = findViewById(R.id.filtroMoradia);
        filtroVestuario = findViewById(R.id.filtroVestuario);
        filtroOutros = findViewById(R.id.filtroOutros);

        recyclerViewGastosFiltro.setLayoutManager(new LinearLayoutManager(this));
        gastoAdapter = new GastoAdapter(new ArrayList<>(), gasto -> {
            new Thread(() -> AppDatabase.getInstance(this).gastoDao().apagar(gasto)).start();
        });
        recyclerViewGastosFiltro.setAdapter(gastoAdapter);

        AppDatabase.getInstance(this).gastoDao().obterTodosGastos().observe(this, gastos -> {
            listaCompleta = gastos != null ? gastos : new ArrayList<>();
            atualizarListaFiltrada("Todas");
        });

        if (filtroTodas != null) filtroTodas.setOnClickListener(v -> atualizarListaFiltrada("Todas"));
        if (filtroAlimentacao != null) filtroAlimentacao.setOnClickListener(v -> atualizarListaFiltrada("Alimentação"));
        if (filtroTransporte != null) filtroTransporte.setOnClickListener(v -> atualizarListaFiltrada("Transporte"));
        if (filtroLazer != null) filtroLazer.setOnClickListener(v -> atualizarListaFiltrada("Lazer"));
        if (filtroSaude != null) filtroSaude.setOnClickListener(v -> atualizarListaFiltrada("Saúde"));
        if (filtroEducacao != null) filtroEducacao.setOnClickListener(v -> atualizarListaFiltrada("Educação"));
        if (filtroMoradia != null) filtroMoradia.setOnClickListener(v -> atualizarListaFiltrada("Moradia"));
        if (filtroVestuario != null) filtroVestuario.setOnClickListener(v -> atualizarListaFiltrada("Vestuário"));
        if (filtroOutros != null) filtroOutros.setOnClickListener(v -> atualizarListaFiltrada("Outros"));

        configurarBarraNavegacao();
    }

    private void atualizarListaFiltrada(String categoria) {
        double total = 0.0;
        List<GastoEntity> filtrada = new ArrayList<>();

        for (GastoEntity g : listaCompleta) {
            total += g.getValor();
            if (categoria.equals("Todas") || (g.getCategoria() != null && g.getCategoria().equalsIgnoreCase(categoria))) {
                filtrada.add(g);
            }
        }

        if (gastoAdapter != null) gastoAdapter.setListaGastos(filtrada);
        if (txtResumoCabecalho != null) {
            txtResumoCabecalho.setText(String.format("Extrato Completo · Total: R$ %.2f", total));
        }
    }

    private void configurarBarraNavegacao() {
        if (findViewById(R.id.btnMenuInicio) != null) {
            findViewById(R.id.btnMenuInicio).setOnClickListener(v -> {
                startActivity(new Intent(this, HomeActivity.class));
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
        if (findViewById(R.id.btnMenuPerfil) != null) {
            findViewById(R.id.btnMenuPerfil).setOnClickListener(v -> {
                startActivity(new Intent(this, PerfilActivity.class));
                finish();
            });
        }
    }
}