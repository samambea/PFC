package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class ParticipacaoWebControllerTest {

    // Mock do servico de participacao para simular a logica sem chamar o servico real
    @Mock
    private ParticipacaoService participacaoService;

    // Mock do Principal que representa o usuario autenticado no contexto da aplicacao
    @Mock
    private Principal principal;

    // Mock para manipular atributos de redirecionamento com mensagens flash
    @Mock
    private RedirectAttributes redirectAttributes;

    // Controlador que sera testado, com as dependencias injetadas automaticamente pelos mocks acima
    @InjectMocks
    private ParticipacaoController participacaoController;

    // Inicializa os mocks antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa o fluxo de participacao com sucesso
    @Test
    void participar_QuandoSucesso_RetornaRedirectHome() {
        String pesquisaId = "123";           // ID da pesquisa para participar
        String email = "user@example.com";   // Email simulado do usuario autenticado

        // Simula que o principal retorna o email do usuario autenticado
        when(principal.getName()).thenReturn(email);

        // Executa o metodo participar do controller
        String result = participacaoController.participar(pesquisaId, principal, redirectAttributes);

        // Verifica se o servico foi chamado para registrar a participacao
        verify(participacaoService).registrarParticipacao(pesquisaId, email);

        // Verifica se a mensagem de sucesso foi adicionada nos atributos para o redirecionamento
        verify(redirectAttributes).addFlashAttribute("alertMessage",
                "Participação confirmada! Em breve, o pesquisador responsavel entrara em contato.");

        // Verifica se o metodo retornou o redirecionamento correto para a pagina inicial (home)
        assertEquals("redirect:/home", result);
    }

    // Testa o fluxo quando ocorre um erro ao registrar a participacao
    @Test
    void participar_QuandoErro_RetornaRedirectHomeComMensagemDeErro() {
        String pesquisaId = "123";           // ID da pesquisa
        String email = "user@example.com";   // Email simulado do usuario
        String errorMessage = "Erro ao registrar participacao"; // Mensagem de erro simulada

        // Simula o usuario autenticado
        when(principal.getName()).thenReturn(email);

        // Simula que o metodo registrarParticipacao lança uma excecao ao ser chamado
        doThrow(new RuntimeException(errorMessage))
                .when(participacaoService).registrarParticipacao(pesquisaId, email);

        // Executa o metodo participar do controller, que deve tratar o erro
        String result = participacaoController.participar(pesquisaId, principal, redirectAttributes);

        // Verifica se o servico foi chamado
        verify(participacaoService).registrarParticipacao(pesquisaId, email);

        // Verifica se a mensagem de erro foi adicionada nos atributos para o redirecionamento
        verify(redirectAttributes).addFlashAttribute("alertMessage",
                "Erro ao registrar participacao");

        // Verifica se o metodo retornou o redirecionamento para a pagina inicial (home), mesmo apos erro
        assertEquals("redirect:/home", result);
    }

}
