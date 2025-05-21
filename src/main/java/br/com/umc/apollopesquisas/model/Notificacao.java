package br.com.umc.apollopesquisas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notificacoes")
public class Notificacao {

    @Id
    private String notificacaoId;
    private String mensagem;
    private String dataNotificacao;

    public String getNotificacaoId() {
        return notificacaoId;
    }

    public void setNotificacaoId(String notificacaoId) {
        this.notificacaoId = notificacaoId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDataNotificacao() {
        return dataNotificacao;
    }

    public void setDataNotificacao(String dataNotificacao) {
        this.dataNotificacao = dataNotificacao;
    }
}