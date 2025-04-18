package br.com.umc.apollopesquisas.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "voluntarios")
public class Voluntario extends Usuario {


    private String telefone;
    private String endereco;

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
