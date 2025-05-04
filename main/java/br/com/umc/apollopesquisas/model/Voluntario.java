package br.com.umc.apollopesquisas.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Voluntario extends Usuario {

    private String telefone;
    private String endereco;

    public Voluntario() {
        this.setTipo("voluntario");
        this.setRole("USER");
    }

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
