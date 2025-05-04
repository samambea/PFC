package br.com.umc.apollopesquisas.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Authentication authentication) {
        String username = authentication.getName(); // nome do usuário logado
        System.out.println("Usuário logado: " + username); // conferir no console

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro-usuario")
    public String cadastroUsuario() {
        return "cadastroUsuario";
    }
}
