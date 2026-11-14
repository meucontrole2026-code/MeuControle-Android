package com.example.meucontrole.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meucontrole.R;
import com.example.meucontrole.data.local.GastoEntity;
import java.util.List;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.GastoViewHolder> {

    private List<GastoEntity> listaGastos;
    private final OnGastoLongClickListener listener;

    public interface OnGastoLongClickListener {
        void onGastoLongClick(GastoEntity gasto);
    }

    public GastoAdapter(List<GastoEntity> listaGastos, OnGastoLongClickListener listener) {
        this.listaGastos = listaGastos;
        this.listener = listener;
    }

    public void setListaGastos(List<GastoEntity> novaLista) {
        this.listaGastos = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        GastoEntity gasto = listaGastos.get(position);
        holder.txtDescricao.setText(gasto.getDescricao());
        holder.txtCategoriaTag.setText(gasto.getCategoria());

        String dataHora = (gasto.getData() != null ? gasto.getData() : "") +
                (gasto.getHorario() != null && !gasto.getHorario().isEmpty() ? " · " + gasto.getHorario() : "");
        holder.txtDataHora.setText(dataHora);

        // Tratamento dinâmico para Receitas (+) e Despesas (-)
        String tipo = gasto.getTipo() != null ? gasto.getTipo() : "DESPESA";
        if (tipo.equalsIgnoreCase("RECEITA")) {
            holder.txtValor.setText(String.format("+R$ %.2f", gasto.getValor()));
            holder.txtValor.setTextColor(Color.parseColor("#10B981")); // Verde para entradas
        } else {
            holder.txtValor.setText(String.format("-R$ %.2f", gasto.getValor()));
            holder.txtValor.setTextColor(Color.parseColor("#EF4444")); // Vermelho para despesas
        }

        holder.txtPagamento.setText(gasto.getTipoPagamento());

        // Ícones e emojis inteligentes baseados na categoria ou descrição
        String cat = gasto.getCategoria() != null ? gasto.getCategoria() : "";
        String desc = gasto.getDescricao() != null ? gasto.getDescricao().toLowerCase() : "";

        if (cat.equalsIgnoreCase("Salário/Entrada") || desc.contains("salário") || desc.contains("ganhei") || desc.contains("recebi") || desc.contains("pix")) {
            holder.txtIconeEmoji.setText("💰");
        } else if (cat.equalsIgnoreCase("Alimentação") || desc.contains("food") || desc.contains("burguer") || desc.contains("mercado") || desc.contains("almoço")) {
            holder.txtIconeEmoji.setText("🍔");
        } else if (cat.equalsIgnoreCase("Transporte") || desc.contains("uber") || desc.contains("táxi") || desc.contains("gasolina")) {
            holder.txtIconeEmoji.setText("🚗");
        } else if (cat.equalsIgnoreCase("Saúde") || desc.contains("farmácia") || desc.contains("médico") || desc.contains("remedio")) {
            holder.txtIconeEmoji.setText("💊");
        } else if (cat.equalsIgnoreCase("Lazer") || desc.contains("netflix") || desc.contains("cinema") || desc.contains("jogo")) {
            holder.txtIconeEmoji.setText("🎬");
        } else {
            holder.txtIconeEmoji.setText("🛍️");
        }

        // Long click para remover o gasto
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onGastoLongClick(gasto);
            }
            return true;
        });

        // Clique no botão de deletar
        if (holder.btnDeletar != null) {
            holder.btnDeletar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGastoLongClick(gasto);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaGastos != null ? listaGastos.size() : 0;
    }

    public static class GastoViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescricao, txtCategoriaTag, txtDataHora, txtValor, txtPagamento, txtIconeEmoji;
        View btnDeletar;

        public GastoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            txtCategoriaTag = itemView.findViewById(R.id.txtCategoriaTag);
            txtDataHora = itemView.findViewById(R.id.txtDataHora);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtPagamento = itemView.findViewById(R.id.txtPagamento);
            txtIconeEmoji = itemView.findViewById(R.id.txtIconeEmoji);
            btnDeletar = itemView.findViewById(R.id.btnDeletar);
        }
    }
}