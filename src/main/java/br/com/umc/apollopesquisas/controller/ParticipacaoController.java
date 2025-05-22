package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

// Controller responsável pelo gerenciamento das participações dos voluntários nas pesquisas.
// Permite registrar participação, confirmar participação e visualizar detalhes da participação.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
@RequestMapping("/participacoes") // Prefixo base para todas as rotas deste controller
public class ParticipacaoController {

    // Serviços e repositórios para operações com participações, pesquisas, usuários e feedbacks
    @Autowired
    private ParticipacaoService participacaoService;

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    // Registra participação do voluntário em uma pesquisa específica.
    // Requer autenticação com role VOLUNTARIO.
    @PostMapping("/pesquisa/{pesquisaId}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public String participarDaPesquisa(@PathVariable String pesquisaId,
                                       @AuthenticationPrincipal CustomUserDetails usuario,
                                       RedirectAttributes redirectAttributes) {
        // Verifica se usuário está autenticado
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "Faça login para confirmar sua participação.");
            return "redirect:/login";
        }

        String usuarioId = usuario.getUsuarioId();

        // Busca pesquisa pelo ID
        Optional<Pesquisa> pesquisa = pesquisaService.buscarPorId(pesquisaId);
        if (pesquisa.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Pesquisa não encontrada.");
            return "redirect:/home";
        }

        // Verifica se já existe participação para o usuário e pesquisa
        Optional<Participacao> existente = participacaoService.buscarPorUsuarioEPesquisa(usuarioId, pesquisaId);
        if (existente.isPresent()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Você já está participando desta pesquisa.");
            return "redirect:/home";
        }

        // Cria nova participação com status inscrito e data atual
        Participacao nova = new Participacao();
        nova.setUsuarioId(usuarioId);
        nova.setPesquisaId(pesquisaId);
        nova.setStatusParticipacao(StatusParticipacao.INSCRITO);
        nova.setDataInscricao(LocalDateTime.now());

        // Salva participação
        participacaoService.criar(nova);
        redirectAttributes.addFlashAttribute("alertMessage", "Participação registrada com sucesso.");
        return "redirect:/home";
    }

    // Confirma participação do usuário autenticado em uma pesquisa.
    @PostMapping("/participar")
    public String participar(@RequestParam String pesquisaId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            participacaoService.registrarParticipacao(pesquisaId, principal.getName());
            redirectAttributes.addFlashAttribute("alertMessage", "Participação confirmada! Em breve, o pesquisador responsável entrará em contato.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
        }
        return "redirect:/home";
    }

    // Exibe detalhes de uma participação específica.
    // Requer autenticação com role VOLUNTARIO.
    @GetMapping("/detalhes/{participacaoId}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public String detalhesParticipacao(@PathVariable String participacaoId, Model model) {
        // Busca participação pelo ID
        Participacao participacao = participacaoService.buscarPorId(participacaoId)
                .orElseThrow(() -> new RuntimeException("Participação não encontrada"));

        // Obtém feedback associado à participação
        Feedback feedback = feedbackService.getFeedbackByParticipacaoId(participacaoId);
        participacao.setFeedback(feedback);

        // Adiciona participação ao modelo para exibição
        model.addAttribute("participacao", participacao);
        return "detalhesParticipacao";
    }

}
