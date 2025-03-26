package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pesquisadores")
public class Pesquisador extends Usuario {

    @Id
    private int pesquisadorId;
    private String crm;
    private String especialidade;
    private String areaDePesquisa;

    public int getPesquisadorId() {
        return pesquisadorId;
    }

    public void setPesquisadorId(int pesquisadorId) {
        this.pesquisadorId = pesquisadorId;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getAreaDePesquisa() {
        return areaDePesquisa;
    }

    public void setAreaDePesquisa(String areaDePesquisa) {
        this.areaDePesquisa = areaDePesquisa;
    }

    public void criarPesquisa(Pesquisa pesquisa) {
        System.out.println("Pesquisa " + pesquisa.getTitulo() + " criada com sucesso.");
    }

    public void atualizarPesquisa(Pesquisa pesquisa) {
        System.out.println("Pesquisa " + pesquisa.getTitulo() + " atualizada.");
    }

    public void listarPesquisas(int pesquisadorId) {
        System.out.println("Listando pesquisas do pesquisador: " + pesquisadorId);
    }

    public void deletarPesquisa(Pesquisa pesquisa) {
        System.out.println("Pesquisa " + pesquisa.getTitulo() + " deletada.");
    }

    public void fecharPesquisa(Pesquisa pesquisa) {
        System.out.println("Pesquisa " + pesquisa.getTitulo() + " foi fechada.");
    }

    public void atualizarStatusParticipacao(int participacaoId, StatusParticipacao novoStatus) {
        System.out.println("Status da participação " + participacaoId + " atualizado para: " + novoStatus);
    }

    public void criarAgendamento(Agendamento agendamento) {
        System.out.println("Agendamento criado para a pesquisa: " + agendamento.getPesquisa().getTitulo());
    }

    public void atualizarAgendamento(Agendamento agendamento) {
        System.out.println("Agendamento atualizado para a pesquisa: " + agendamento.getPesquisa().getTitulo());
    }

    public void listarAgendamentos(Pesquisa pesquisa) {
        System.out.println("Listando agendamentos para a pesquisa: " + pesquisa.getTitulo());
    }

    public void cancelarAgendamento(int agendamentoId) {
        System.out.println("Agendamento " + agendamentoId + " cancelado.");
    }
}
