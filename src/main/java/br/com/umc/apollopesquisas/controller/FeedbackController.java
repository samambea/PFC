package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ParticipacaoService participacaoService;

    // Exibe o formulário de feedback, se ainda não houver um para a participação
    @GetMapping("/form/{participacaoId}")
    public String mostrarFormularioFeedback(@PathVariable String participacaoId,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        Optional<Participacao> participacaoOpt = participacaoService.buscarPorId(participacaoId);

        if (participacaoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Participação não encontrada.");
            return "redirect:/home";
        }

        Participacao participacao = participacaoOpt.get();

        Feedback feedbackExistente = feedbackService.getFeedbackByParticipacaoId(participacaoId);
        if (feedbackExistente != null && feedbackExistente.getComentario() != null && !feedbackExistente.getComentario().isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Você já enviou um feedback para essa pesquisa.");
            return "redirect:/home";
        }

        Feedback feedback = new Feedback();
        feedback.setParticipacao(participacao);
        model.addAttribute("feedback", feedback);
        model.addAttribute("participacaoId", participacaoId);

        return "feedback"; // Nome da sua view (ex: feedback.html)
    }

    // Processa o envio do formulário de feedback
    @PostMapping
    public String enviarFeedback(@RequestParam("participacaoId") String participacaoId,
                                 @ModelAttribute Feedback feedback,
                                 RedirectAttributes redirectAttributes) {

        Optional<Participacao> participacaoOpt = participacaoService.buscarPorId(participacaoId);

        if (participacaoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Participação não encontrada.");
            return "redirect:/home";
        }

        Participacao participacao = participacaoOpt.get();

        Feedback feedbackExistente = feedbackService.getFeedbackByParticipacaoId(participacaoId);
        if (feedbackExistente != null && feedbackExistente.getComentario() != null && !feedbackExistente.getComentario().isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Você já enviou um feedback para essa pesquisa.");
            return "redirect:/home";
        }

        feedback.setParticipacao(participacao);
        feedbackService.criar(feedback);

        redirectAttributes.addFlashAttribute("alertMessage", "Feedback enviado com sucesso!");
        return "redirect:/home";
    }
}
