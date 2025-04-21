package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;  // injetando o BCrypt

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam("confirm-password") String confirmPassword,
                                  Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "As senhas não coincidem.");
            return "register";
        }

        if (usuarioRepository.existsByEmail(email)) {
            model.addAttribute("error", "Este e-mail já está cadastrado.");
            return "register";
        }

        Usuario usuario = new Usuario(); 
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(password));  // criptografando a senha

        usuarioService.save(usuario);

        return "redirect:/login";
    }
}