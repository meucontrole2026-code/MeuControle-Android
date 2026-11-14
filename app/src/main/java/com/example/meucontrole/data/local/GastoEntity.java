package com.example.meucontrole.data.local;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "gastos_tabela")
public class GastoEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String descricao;
    private double valor;
    private String categoria;
    private String tipoPagamento;
    private String data;
    private String horario;

    // Construtor vazio obrigatório para o Room
    public GastoEntity() {
    }

    // Construtor completo com @Ignore para evitar avisos do compilador
    @Ignore
    public GastoEntity(String descricao, double valor, String categoria, String tipoPagamento, String data, String horario) {
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.tipoPagamento = tipoPagamento;
        this.data = data;
        this.horario = horario;
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}