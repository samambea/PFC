package br.com.umc.apollopesquisas.model;

import java.time.LocalDateTime;

public class ParticipacaoViewModel {
    private String id;
    private Pesquisa pesquisa;
    private LocalDateTime dataParticipacao;
    private String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(Pesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

    public LocalDateTime getDataParticipacao() {
        return dataParticipacao;
    }

    public void setDataParticipacao(LocalDateTime dataParticipacao) {
        this.dataParticipacao = dataParticipacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}