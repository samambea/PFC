package br.com.umc.apollopesquisas.dto;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Classe de teste unitario para PesquisaComFeedbackDTO - testa DTO que combina pesquisa, participacao e status de feedback
class PesquisaComFeedbackDTOTest {

    // Testa o construtor e getters - deve retornar valores corretos
    @Test
    void constructorAndGetters_shouldReturnCorrectValues() {
        // Preparacao: cria objetos fictícios de Pesquisa e Participacao
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("pesq123");
        pesquisa.setNomePesquisa("Pesquisa Teste");

        Participacao participacao = new Participacao();
        participacao.setParticipacaoId("part123");
        participacao.setUsuarioId("user123");
        participacao.setPesquisaId("pesq123");

        boolean feedbackExiste = true;

        // Execucao: cria instancia do DTO
        PesquisaComFeedbackDTO dto = new PesquisaComFeedbackDTO(pesquisa, participacao, feedbackExiste);

        // Verificacao: confirma se os getters retornam os mesmos objetos e valor
        assertSame(pesquisa, dto.getPesquisa(), "Pesquisa should match");
        assertSame(participacao, dto.getParticipacao(), "Participacao should match");
        assertTrue(dto.isFeedbackExiste(), "feedbackExiste should be true");
    }

    // Testa feedbackExiste false - deve retornar false e objetos null
    @Test
    void feedbackExiste_false_shouldReturnFalse() {
        // Cria DTO com valores null e feedbackExiste false
        PesquisaComFeedbackDTO dto = new PesquisaComFeedbackDTO(null, null, false);

        // Verifica se feedbackExiste retorna false
        assertFalse(dto.isFeedbackExiste(), "feedbackExiste should be false");
        // Verifica se pesquisa e participacao sao null
        assertNull(dto.getPesquisa());
        assertNull(dto.getParticipacao());
    }
}