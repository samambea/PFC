package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "voluntarios")
public class Voluntario extends Usuario {

    @Id
    private int voluntarioId;
    private String telefone;
    private String endereco;

    public int getVoluntarioId() {
        return voluntarioId;
    }

    public void setVoluntarioId(int voluntarioId) {
        this.voluntarioId = voluntarioId;
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
    public void participarPesquisa(int pesquisaId) {
        System.out.println("Voluntário " + voluntarioId + " participou da pesquisa " + pesquisaId);
    }

    public void listarParticipacoes(int voluntarioId) {
        System.out.println("Listando pesquisas para o voluntário " + voluntarioId);
    }

    public void listarAgendamentos(int voluntarioId) {
        System.out.println("Listando agendamentos para o voluntário " + voluntarioId);
    }
}
