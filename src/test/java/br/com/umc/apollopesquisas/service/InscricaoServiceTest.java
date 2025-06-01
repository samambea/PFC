package br.com.umc.apollopesquisas.service;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Classe de testes unitários para o serviço InscricaoService
class InscricaoServiceTest {

    // Mock do repositório de pesquisas - simula operações de banco de dados para pesquisas
    @Mock
    private PesquisaRepository pesquisaRepository;

    // Mock do repositório de participações - simula operações de banco de dados para participações
    @Mock
    private ParticipacaoRepository participacaoRepository;

    // Mock do repositório de voluntários - simula operações de banco de dados para voluntários
    @Mock
    private VoluntarioRepository voluntarioRepository;

    // Instância do serviço sendo testado com dependências injetadas
    @InjectMocks
    private InscricaoService inscricaoService;

    // Configuração executada antes de cada teste
    @BeforeEach
    void setUp() {
        // Inicializa os mocks anotados
        MockitoAnnotations.openMocks(this);
    }

    // Testa buscarInscricoesDoPesquisador() - deve retornar lista de InscricaoInfo
    @Test
    void buscarInscricoesDoPesquisador_shouldReturnListOfInscricaoInfo() {
        // Preparação dos dados de teste
        String usuarioId = "pesquisador123";

        // Cria duas pesquisas mock associadas ao pesquisador
        Pesquisa pesquisa1 = new Pesquisa();
        pesquisa1.setPesquisaId("p1");
        pesquisa1.setNomePesquisa("Pesquisa 1");

        Pesquisa pesquisa2 = new Pesquisa();
        pesquisa2.setPesquisaId("p2");
        pesquisa2.setNomePesquisa("Pesquisa 2");

        List<Pesquisa> pesquisas = List.of(pesquisa1, pesquisa2);
        // Configura mock para retornar pesquisas do usuário
        when(pesquisaRepository.findByUsuarioId(usuarioId)).thenReturn(pesquisas);

        // Cria participações mock associadas a cada pesquisa
        Participacao participacao1 = new Participacao();
        participacao1.setUsuarioId("vol1");
        Participacao participacao2 = new Participacao();
        participacao2.setUsuarioId("vol2");
        Participacao participacao3 = new Participacao();
        participacao3.setUsuarioId("vol3");

        // Configura mocks para retornar participações por pesquisa
        when(participacaoRepository.findByPesquisaId("p1")).thenReturn(List.of(participacao1));
        when(participacaoRepository.findByPesquisaId("p2")).thenReturn(List.of(participacao2, participacao3));

        // Cria voluntários mock correspondentes às participações
        Voluntario vol1 = new Voluntario();
        vol1.setNome("Voluntário Um");
        vol1.setEmail("vol1@example.com");
        vol1.setTelefone("1111-1111");

        Voluntario vol2 = new Voluntario();
        vol2.setNome("Voluntário Dois");
        vol2.setEmail("vol2@example.com");
        vol2.setTelefone("2222-2222");

        Voluntario vol3 = new Voluntario();
        vol3.setNome("Voluntário Três");
        vol3.setEmail("vol3@example.com");
        vol3.setTelefone("3333-3333");

        // Configura mocks para retornar voluntários por ID
        when(voluntarioRepository.findById("vol1")).thenReturn(Optional.of(vol1));
        when(voluntarioRepository.findById("vol2")).thenReturn(Optional.of(vol2));
        when(voluntarioRepository.findById("vol3")).thenReturn(Optional.of(vol3));

        // Executa o metodo sendo testado
        List<InscricaoInfo> result = inscricaoService.buscarInscricoesDoPesquisador(usuarioId);

        // Verifica se o resultado contém o número correto de inscrições
        assertEquals(3, result.size());

        // Verifica os dados da primeira inscrição
        InscricaoInfo info1 = result.get(0);
        assertEquals("Voluntário Um", info1.getNomeVoluntario());
        assertEquals("vol1@example.com", info1.getEmail());
        assertEquals("1111-1111", info1.getTelefone());
        assertEquals("Pesquisa 1", info1.getTituloPesquisa());

        // Verifica os dados da segunda inscrição
        InscricaoInfo info2 = result.get(1);
        assertEquals("Voluntário Dois", info2.getNomeVoluntario());
        assertEquals("vol2@example.com", info2.getEmail());
        assertEquals("2222-2222", info2.getTelefone());
        assertEquals("Pesquisa 2", info2.getTituloPesquisa());

        // Verifica os dados da terceira inscrição
        InscricaoInfo info3 = result.get(2);
        assertEquals("Voluntário Três", info3.getNomeVoluntario());
        assertEquals("vol3@example.com", info3.getEmail());
        assertEquals("3333-3333", info3.getTelefone());
        assertEquals("Pesquisa 2", info3.getTituloPesquisa());

        // Verifica se os métodos dos repositórios foram chamados corretamente
        verify(pesquisaRepository).findByUsuarioId(usuarioId);
        verify(participacaoRepository).findByPesquisaId("p1");
        verify(participacaoRepository).findByPesquisaId("p2");
        verify(voluntarioRepository).findById("vol1");
        verify(voluntarioRepository).findById("vol2");
        verify(voluntarioRepository).findById("vol3");
    }

    // Testa buscarInscricoesDoPesquisador() quando voluntário não é encontrado - deve pular o voluntário
    @Test
    void buscarInscricoesDoPesquisador_shouldSkipVoluntarioIfNotFound() {
        // Preparação dos dados de teste
        String usuarioId = "pesquisador123";

        // Cria pesquisa mock
        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("p1");
        pesquisa.setNomePesquisa("Pesquisa 1");

        // Cria participação mock
        Participacao participacao = new Participacao();
        participacao.setUsuarioId("vol1");

        // Configura mocks para retornar dados
        when(pesquisaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(pesquisa));
        when(participacaoRepository.findByPesquisaId("p1")).thenReturn(List.of(participacao));
        // Configura mock para simular que o voluntário não foi encontrado
        when(voluntarioRepository.findById("vol1")).thenReturn(Optional.empty());

        // Executa o metodo sendo testado
        List<InscricaoInfo> result = inscricaoService.buscarInscricoesDoPesquisador(usuarioId);

        // Verifica se o resultado está vazio (voluntário não encontrado deve ser ignorado)
        assertTrue(result.isEmpty());

        // Verifica se os métodos dos repositórios foram chamados
        verify(pesquisaRepository).findByUsuarioId(usuarioId);
        verify(participacaoRepository).findByPesquisaId("p1");
        verify(voluntarioRepository).findById("vol1");
    }
}