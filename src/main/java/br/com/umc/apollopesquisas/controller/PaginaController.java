package br.com.umc.apollopesquisas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller responsável por rotas estáticas relacionadas ao pesquisador.
// Fornece páginas para home do pesquisador e criação de pesquisas.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class PaginaController {

    // Exibe a página inicial do pesquisador
    @GetMapping("/home-pesquisador")
    public String homePesquisador() {
        return "home-pesquisador";
    }

    // Exibe o formulário para criação de nova pesquisa
    @GetMapping("/pesquisas/criar")
    public String criarPesquisaForm() {
        return "form-pesquisa";
    }
}
