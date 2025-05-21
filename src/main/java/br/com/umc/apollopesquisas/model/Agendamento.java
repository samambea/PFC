package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "agendamentos")
public class Agendamento {

    @Id

    private String agendamentoId;
    private String pesquisadorEmail;
    private String voluntarioEmail;
    private String tipo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataAgendamento;
    private String localAgendamento;
    private String resultado;
    private String pesquisadorId;
    private String voluntarioId;
    private String nomePesquisa;
    private LocalTime horario;



    public String getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(String agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    public String getPesquisadorEmail() {
        return pesquisadorEmail;
    }

    public void setPesquisadorEmail(String pesquisadorEmail) {
        this.pesquisadorEmail = pesquisadorEmail;
    }

    public String getVoluntarioEmail() {
        return voluntarioEmail;
    }

    public void setVoluntarioEmail(String voluntarioEmail) {
        this.voluntarioEmail = voluntarioEmail;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public String getLocalAgendamento() {
        return localAgendamento;
    }

    public void setLocalAgendamento(String localAgendamento) {
        this.localAgendamento = localAgendamento;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getPesquisadorId() {
        return pesquisadorId;
    }

    public void setPesquisadorId(String pesquisadorId) {
        this.pesquisadorId = pesquisadorId;
    }

    public String getVoluntarioId() {
        return voluntarioId;
    }

    public void setVoluntarioId(String voluntarioId) {
        this.voluntarioId = voluntarioId;
    }

    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }
}
