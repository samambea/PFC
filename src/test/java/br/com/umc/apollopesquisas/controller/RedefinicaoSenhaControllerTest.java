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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RedefinicaoSenhaControllerTest {

    // Mocks para simular as dependências do controller
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder; // Simula o encoder de senha

    @Mock
    private Model model; // Simula o modelo usado na view

    @Mock
    private RedirectAttributes redirectAttributes; // Simula atributos para redirecionamento

    // Injeta os mocks acima no controller que vamos testar
    @InjectMocks
    private RedefinicaoSenhaController redefinicaoSenhaController;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks antes de cada teste
        MockitoAnnotations.openMocks(this);
    }

    // Testa se o método que retorna a página "esqueci-senha" funciona corretamente
    @Test
    void formEsqueciSenha_VisualizacaoCorreta() {
        String viewName = redefinicaoSenhaController.formEsqueciSenha();
        // Espera que o nome da view seja "esqueci-senha"
        assertEquals("esqueci-senha", viewName);
    }

    // Testa o envio de email quando o usuário existe na base
    @Test
    void enviarEmail_UsuarioExistente_EnvioDoEmailComToken() throws Exception {
        String email = "test@example.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        // Quando o repositório procurar pelo email, retorna o usuário criado
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        // Quando salvar, retorna o usuário (simulando persistência)
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Chama o método que envia o email
        String viewName = redefinicaoSenhaController.enviarEmail(email, model);

        // Verifica se o método findByEmail foi chamado com o email correto
        verify(usuarioRepository).findByEmail(email);
        // Verifica se o usuário foi salvo após gerar token
        verify(usuarioRepository).save(any(Usuario.class));
        // Verifica se o email com o link de redefinição foi enviado
        verify(emailService).enviarLinkRedefinicao(eq(email), anyString());
        // Verifica se a mensagem de sucesso foi adicionada ao modelo
        verify(model).addAttribute("mensagem", "Link enviado para seu e-mail.");
        // Verifica se a view retornada é a correta
        assertEquals("esqueci-senha", viewName);
    }

    // Testa o cenário quando o usuário não existe e tenta enviar o email
    @Test
    void enviarEmail_UsuarioInexistente_ErroNoEnvio() {
        String email = "nonexistent@example.com";
        // Simula que o usuário não foi encontrado no repositório
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        String viewName = redefinicaoSenhaController.enviarEmail(email, model);

        // Verifica se tentou buscar o usuário
        verify(usuarioRepository).findByEmail(email);
        // Verifica se foi adicionada mensagem de erro ao modelo
        verify(model).addAttribute("mensagem", "E-mail não encontrado.");
        // A view deve continuar a mesma
        assertEquals("esqueci-senha", viewName);
    }

    // Testa se o formulário para redefinir senha recebe o token corretamente
    @Test
    void formRedefinirSenha_AdicionarToken() {
        String token = "valid-token";

        // Chama o método que retorna a view para redefinir a senha
        String viewName = redefinicaoSenhaController.formRedefinirSenha(token, model);

        // Verifica se o token foi adicionado ao modelo (para ser usado no form)
        verify(model).addAttribute("token", token);
        // Verifica se a view retornada é a correta
        assertEquals("redefinir-senha", viewName);
    }

    // Testa o salvamento da nova senha quando o token é válido
    @Test
    void salvarNovaSenha_TokenValido_FazUpdate() {
        String token = "valid-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "newPassword123";

        Usuario usuario = new Usuario();
        usuario.setResetToken(token);
        // Define que o token ainda está válido por 10 minutos a partir de agora
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(10));

        // Captura o usuário salvo para validar depois
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        // Simula encontrar o usuário pelo token
        when(usuarioRepository.findByResetToken(token)).thenReturn(Optional.of(usuario));
        // Simula o encoder de senha retornando a senha codificada
        when(passwordEncoder.encode(novaSenha)).thenReturn("encodedPassword");
        // Simula o salvamento do usuário
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Chama o método para salvar a nova senha
        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);

        // Verificações importantes
        verify(usuarioRepository).findByResetToken(token);
        verify(passwordEncoder).encode(novaSenha);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        verify(redirectAttributes).addFlashAttribute("sucesso", "Senha alterada com sucesso!");

        // Usuário salvo deve ter token e expiração nulos e senha atualizada
        Usuario savedUsuario = usuarioCaptor.getValue();
        assertNull(savedUsuario.getResetToken());
        assertNull(savedUsuario.getTokenExpiration());
        assertEquals("encodedPassword", savedUsuario.getSenha());
        // A view deve redirecionar para login
        assertEquals("redirect:/login", viewName);
    }

    // Testa quando o token está expirado e não permite atualização
    @Test
    void salvarNovaSenha_TokenExpirado_ErroNoUpdate() {
        String token = "expired-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "newPassword123";
        String originalPassword = "oldPassword";

        Usuario usuario = new Usuario();
        usuario.setResetToken(token);
        usuario.setSenha(originalPassword);
        // Define uma data de expiração passada
        usuario.setTokenExpiration(LocalDateTime.of(2024, 1, 1, 12, 0));

        // Simula encontrar o usuário pelo token
        when(usuarioRepository.findByResetToken(token)).thenReturn(Optional.of(usuario));

        // Tenta salvar a nova senha
        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);

        // Verifica que o redirectAttributes recebeu mensagem de erro de token expirado
        verify(redirectAttributes).addFlashAttribute("erro", "Token expirado.");
        // Verifica que não salvou o usuário (por token expirado)
        verify(usuarioRepository, never()).save(any(Usuario.class));
        // Verifica que não chamou o encoder de senha
        verify(passwordEncoder, never()).encode(anyString());
        // Verifica se o redirect foi para a página de esqueci-senha
        assertEquals("redirect:/esqueci-senha", viewName);
        // O usuário não deve ter alterado a senha ou token
        assertEquals(originalPassword, usuario.getSenha());
        assertEquals(token, usuario.getResetToken());
        assertNotNull(usuario.getTokenExpiration());
    }

    // Testa quando o token não existe (inválido)
    @Test
    void salvarNovaSenha_TokenExpirado_Erro() {
        String token = "invalid-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "newPassword123";

        // Simula que não encontrou usuário pelo token
        when(usuarioRepository.findByResetToken(token)).thenReturn(Optional.empty());

        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);

        // Verifica mensagem de token inválido
        verify(redirectAttributes).addFlashAttribute("erro", "Token inválido.");
        // Verifica se redireciona para página de esqueci-senha
        assertEquals("redirect:/esqueci-senha", viewName);
    }

    // Testa quando as senhas informadas não coincidem
    @Test
    void salvarNovaSenha_SenhaNaoCoincide_Erro() {
        String token = "valid-token";
        String novaSenha = "newPassword123";
        String confirmarSenha = "differentPassword123";

        String viewName = redefinicaoSenhaController.salvarNovaSenha(token, novaSenha, confirmarSenha, redirectAttributes);

        // Verifica se adiciona mensagem de erro de senhas não coincidentes
        verify(redirectAttributes).addFlashAttribute("erro", "As senhas não coincidem.");
        // Verifica se retorna para o formulário de redefinição com token no parâmetro
        assertEquals("redirect:/redefinir-senha?token=" + token, viewName);
    }
}
