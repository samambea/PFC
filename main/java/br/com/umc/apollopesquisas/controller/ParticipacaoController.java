package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.enums.StatusParticipacao;
import br.com.umc.apollopesquisas.model.Feedback;
import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.ParticipacaoRepository;
import br.com.umc.apollopesquisas.repository.PesquisaRepository;
import br.com.umc.apollopesquisas.security.CustomUserDetails;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/participacoes")
public class ParticipacaoController {

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

    @PostMapping("/pesquisa/{pesquisaId}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public String participarDaPesquisa(@PathVariable String pesquisaId,
                                       @AuthenticationPrincipal CustomUserDetails usuario,
                                       RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "Faça login para confirmar sua participação.");
            return "redirect:/login";
        }

        String usuarioId = usuario.getUsuarioId();

        Optional<Pesquisa> pesquisa = pesquisaService.buscarPorId(pesquisaId);
        if (pesquisa.isEmpty()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Pesquisa não encontrada.");
            return "redirect:/home";
        }

        Optional<Participacao> existente = participacaoService.buscarPorUsuarioEPesquisa(usuarioId, pesquisaId);
        if (existente.isPresent()) {
            redirectAttributes.addFlashAttribute("alertMessage", "Você já está participando desta pesquisa.");
            return "redirect:/home";
        }

        Participacao nova = new Participacao();
        nova.setUsuarioId(usuarioId);
        nova.setPesquisaId(pesquisaId);
        nova.setStatusParticipacao(StatusParticipacao.INSCRITO);
        nova.setDataInscricao(LocalDateTime.now());

        participacaoService.criar(nova);
        redirectAttributes.addFlashAttribute("alertMessage", "Participação registrada com sucesso.");
        return "redirect:/home";
    }

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

    @GetMapping("/detalhes/{participacaoId}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public String detalhesParticipacao(@PathVariable String participacaoId, Model model) {
        Participacao participacao = participacaoService.buscarPorId(participacaoId)
                .orElseThrow(() -> new RuntimeException("Participação não encontrada"));

        Feedback feedback = feedbackService.getFeedbackByParticipacaoId(participacaoId);
        participacao.setFeedback(feedback);

        model.addAttribute("participacao", participacao);
        return "detalhesParticipacao";
    }

    @GetMapping("/minhas-participacoes")
    public String listarParticipacoes(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String usuarioId = userDetails.getUsuarioId();
        List<Participacao> participacoes = participacaoRepository.findByUsuarioId(usuarioId);


        for (Participacao p : participacoes) {
            Pesquisa pesquisa = pesquisaRepository.findById(p.getPesquisaId()).orElse(null);
            p.setPesquisa(pesquisa);
        }

        model.addAttribute("participacoes", participacoes);
        return "minhas-participacoes";
    }
}
