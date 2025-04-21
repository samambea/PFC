package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "agendamentos")
public class Agendamento {

    @Id
    private String agendamentoId;
    private String tipo;
    private LocalDate dataAgendamento;
    private String localAgendamento;
    private String resultado;

    public String getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(String agendamentoId) {
        this.agendamentoId = agendamentoId;
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
}
