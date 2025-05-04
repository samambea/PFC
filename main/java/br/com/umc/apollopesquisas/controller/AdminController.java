package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "lista-usuarios";
    }


    @GetMapping("/editar-voluntario/{id}")
    public String editarVoluntario(@PathVariable String id, Model model) {
        Optional<Usuario> usuarioOptional = usuarioService.findById(id);
        if (usuarioOptional.isEmpty() || !(usuarioOptional.get() instanceof Voluntario)) {
            return "redirect:/admin/usuarios?error=notfound";
        }

        Voluntario voluntario = (Voluntario) usuarioOptional.get();
        model.addAttribute("usuario", voluntario);
        return "editar-voluntario";
    }

    @PostMapping("/salvar-voluntario")
    public String salvarVoluntario(@ModelAttribute Voluntario voluntario) {
        usuarioService.save(voluntario);
        return "redirect:/admin/usuarios?success=editado";
    }


    @GetMapping("/editar-pesquisador/{id}")
    public String editarPesquisador(@PathVariable String id, Model model) {
        Optional<Usuario> usuarioOptional = usuarioService.findById(id);
        if (usuarioOptional.isEmpty() || !(usuarioOptional.get() instanceof Pesquisador)) {
            return "redirect:/admin/usuarios?error=notfound";
        }

        Pesquisador pesquisador = (Pesquisador) usuarioOptional.get();
        model.addAttribute("usuario", pesquisador);
        return "editar-pesquisador";
    }

    @PostMapping("/salvar-pesquisador")
    public String salvarPesquisador(@ModelAttribute Pesquisador pesquisador) {
        usuarioService.save(pesquisador);
        return "redirect:/admin/usuarios?success=editado";
    }


    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable("id") String id) {
        usuarioService.deleteById(id);
        return "redirect:/admin/usuarios?success=deletado";
    }
}
