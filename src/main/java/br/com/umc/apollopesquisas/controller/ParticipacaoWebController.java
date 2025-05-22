package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.service.ParticipacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

// Controller responsável por operações web relacionadas à participação em pesquisas.
// Permite confirmar participação via formulário web.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class ParticipacaoWebController {

    // Serviço para operações com participações
    @Autowired
    private ParticipacaoService participacaoService;

    // Processa confirmação de participação em pesquisa.
    // Recebe ID da pesquisa e usuário autenticado.
    @PostMapping("/participar")
    public String participar(@RequestParam String pesquisaId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            // Registra participação para o usuário autenticado
            participacaoService.registrarParticipacao(pesquisaId, principal.getName());
            // Adiciona mensagem de sucesso para exibição após redirecionamento
            redirectAttributes.addFlashAttribute("alertMessage", "Participação confirmada! Em breve, o pesquisador responsável entrará em contato.");
        } catch (RuntimeException e) {
            // Adiciona mensagem de erro para exibição após redirecionamento
            redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
        }
        // Redireciona para a página inicial
        return "redirect:/home";
    }

}
