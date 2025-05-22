package br.com.umc.apollopesquisas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller responsável por redirecionar todas as rotas front-end para a página index.
// Utilizado para aplicações SPA que carregam a partir de uma única página.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class FrontendController {

    // Captura qualquer rota e retorna a view index
    @GetMapping("/{path}")
    public String index() {
        return "index";
    }

}
