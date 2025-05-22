package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.model.Pesquisador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


// Controller responsável pelas rotas de autenticação e cadastro.
// Fornece páginas para login, cadastro e verificação de autenticação.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
@RequestMapping("/auth") // Prefixo base para todas as rotas deste controller
public class AuthController {


    // Exibe formulário de cadastro.
    // Cria objeto usuário do tipo pesquisador ou voluntário conforme parâmetro.

    @GetMapping("/cadastro")
    public String cadastro(@RequestParam(name = "tipo", required = false, defaultValue = "voluntario") String tipo, Model model) {
        Usuario usuario;

        // Define tipo de usuário para cadastro
        if ("pesquisador".equals(tipo)) {
            usuario = new Pesquisador();
        } else {
            usuario = new Voluntario();
        }

        // Adiciona objeto usuário ao modelo para o formulário
        model.addAttribute("usuario", usuario);
        return "cadastro";
    }


    // Exibe página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    // Exibe página inicial (home)
    @GetMapping("/")
    public String home() {
        return "index";
    }


    // Verifica se o usuário está autenticado.
    // Retorna 200 OK se autenticado, 401 Unauthorized caso contrário.

    @GetMapping("/logado")
    public ResponseEntity<Void> isLogado(Authentication authentication) {
        return (authentication != null && authentication.isAuthenticated())
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
