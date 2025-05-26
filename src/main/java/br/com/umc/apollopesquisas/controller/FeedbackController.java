package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.repository.FeedbackRepository;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

// Controller responsável pelo gerenciamento de feedbacks.
// Permite exibir formulário e processar envio de feedbacks relacionados a participações.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
@RequestMapping("/feedbacks") // Prefixo base para todas as rotas deste controller
public class FeedbackController {

    // Serviços para operações com feedbacks e participações
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ParticipacaoService participacaoService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Exibe o formulário de feedback, se ainda não houver um para a participação.
    @GetMapping("/form/{participacaoId}")
    public String mostrarFormularioFeedback(@PathVariable String participacaoId,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        // Busca participação pelo ID
        Optional<Participacao> participacaoOpt = participacaoService.buscarPorId(participacaoId);

        // Validação: redireciona se participação não encontrada
        if (participacaoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Participação não encontrada.");
            return "redirect:/home";
        }

        Participacao participacao = participacaoOpt.get();

        // Verifica se já existe feedback com comentário para essa participação
        Feedback feedbackExistente = feedbackService.getFeedbackByParticipacaoId(participacaoId);
        if (feedbackExistente != null && feedbackExistente.getComentario() != null && !feedbackExistente.getComentario().isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Você já enviou um feedback para essa pesquisa.");
            return "redirect:/home";
        }

        // Prepara novo objeto feedback vinculado à participação
        Feedback feedback = new Feedback();
        feedback.setParticipacao(participacao);
        model.addAttribute("feedback", feedback);
        model.addAttribute("participacaoId", participacaoId);

        // Retorna view do formulário de feedback
        return "feedback";
    }

    // Processa o envio do formulário de feedback.
    @PostMapping
    public String enviarFeedback(@RequestParam("participacaoId") String participacaoId,
                                 @ModelAttribute Feedback feedback,
                                 RedirectAttributes redirectAttributes) {

        // Busca participação pelo ID
        Optional<Participacao> participacaoOpt = participacaoService.buscarPorId(participacaoId);

        // Validação: redireciona se participação não encontrada
        if (participacaoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Participação não encontrada.");
            return "redirect:/home";
        }

        Participacao participacao = participacaoOpt.get();

        // Verifica se já existe feedback com comentário para essa participação
        Feedback feedbackExistente = feedbackService.getFeedbackByParticipacaoId(participacaoId);
        if (feedbackExistente != null && feedbackExistente.getComentario() != null && !feedbackExistente.getComentario().isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Você já enviou um feedback para essa pesquisa.");
            return "redirect:/home";
        }

        // Associa participação ao feedback e salva
        feedback.setParticipacao(participacao);
        feedbackService.criar(feedback);

        // Adiciona mensagem de sucesso e redireciona para home
        redirectAttributes.addFlashAttribute("alertMessage", "Feedback enviado com sucesso!");
        return "redirect:/home";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Feedback> feedbackOpt = feedbackService.buscarPorId(id);
        if (feedbackOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Feedback não encontrado.");
            return "redirect:/feedbacks/listar";
        }
        model.addAttribute("feedback", feedbackOpt.get());
        return "editar-feedback";
    }

    @PostMapping("/editar/{id}")
    public String editarFeedback(@PathVariable String id, @ModelAttribute Feedback feedback, RedirectAttributes redirectAttributes) {
        Feedback atualizado = feedbackService.atualizar(id, feedback);
        if (atualizado != null) {
            redirectAttributes.addFlashAttribute("alertMessage", "Feedback atualizado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("alertMessage", "Erro ao atualizar feedback.");
        }
        return "redirect:/feedbacks/listar";
    }


    @GetMapping("/excluir/{id}")
    public String excluirFeedback(@PathVariable String id, RedirectAttributes redirectAttributes) {
        if (feedbackService.deletar(id)) {
            redirectAttributes.addFlashAttribute("alertMessage", "Feedback excluído com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("alertMessage", "Erro ao excluir feedback.");
        }
        return "redirect:/feedbacks/listar";
    }

    @GetMapping("/listar")
    public String listarTodos(Model model) {
        List<Feedback> feedbacks = feedbackService.listarTodos();
        model.addAttribute("feedbacks", feedbacks);
        return "listar-feedbacks";
    }




}
