package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class UsuarioController {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private PesquisadorRepository pesquisadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/usuarios/cadastro")
    public String cadastroForm(Model model, @RequestParam(name = "tipo", required = false, defaultValue = "voluntario") String tipo) {
        Usuario usuario;
        if ("pesquisador".equals(tipo)) {
            usuario = new Pesquisador();
        } else {
            usuario = new Voluntario();
        }

        model.addAttribute("usuario", usuario);
        return "cadastro";
    }


    @GetMapping("/cadastro/pesquisador")
    public String cadastroPesquisador(Model model) {
        model.addAttribute("usuario", new Pesquisador());
        return "cadastro-pesquisador";
    }


    @PostMapping("/cadastro/pesquisador")
    public String cadastrarPesquisador(@ModelAttribute Pesquisador pesquisador, RedirectAttributes redirectAttributes) {
        pesquisador.setSenha(passwordEncoder.encode(pesquisador.getSenha()));
        pesquisadorRepository.save(pesquisador);
        redirectAttributes.addFlashAttribute("successMessage", "Cadastro de pesquisador realizado com sucesso!");
        return "redirect:/auth/login";
    }


    @GetMapping("/cadastro/voluntario")
    public String cadastroVoluntario(Model model) {
        model.addAttribute("usuario", new Voluntario());
        return "cadastro-voluntario";
    }


    @PostMapping("/cadastro/voluntario")
    public String cadastrarVoluntario(@ModelAttribute Voluntario voluntario, RedirectAttributes redirectAttributes) {
        voluntario.setSenha(passwordEncoder.encode(voluntario.getSenha()));
        voluntarioRepository.save(voluntario);
        redirectAttributes.addFlashAttribute("successMessage", "Cadastro de voluntário realizado com sucesso!");
        return "redirect:/auth/login";
    }
}
