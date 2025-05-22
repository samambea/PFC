package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

// Controller responsável pelo cadastro, gerenciamento e operações relacionadas aos usuários,
// incluindo voluntários e pesquisadores, além do upload de foto de perfil e listagem de participações.

@Controller
@RequestMapping("/auth")
public class UsuarioController {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private PesquisadorRepository pesquisadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PesquisaService pesquisaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Exibe o formulário de cadastro, permitindo escolher entre pesquisador e voluntário.
    // Cria a instância correta de usuário conforme o parâmetro 'tipo'.
    @GetMapping("/usuarios/cadastro")
    public String cadastroForm(Model model, @RequestParam(name = "tipo", required = false, defaultValue = "voluntario") String tipo) {
        Usuario usuario;
        if ("pesquisador".equals(tipo)) {
            usuario = new Pesquisador();
            usuario.setRole("PESQUISADOR");
        } else {
            usuario = new Voluntario();
            usuario.setRole("VOLUNTARIO");
        }

        model.addAttribute("usuario", usuario);
        return "cadastro";
    }

    // Exibe o formulário específico para cadastro de pesquisador.
    @GetMapping("/cadastro/pesquisador")
    public String cadastroPesquisador(Model model) {
        model.addAttribute("usuario", new Pesquisador());
        return "cadastro-pesquisador";
    }

    // Processa o cadastro de pesquisador.
    // Codifica a senha antes de salvar e adiciona mensagem de sucesso.
    @PostMapping("/cadastro/pesquisador")
    public String cadastrarPesquisador(@ModelAttribute Pesquisador pesquisador, RedirectAttributes redirectAttributes) {
        pesquisador.setSenha(passwordEncoder.encode(pesquisador.getSenha()));
        pesquisadorRepository.save(pesquisador);
        redirectAttributes.addFlashAttribute("successMessage", "Cadastro de pesquisador realizado com sucesso!");
        return "redirect:/auth/login";
    }

    // Exibe o formulário específico para cadastro de voluntário.
    @GetMapping("/cadastro/voluntario")
    public String cadastroVoluntario(Model model) {
        model.addAttribute("usuario", new Voluntario());
        return "cadastro-voluntario";
    }

    // Processa o cadastro de voluntário.
    // Codifica a senha antes de salvar e adiciona mensagem de sucesso.
    @PostMapping("/cadastro/voluntario")
    public String cadastrarVoluntario(@ModelAttribute Voluntario voluntario, RedirectAttributes redirectAttributes) {
        voluntario.setSenha(passwordEncoder.encode(voluntario.getSenha()));
        voluntarioRepository.save(voluntario);
        redirectAttributes.addFlashAttribute("successMessage", "Cadastro de voluntário realizado com sucesso!");
        return "redirect:/auth/login";
    }

    // Exibe a lista de participações do usuário autenticado.
    // Busca as pesquisas nas quais o usuário está participando e as adiciona ao modelo.
    @GetMapping("/participacoes")
    public String minhasParticipacoes(Model model, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);

        if (usuario != null) {
            List<Pesquisa> pesquisas = pesquisaService.listarParticipacoes(usuario.getUsuarioId());
            model.addAttribute("pesquisas", pesquisas);
        }

        return "usuario/participacoes";
    }

    // Processa o upload da foto de perfil do usuário autenticado.
    // Salva a imagem, atualiza o usuário no banco e na sessão, e adiciona mensagens de sucesso ou erro.
    @PostMapping("/upload-foto")
    public String uploadFoto(@RequestParam("fotoPerfil") MultipartFile fotoPerfil,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            String nomeImagem = usuarioService.salvarImagem(fotoPerfil);

            usuario.setImagemPerfil(nomeImagem);
            usuarioRepository.save(usuario);

            session.setAttribute("usuario", usuario);

            redirectAttributes.addFlashAttribute("mensagem", "Foto atualizada com sucesso!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao fazer upload: " + e.getMessage());
        }

        return "redirect:/perfil";
    }
}
