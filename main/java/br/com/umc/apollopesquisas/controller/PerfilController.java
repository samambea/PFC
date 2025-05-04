package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.dto.EditarPerfilDTO;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String mostrarPerfil(Model model, Principal principal) {
        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);
        return "perfil";  // perfil.html
    }

    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model, Principal principal) {
        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        EditarPerfilDTO editarPerfilDTO = new EditarPerfilDTO();
        editarPerfilDTO.setNome(usuario.getNome());
        model.addAttribute("editarPerfil", editarPerfilDTO);

        return "editar-perfil";  // editar-perfil.html
    }

    @PostMapping("/editar")
    public String processarEdicao(@ModelAttribute("editarPerfil") EditarPerfilDTO editarPerfilDTO,
                                  Model model, Principal principal) {
        Usuario usuario = usuarioService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(editarPerfilDTO.getNome());

        if (editarPerfilDTO.getSenha() != null && !editarPerfilDTO.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(editarPerfilDTO.getSenha()));
        }

        usuarioService.save(usuario);

        model.addAttribute("successMessage", "Perfil atualizado com sucesso!");
        return "redirect:/perfil";
    }
}
