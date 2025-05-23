package br.com.umc.apollopesquisas.dto;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PesquisaComFeedbackDTOTest {

    @Test
    void constructorAndGetters_shouldReturnCorrectValues() {
        // Arrange: create dummy Pesquisa and Participacao objects
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("pesq123");
        pesquisa.setNomePesquisa("Pesquisa Teste");

        Participacao participacao = new Participacao();
        participacao.setParticipacaoId("part123");
        participacao.setUsuarioId("user123");
        participacao.setPesquisaId("pesq123");

        boolean feedbackExiste = true;

        // Act: create DTO instance
        PesquisaComFeedbackDTO dto = new PesquisaComFeedbackDTO(pesquisa, participacao, feedbackExiste);

        // Assert: verify getters return the same objects and value
        assertSame(pesquisa, dto.getPesquisa(), "Pesquisa should match");
        assertSame(participacao, dto.getParticipacao(), "Participacao should match");
        assertTrue(dto.isFeedbackExiste(), "feedbackExiste should be true");
    }

    @Test
    void feedbackExiste_false_shouldReturnFalse() {
        PesquisaComFeedbackDTO dto = new PesquisaComFeedbackDTO(null, null, false);

        assertFalse(dto.isFeedbackExiste(), "feedbackExiste should be false");
        assertNull(dto.getPesquisa());
        assertNull(dto.getParticipacao());
    }
}
