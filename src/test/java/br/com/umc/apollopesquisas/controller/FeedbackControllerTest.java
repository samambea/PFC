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

    @InjectMocks
    private FeedbackController controller;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private ParticipacaoService participacaoService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- mostrarFormularioFeedback Tests ---

    @Test
    void mostrarFormularioFeedback_participacaoNaoEncontrada() {
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.empty());

        String view = controller.mostrarFormularioFeedback("1", model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), anyString());
        assertEquals("redirect:/home", view);
    }

    @Test
    void mostrarFormularioFeedback_feedbackJaEnviado() {
        Participacao p = new Participacao();
        Feedback f = new Feedback();
        f.setComentario("Já enviado");

        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(f);

        String view = controller.mostrarFormularioFeedback("1", model, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), contains("já enviou"));
        assertEquals("redirect:/home", view);
    }

    @Test
    void mostrarFormularioFeedback_formularioExibido() {
        Participacao p = new Participacao();

        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(null);

        String view = controller.mostrarFormularioFeedback("1", model, redirectAttributes);

        verify(model).addAttribute(eq("feedback"), any(Feedback.class));
        verify(model).addAttribute("participacaoId", "1");
        assertEquals("feedback", view);
    }

    // --- enviarFeedback Tests ---

    @Test
    void enviarFeedback_participacaoNaoEncontrada() {
        Feedback feedback = new Feedback();
        when(participacaoService.buscarPorId("1")).thenReturn(Optional.empty());

        String view = controller.enviarFeedback("1", feedback, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), anyString());
        assertEquals("redirect:/home", view);
    }

    @Test
    void enviarFeedback_feedbackJaEnviado() {
        Participacao p = new Participacao();
        Feedback f = new Feedback();
        f.setComentario("Já enviado");

        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(f);

        Feedback feedback = new Feedback();
        String view = controller.enviarFeedback("1", feedback, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), contains("já enviou"));
        assertEquals("redirect:/home", view);
    }

    @Test
    void enviarFeedback_feedbackSalvoComSucesso() {
        Participacao p = new Participacao();

        when(participacaoService.buscarPorId("1")).thenReturn(Optional.of(p));
        when(feedbackService.getFeedbackByParticipacaoId("1")).thenReturn(null);

        Feedback feedback = new Feedback();
        String view = controller.enviarFeedback("1", feedback, redirectAttributes);

        verify(feedbackService).criar(any(Feedback.class));
        verify(redirectAttributes).addFlashAttribute(eq("alertMessage"), contains("sucesso"));
        assertEquals("redirect:/home", view);
    }
}

