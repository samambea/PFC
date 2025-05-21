package br.com.umc.apollopesquisas.dto;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;

public class PesquisaComFeedbackDTO {
    private Pesquisa pesquisa;
    private Participacao participacao;
    private boolean feedbackExiste;

    public PesquisaComFeedbackDTO(Pesquisa pesquisa, Participacao participacao, boolean feedbackExiste) {
        this.pesquisa = pesquisa;
        this.participacao = participacao;
        this.feedbackExiste = feedbackExiste;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public Participacao getParticipacao() {
        return participacao;
    }

    public boolean isFeedbackExiste() {
        return feedbackExiste;
    }
}
