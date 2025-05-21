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


@Controller
@RequestMapping("/auth")
public class AuthController {


    @GetMapping("/cadastro")
    public String cadastro(@RequestParam(name = "tipo", required = false, defaultValue = "voluntario") String tipo, Model model) {
        Usuario usuario;


        if ("pesquisador".equals(tipo)) {
            usuario = new Pesquisador();
        } else {
            usuario = new Voluntario();
        }

        model.addAttribute("usuario", usuario);
        return "cadastro";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    @GetMapping("/logado")
    public ResponseEntity<Void> isLogado(Authentication authentication) {
        return (authentication != null && authentication.isAuthenticated())
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}


