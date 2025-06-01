package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.security.CustomUserDetails;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ParticipacaoControllerTest {

    // Mocks para as dependências do controller, simulando comportamentos sem usar implementações reais
    @Mock
    private ParticipacaoService participacaoService;

    @Mock
    private PesquisaService pesquisaService;

    @Mock
    private RedirectAttributes redirectAttributes;  // Para simular atributos flash para redirecionamento

    @Mock
    private CustomUserDetails customUserDetails;  // Simula dados do usuário autenticado

    // Injeção dos mocks no objeto ParticipacaoController a ser testado
    @InjectMocks
    private ParticipacaoController participacaoController;

    // Inicializa os mocks antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa o caso quando o usuário NÃO está autenticado (objeto user é null)
    @Test
    void participarDaPesquisa_QuandoUsuarioNaoAutenticado_RetornaRedirectLogin() {
        String pesquisaId = "123";

        // Chama o método com usuário null simulando usuário não autenticado
        String result = participacaoController.participarDaPesquisa(pesquisaId, null, redirectAttributes);

        // Verifica se uma mensagem de alerta para login foi adicionada
        verify(redirectAttributes).addFlashAttribute("alertMessage", "Faça login para confirmar sua participação.");

        // Verifica se o método retornou o redirecionamento correto para a página de login
        assertEquals("redirect:/login", result);
    }

    // Testa o caso quando a pesquisa informada não é encontrada (Optional vazio)
    @Test
    void participarDaPesquisa_QuandoPesquisaNaoEncontrada_RetornaRedirectHome() {
        String pesquisaId = "123";

        // Simula usuário autenticado
        when(customUserDetails.getUsuarioId()).thenReturn("userId");
        // Simula pesquisa não encontrada
        when(pesquisaService.buscarPorId(pesquisaId)).thenReturn(Optional.empty());

        // Chama o método com dados simulados
        String result = participacaoController.participarDaPesquisa(pesquisaId, customUserDetails, redirectAttributes);

        // Verifica se mensagem de alerta para pesquisa não encontrada foi adicionada
        verify(redirectAttributes).addFlashAttribute("alertMessage", "Pesquisa não encontrada.");

        // Verifica se retornou redirecionamento para a home
        assertEquals("redirect:/home", result);
    }

    // Testa o caso quando o usuário já está participando da pesquisa
    @Test
    void participarDaPesquisa_QuandoParticipacaoJaExiste_RetornaRedirectHome() {
        String pesquisaId = "123";
        String usuarioId = "userId";

        // Simula usuário autenticado
        when(customUserDetails.getUsuarioId()).thenReturn(usuarioId);
        // Simula pesquisa encontrada
        when(pesquisaService.buscarPorId(pesquisaId)).thenReturn(Optional.of(new Pesquisa()));
        // Simula que a participação já existe para aquele usuário e pesquisa
        when(participacaoService.buscarPorUsuarioEPesquisa(usuarioId, pesquisaId))
                .thenReturn(Optional.of(new Participacao()));

        // Executa o método com os mocks configurados
        String result = participacaoController.participarDaPesquisa(pesquisaId, customUserDetails, redirectAttributes);

        // Verifica se mensagem avisando que já participa foi adicionada
        verify(redirectAttributes).addFlashAttribute("alertMessage", "Você já está participando desta pesquisa.");

        // Verifica se redirecionou para a home
        assertEquals("redirect:/home", result);
    }

    // Testa o caso de sucesso: usuário autenticado, pesquisa encontrada e participação não existente
    @Test
    void participarDaPesquisa_QuandoSucesso_CriaNovaParticipacao() {
        String pesquisaId = "123";
        String usuarioId = "userId";

        // Simula usuário autenticado
        when(customUserDetails.getUsuarioId()).thenReturn(usuarioId);
        // Simula pesquisa encontrada
        when(pesquisaService.buscarPorId(pesquisaId)).thenReturn(Optional.of(new Pesquisa()));
        // Simula que participação não existe para esse usuário e pesquisa
        when(participacaoService.buscarPorUsuarioEPesquisa(usuarioId, pesquisaId))
                .thenReturn(Optional.empty());

        // Captura o objeto Participacao que será passado para o método criar da service
        ArgumentCaptor<Participacao> participacaoCaptor = ArgumentCaptor.forClass(Participacao.class);

        // Executa o método a ser testado
        String result = participacaoController.participarDaPesquisa(pesquisaId, customUserDetails, redirectAttributes);

        // Verifica se o método criar foi chamado com o objeto capturado
        verify(participacaoService).criar(participacaoCaptor.capture());

        // Obtém a participação criada para verificar seus valores
        Participacao participacaoCriada = participacaoCaptor.getValue();

        // Assegura que a participação criada tem os dados esperados
        assertEquals(usuarioId, participacaoCriada.getUsuarioId());
        assertEquals(pesquisaId, participacaoCriada.getPesquisaId());
        assertEquals(StatusParticipacao.INSCRITO, participacaoCriada.getStatusParticipacao());

        // Verifica se a mensagem de sucesso foi adicionada nos atributos flash
        verify(redirectAttributes).addFlashAttribute("alertMessage", "Participação registrada com sucesso.");

        // Verifica se redirecionou para a home após sucesso
        assertEquals("redirect:/home", result);
    }
}
