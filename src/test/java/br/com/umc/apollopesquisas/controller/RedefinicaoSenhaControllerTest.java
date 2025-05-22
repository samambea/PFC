package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RedefinicaoSenhaControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder; // Mock do encoder de senha

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private RedefinicaoSenhaController redefinicaoSenhaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void formEsqueciSenha_VisualizacaoCorreta() {
        String viewName = redefinicaoSenhaController.formEsqueciSenha();
        assertEquals("esqueci-senha", viewName);
    }

    @Test
    void enviarEmail_UsuarioExistente_EnvioDoEmailComToken() {
        String email = "test@example.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        String viewName = redefinicaoSenhaController.enviarEmail(email, model);

        verify(usuarioRepository).findByEmail(email);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(emailService).enviarLinkRedefinicao(eq(email), anyString());
        verify(model).addAttribute("mensagem", "Link enviado para seu e-mail.");
        assertEquals("esqueci-senha", viewName);
    }

    @Test
    void enviarEmail_UsuarioInexistente_ErroNoEnvio() {
        String email = "nonexistent@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        String viewName = redefinicaoSenhaController.enviarEmail(email, model);

        verify(usuarioRepository).findByEmail(email);
        verify(model).addAttribute("mensagem", "E-mail não encontrado.");
        assertEquals("esqueci-senha", viewName);
    }

    @Test
    void formRedefinirSenha_AdicionarToken() {
        String token = "valid-token";

        String viewName = redefinicaoSenhaController.formRedefinirSenha(token, model);

        verify(model).addAttribute("token", token);
        assertEquals("redefinir-senha", viewName);
    }

    @Test
    void salvarNovaSenha_TokenValido_FazUpdate() {
        String token = "valid-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "newPassword123";
        
        Usuario usuario = new Usuario();
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(10));
        
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        
        when(usuarioRepository.findByResetToken(token)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(novaSenha)).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);
        
        verify(usuarioRepository).findByResetToken(token);
        verify(passwordEncoder).encode(novaSenha);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        verify(redirectAttributes).addFlashAttribute("sucesso", "Senha alterada com sucesso!");
        
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertNull(savedUsuario.getResetToken());
        assertNull(savedUsuario.getTokenExpiration());
        assertEquals("encodedPassword", savedUsuario.getSenha());
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void salvarNovaSenha_TokenExpirado_ErroNoUpdate() {
        String token = "expired-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "newPassword123";
        String originalPassword = "oldPassword";
        
        Usuario usuario = new Usuario();
        usuario.setResetToken(token);
        usuario.setSenha(originalPassword);
        usuario.setTokenExpiration(LocalDateTime.of(2024, 1, 1, 12, 0));
        
        when(usuarioRepository.findByResetToken(token)).thenReturn(Optional.of(usuario));
        
        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);
        
        verify(redirectAttributes).addFlashAttribute("erro", "Token expirado.");
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("redirect:/esqueci-senha", viewName);
        assertEquals(originalPassword, usuario.getSenha());
        assertEquals(token, usuario.getResetToken());
        assertNotNull(usuario.getTokenExpiration());
    }

    @Test
    void salvarNovaSenha_TokenExpirado_Erro() {
        String token = "invalid-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "newPassword123";
        
        when(usuarioRepository.findByResetToken(token)).thenReturn(Optional.empty());

        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("erro", "Token inválido.");
        assertEquals("redirect:/esqueci-senha", viewName);
    }

    @Test
    void salvarNovaSenha_SenhaNaoCoincide_Erro() {
        String token = "valid-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "differentPassword123";

        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("erro", "As senhas não coincidem.");
        assertEquals("redirect:/redefinir-senha?token=" + token, viewName);
    }
}