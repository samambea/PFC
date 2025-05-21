package br.com.umc.apollopesquisas.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    @GetMapping("/home-pesquisador")
    public String homePesquisador() {
        return "home-pesquisador";
    }

    @GetMapping("/pesquisas/criar")
    public String criarPesquisaForm() {
        return "form-pesquisa";
    }
}
