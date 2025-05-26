// UsuarioController.java - atualizado

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
import java.util.Optional;

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

    // Formulário genérico com tipo
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

        @GetMapping("/cadastro/pesquisador")
        public String cadastroPesquisador(Model model) {
            model.addAttribute("usuario", new Pesquisador());
            return "cadastro-pesquisador";
        }

    // Processa o cadastro de pesquisador com verificação de termos e e-mail duplicado
    @PostMapping("/cadastro/pesquisador")
    public String cadastrarPesquisador(
            @ModelAttribute Pesquisador pesquisador,
            @RequestParam(name = "aceitouTermos", defaultValue = "false") boolean aceitouTermos,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Verifica se o usuário aceitou os termos
        if (!aceitouTermos) {
            redirectAttributes.addFlashAttribute("errorMessage", "É necessário aceitar os termos de uso.");
            return "redirect:/auth/cadastro/pesquisador";
        }

        // Verifica se o e-mail já está cadastrado
        if (usuarioService.findByEmail(pesquisador.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Este e-mail já está em uso.");
            model.addAttribute("usuario", pesquisador);
            return "cadastro-pesquisador";
        }

        pesquisador.setSenha(passwordEncoder.encode(pesquisador.getSenha()));
        usuarioService.cadastrarComConfirmacao(pesquisador);

        // Mensagem de sucesso após cadastro
        redirectAttributes.addFlashAttribute("successMessage", "Para confirmar sua conta, por favor verifique seu e-mail.");

        return "redirect:/auth/login";
    }


    // Formulário específico de voluntário
    @GetMapping("/cadastro/voluntario")
    public String cadastroVoluntario(Model model) {
        model.addAttribute("usuario", new Voluntario());
        return "cadastro-voluntario";
    }

    // Processa o cadastro de voluntário com verificação de termos
    @PostMapping("/cadastro/voluntario")
    public String cadastrarVoluntario(
            @ModelAttribute Voluntario voluntario,
            @RequestParam(name = "aceitouTermos", defaultValue = "false") boolean aceitouTermos,
            RedirectAttributes redirectAttributes,
            Model model) { // <-- adiciona Model para enviar erros na mesma página

        // Verifica se o usuário aceitou os termos
        if (!aceitouTermos) {
            redirectAttributes.addFlashAttribute("errorMessage", "É necessário aceitar os termos de uso.");
            return "redirect:/auth/cadastro/voluntario";
        }

        // Verifica se o e-mail já está cadastrado
        if (usuarioService.findByEmail(voluntario.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Este e-mail já está em uso.");
            model.addAttribute("usuario", voluntario); // manter dados preenchidos no form
            return "cadastro-voluntario"; // exibe a view com o erro
        }

        voluntario.setSenha(passwordEncoder.encode(voluntario.getSenha()));
        usuarioService.cadastrarComConfirmacao(voluntario);
        redirectAttributes.addFlashAttribute("successMessage", "Para confirmar sua conta, por favor verifique seu e-mail.");
        return "redirect:/auth/login";
    }


    // Exibe participações do usuário logado
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

    // Upload de foto de perfil
    @PostMapping("/upload-foto")
    public String uploadFoto(
            @RequestParam("fotoPerfil") MultipartFile fotoPerfil,
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

    // Confirmação de e-mail
    @GetMapping("/confirmar-email")
    public String confirmarEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        Optional<Usuario> optionalUsuario = usuarioService.confirmarEmailPorToken(token);
        if (optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("successMessage", "E-mail confirmado com sucesso. Faça login.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Token inválido ou expirado.");
        }
        return "redirect:/auth/login";
    }
}
