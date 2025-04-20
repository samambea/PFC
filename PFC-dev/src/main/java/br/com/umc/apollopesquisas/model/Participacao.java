package br.com.umc.apollopesquisas.model;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "participacoes")
public class Participacao {

    @Id
    private String participacaoId;
    private String usuarioId;
    private StatusParticipacao statusParticipacao;
    private LocalDate dataInscricao;

    public String getParticipacaoId() {
        return participacaoId;
    }

    public void setParticipacaoId(String participacaoId) {
        this.participacaoId = participacaoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public StatusParticipacao getStatusParticipacao() {
        return statusParticipacao;
    }

    public void setStatusParticipacao(StatusParticipacao statusParticipacao) {
        this.statusParticipacao = statusParticipacao;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }
}
