package br.com.umc.apollopesquisas.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sugestoes")
public class Sugestao {

    @Id
    private String sugestaoId;
    private String titulo;
    private String descricao;
    private String categoria;
    private LocalDateTime dataCriacao;
    private String email;


    public Sugestao() {
        this.dataCriacao = LocalDateTime.now();
    }

    public String getSugestaoId() {
        return sugestaoId;
    }

    public void setSugestaoId(String sugestaoId) {
        this.sugestaoId = sugestaoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
