package br.com.umc.apollopesquisas.dto;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;

// DTO que encapsula informações combinadas de uma pesquisa, a participação do voluntário nela,
// e um indicador booleano que informa se já existe feedback associado a essa participação.
public class PesquisaComFeedbackDTO {
    private Pesquisa pesquisa;
    private Participacao participacao;
    private boolean feedbackExiste;

    // Construtor que inicializa todos os campos do DTO.
    public PesquisaComFeedbackDTO(Pesquisa pesquisa, Participacao participacao, boolean feedbackExiste) {
        this.pesquisa = pesquisa;
        this.participacao = participacao;
        this.feedbackExiste = feedbackExiste;
    }

    // Retorna a pesquisa associada.
    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    // Retorna a participação do voluntário na pesquisa.
    public Participacao getParticipacao() {
        return participacao;
    }

    // Indica se já existe feedback para esta participação.
    public boolean isFeedbackExiste() {
        return feedbackExiste;
    }
}
