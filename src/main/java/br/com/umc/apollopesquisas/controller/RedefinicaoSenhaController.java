package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.security.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// Controller responsável pelo fluxo de redefinição de senha dos usuários.
// Inclui envio de e-mail com token, validação do token e atualização da senha.

@Controller // Marca como controller MVC que retorna views (páginas HTML)
public class RedefinicaoSenhaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Exibe o formulário para o usuário solicitar a redefinição de senha.
    @GetMapping("/esqueci-senha")
    public String formEsqueciSenha() {
        return "esqueci-senha";
    }

    // Processa o envio do e-mail de redefinição de senha.
    // Gera um token único e o armazena no usuário com validade de 30 minutos.
    // Envia o link com token para o e-mail informado.
    @PostMapping("/esqueci-senha/enviar")
    public String enviarEmail(@RequestParam String email, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(30));
            usuarioRepository.save(usuario);
            emailService.enviarLinkRedefinicao(usuario.getEmail(), token);
            model.addAttribute("mensagem", "Link enviado para seu e-mail.");
        } else {
            model.addAttribute("mensagem", "E-mail não encontrado.");
        }
        return "esqueci-senha";
    }

    // Exibe o formulário para o usuário inserir a nova senha, recebendo o token via parâmetro.
    @GetMapping("/redefinir-senha")
    public String formRedefinirSenha(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "redefinir-senha";
    }

    // Processa a submissão do formulário de nova senha.
    // Valida se as senhas conferem, se o token é válido e não expirou.
    // Atualiza a senha do usuário com hash e limpa o token e sua validade.
    @PostMapping("/redefinir-senha/salvar")
    public String salvarNovaSenha(@RequestParam String token,
                                  @RequestParam String novaSenha,
                                  @RequestParam String confirmarSenha,
                                  RedirectAttributes redirectAttributes) {

        if (!novaSenha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem.");
            return "redirect:/redefinir-senha?token=" + token;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetToken(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getTokenExpiration().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("erro", "Token expirado.");
                return "redirect:/esqueci-senha";
            }

            usuario.setSenha(passwordEncoder.encode(novaSenha));
            usuario.setResetToken(null);
            usuario.setTokenExpiration(null);
            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("erro", "Token inválido.");
            return "redirect:/esqueci-senha";
        }
    }
}
