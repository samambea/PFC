package br.com.umc.apollopesquisas.model;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.enums.StatusPesquisa;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "participacoes")
public class Participacao {

    @Id
    private String participacaoId;
    private String usuarioId;
    private StatusParticipacao statusParticipacao;
    private LocalDateTime dataInscricao;
    private String pesquisaId;
    private Feedback feedback;
    @Transient
    private Pesquisa pesquisa;
    private StatusPesquisa statusPesquisa;



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

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public String getPesquisaId() {
        return pesquisaId;
    }

    public void setPesquisaId(String pesquisaId) {
        this.pesquisaId = pesquisaId;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Participacao() {

    }

    public Participacao(Usuario usuario, Pesquisa pesquisa, LocalDateTime dataHora) {
        this.usuarioId = usuario.getUsuarioId();
        this.pesquisaId = pesquisa.getPesquisaId();
        this.dataInscricao = dataHora;
        this.statusParticipacao = StatusParticipacao.INSCRITO;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(Pesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

    public StatusPesquisa getStatusPesquisa() {
        return statusPesquisa;
    }

    public void setStatusPesquisa(StatusPesquisa statusPesquisa) {
        this.statusPesquisa = statusPesquisa;
    }
}
