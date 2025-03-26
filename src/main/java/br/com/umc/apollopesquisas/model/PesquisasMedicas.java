package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "pesquisas_medicas")
public class PesquisasMedicas {

    @Id
    private int id;
    private String nomePesquisa;
    private String descricao;
    private String status;

    // Referência para a classe Pesquisador
    @DBRef
    private Pesquisador pesquisadorResponsavel;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Pesquisador getPesquisadorResponsavel() {
        return pesquisadorResponsavel;
    }

    public void setPesquisadorResponsavel(Pesquisador pesquisadorResponsavel) {
        this.pesquisadorResponsavel = pesquisadorResponsavel;
    }
}
