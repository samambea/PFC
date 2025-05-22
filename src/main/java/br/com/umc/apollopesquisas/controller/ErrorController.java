package br.com.umc.apollopesquisas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


// Controller responsável por tratar erros de acesso negado.
// Exibe página personalizada quando o usuário não tem permissão.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class ErrorController {

    // Exibe página de acesso negado
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
