package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackControllerTest {

    // Injecao do controller de feedback sob teste com os mocks configurados
    @InjectMocks
    private FeedbackController controller;

    // Mock do servico de feedback para operacoes de persistencia
    @Mock
    private FeedbackService feedbackService;

    // Mock do servico de participacao para buscar participacoes
    @Mock
    private ParticipacaoService participacaoService;

    // Mock do modelo Spring MVC para adicionar atributos na view
    @Mock
    private Model model;

    // Mock dos atributos de redirecionamento para mensagens flash
    @Mock
    private RedirectAttributes redirectAttributes;

    // Configuracao inicial executada antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testes do metodo mostrarFormularioFeedback

    // Testa mostrarFormularioFeedback quando participacao nao e encontrada
    @Test
    void mostrarFormularioFeedback_participacaoNaoEncontrada() {
        // Configura mock para simular participacao nao encontrada
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.empty());

        // Executa o metodo sob teste
        String view = controller.mostrarFormularioFeedback("1", model, redirectAttributes);

        // Verifica que mensagem de erro foi adicionada e redirecionamento ocorre
        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), anyString());
        assertEquals("redirect:/home", view);
    }

    // Testa mostrarFormularioFeedback quando feedback ja foi enviado
    @Test
    void mostrarFormularioFeedback_feedbackJaEnviado() {
        // Prepara dados de teste com feedback ja existente
        Participacao p = new Participacao();
        Feedback f = new Feedback();
        f.setComentario("Já enviado");

        // Configura mocks para simular participacao encontrada e feedback ja existente
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(f);

        // Executa o metodo sob teste
        String view = controller.mostrarFormularioFeedback("1", model, redirectAttributes);

        // Verifica que mensagem informando feedback ja enviado foi adicionada
        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), contains("já enviou"));
        assertEquals("redirect:/home", view);
    }

    // Testa mostrarFormularioFeedback quando formulario deve ser exibido
    @Test
    void mostrarFormularioFeedback_formularioExibido() {
        // Prepara participacao valida para teste
        Participacao p = new Participacao();

        // Configura mocks para participacao encontrada sem feedback existente
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(null);

        // Executa o metodo sob teste
        String view = controller.mostrarFormularioFeedback("1", model, redirectAttributes);

        // Verifica que feedback vazio e participacaoId foram adicionados ao modelo
        verify(model).addAttribute(eq("feedback"), any(Feedback.class));
        verify(model).addAttribute("participacaoId", "1");
        assertEquals("feedback", view);
    }

    // Testes do metodo enviarFeedback

    // Testa enviarFeedback quando participacao nao e encontrada
    @Test
    void enviarFeedback_participacaoNaoEncontrada() {
        // Prepara feedback para teste
        Feedback feedback = new Feedback();

        // Configura mock para simular participacao nao encontrada
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.empty());

        // Executa o metodo sob teste
        String view = controller.enviarFeedback("1", feedback, redirectAttributes);

        // Verifica que mensagem de erro foi adicionada e redirecionamento ocorre
        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), anyString());
        assertEquals("redirect:/home", view);
    }

    // Testa enviarFeedback quando feedback ja foi enviado anteriormente
    @Test
    void enviarFeedback_feedbackJaEnviado() {
        // Prepara dados de teste com feedback ja existente
        Participacao p = new Participacao();
        Feedback f = new Feedback();
        f.setComentario("Já enviado");

        // Configura mocks para simular participacao encontrada e feedback ja existente
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(f);

        // Executa o metodo sob teste
        Feedback feedback = new Feedback();
        String view = controller.enviarFeedback("1", feedback, redirectAttributes);

        // Verifica que mensagem informando feedback ja enviado foi adicionada
        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), contains("já enviou"));
        assertEquals("redirect:/home", view);
    }

    // Testa enviarFeedback quando feedback e salvo com sucesso
    @Test
    void enviarFeedback_feedbackSalvoComSucesso() {
        // Prepara participacao valida para teste
        Participacao p = new Participacao();

        // Configura mocks para participacao encontrada sem feedback existente
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(null);

        // Executa o metodo sob teste
        Feedback feedback = new Feedback();
        String view = controller.enviarFeedback("1", feedback, redirectAttributes);

        // Verifica que feedback foi criado e mensagem de sucesso foi adicionada
        verify(feedbackService).criar(any(Feedback.class));
        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), contains("sucesso"));
        assertEquals("redirect:/home", view);
    }
}