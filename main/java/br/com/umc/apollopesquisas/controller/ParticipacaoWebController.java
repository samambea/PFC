package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ParticipacaoWebController {

    @Autowired
    private ParticipacaoService participacaoService;

    @GetMapping("/participacoes")
    public String listarMinhasParticipacoes(Model model, Principal principal) {
        String usuarioId = principal.getName();
        List<Participacao> participacoes = participacaoService.buscarPorUsuarioId(usuarioId);

        model.addAttribute("participacoes", participacoes);
        return "participacoes";
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



}
