package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Participacao;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

}
