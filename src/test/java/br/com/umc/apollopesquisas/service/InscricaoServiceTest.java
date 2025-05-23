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

class InscricaoServiceTest {

    @Mock
    private PesquisaRepository pesquisaRepository;

    @Mock
    private ParticipacaoRepository participacaoRepository;

    @Mock
    private VoluntarioRepository voluntarioRepository;

    @InjectMocks
    private InscricaoService inscricaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buscarInscricoesDoPesquisador_shouldReturnListOfInscricaoInfo() {
        String usuarioId = "pesquisador123";

        // Prepare pesquisas
        Pesquisa pesquisa1 = new Pesquisa();
        pesquisa1.setPesquisaId("p1");
        pesquisa1.setNomePesquisa("Pesquisa 1");

        Pesquisa pesquisa2 = new Pesquisa();
        pesquisa2.setPesquisaId("p2");
        pesquisa2.setNomePesquisa("Pesquisa 2");

        List<Pesquisa> pesquisas = List.of(pesquisa1, pesquisa2);
        when(pesquisaRepository.findByUsuarioId(usuarioId)).thenReturn(pesquisas);

        // Prepare participacoes for each pesquisa
        Participacao participacao1 = new Participacao();
        participacao1.setUsuarioId("vol1");
        Participacao participacao2 = new Participacao();
        participacao2.setUsuarioId("vol2");
        Participacao participacao3 = new Participacao();
        participacao3.setUsuarioId("vol3");

        when(participacaoRepository.findByPesquisaId("p1")).thenReturn(List.of(participacao1));
        when(participacaoRepository.findByPesquisaId("p2")).thenReturn(List.of(participacao2, participacao3));

        // Prepare voluntarios
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

        when(voluntarioRepository.findById("vol1")).thenReturn(Optional.of(vol1));
        when(voluntarioRepository.findById("vol2")).thenReturn(Optional.of(vol2));
        when(voluntarioRepository.findById("vol3")).thenReturn(Optional.of(vol3));

        // Call the method
        List<InscricaoInfo> result = inscricaoService.buscarInscricoesDoPesquisador(usuarioId);

        // Assertions
        assertEquals(3, result.size());

        // Check contents
        InscricaoInfo info1 = result.get(0);
        assertEquals("Voluntário Um", info1.getNomeVoluntario());
        assertEquals("vol1@example.com", info1.getEmail());
        assertEquals("1111-1111", info1.getTelefone());
        assertEquals("Pesquisa 1", info1.getTituloPesquisa());

        InscricaoInfo info2 = result.get(1);
        assertEquals("Voluntário Dois", info2.getNomeVoluntario());
        assertEquals("vol2@example.com", info2.getEmail());
        assertEquals("2222-2222", info2.getTelefone());
        assertEquals("Pesquisa 2", info2.getTituloPesquisa());

        InscricaoInfo info3 = result.get(2);
        assertEquals("Voluntário Três", info3.getNomeVoluntario());
        assertEquals("vol3@example.com", info3.getEmail());
        assertEquals("3333-3333", info3.getTelefone());
        assertEquals("Pesquisa 2", info3.getTituloPesquisa());

        // Verify repository calls
        verify(pesquisaRepository).findByUsuarioId(usuarioId);
        verify(participacaoRepository).findByPesquisaId("p1");
        verify(participacaoRepository).findByPesquisaId("p2");
        verify(voluntarioRepository).findById("vol1");
        verify(voluntarioRepository).findById("vol2");
        verify(voluntarioRepository).findById("vol3");
    }

    @Test
    void buscarInscricoesDoPesquisador_shouldSkipVoluntarioIfNotFound() {
        String usuarioId = "pesquisador123";

        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("p1");
        pesquisa.setNomePesquisa("Pesquisa 1");

        Participacao participacao = new Participacao();
        participacao.setUsuarioId("vol1");

        when(pesquisaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(pesquisa));
        when(participacaoRepository.findByPesquisaId("p1")).thenReturn(List.of(participacao));
        when(voluntarioRepository.findById("vol1")).thenReturn(Optional.empty());

        List<InscricaoInfo> result = inscricaoService.buscarInscricoesDoPesquisador(usuarioId);

        assertTrue(result.isEmpty());

        verify(pesquisaRepository).findByUsuarioId(usuarioId);
        verify(participacaoRepository).findByPesquisaId("p1");
        verify(voluntarioRepository).findById("vol1");
    }
}
