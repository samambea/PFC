package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsuarioControllerTest {

    private UsuarioController usuarioController; // Controlador a ser testado
    private VoluntarioRepository voluntarioRepository; // Mock do repositório de voluntários
    private PesquisadorRepository pesquisadorRepository; // Mock do repositório de pesquisadores
    private PasswordEncoder passwordEncoder; // Mock do encoder de senha
    private UsuarioService usuarioService;
    private Model model;

    // Inicializa os mocks e injeta no controlador antes de cada teste
    @BeforeEach
    public void setupMockUsuarios() {
        voluntarioRepository = mock(VoluntarioRepository.class);
        pesquisadorRepository = mock(PesquisadorRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        usuarioService = mock(UsuarioService.class);
        model = mock(Model.class);

        usuarioController = new UsuarioController();
        usuarioController.voluntarioRepository = voluntarioRepository;
        usuarioController.pesquisadorRepository = pesquisadorRepository;
        usuarioController.passwordEncoder = passwordEncoder;
        usuarioController.usuarioService = usuarioService;
    }

    // Testa cadastro de voluntário com sucesso
    @Test
    public void testCadastrarVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário Teste");
        voluntario.setEmail("voluntario@test.com");
        voluntario.setSenha("123");

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        // Simula o retorno do encoder de senha
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        // Simula que o email não existe no sistema
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.empty());

        // Executa o método de cadastro com aceitouTermos = true
        String result = usuarioController.cadastrarVoluntario(
                voluntario,
                true,
                redirectAttributes,
                model
        );

        // Verifica se o encoder foi chamado com a senha correta
        verify(passwordEncoder, times(1)).encode("123");
        // Verifica se o serviço de cadastro foi chamado
        verify(usuarioService, times(1)).cadastrarComConfirmacao(any(Voluntario.class));
        // Verifica se a mensagem de sucesso foi adicionada nos atributos de redirecionamento
        verify(redirectAttributes, times(1)).addFlashAttribute(
                "successMessage",
                "Para confirmar sua conta, por favor verifique seu e-mail."
        );

        // Verifica se o redirecionamento foi para a página de login
        assertEquals("redirect:/auth/login", result);
    }

    // Testa cadastro de pesquisador com sucesso
    @Test
    public void testCadastrarPesquisador_Success() {
        Pesquisador pesquisador = new Pesquisador();
        pesquisador.setNome("Pesquisador Teste");
        pesquisador.setEmail("pesquisador@test.com");
        pesquisador.setSenha("123");

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        // Simula o retorno do encoder de senha
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        // Simula que o email não existe no sistema
        when(usuarioService.findByEmail(anyString())).thenReturn(Optional.empty());

        // Executa o método de cadastro com aceitouTermos = true
        String result = usuarioController.cadastrarPesquisador(
                pesquisador,
                true,
                redirectAttributes,
                model
        );

        // Verifica se o encoder foi chamado com a senha correta
        verify(passwordEncoder, times(1)).encode("123");
        // Verifica se o serviço de cadastro foi chamado
        verify(usuarioService, times(1)).cadastrarComConfirmacao(any(Pesquisador.class));
        // Verifica se a mensagem de sucesso foi adicionada nos atributos de redirecionamento
        verify(redirectAttributes, times(1)).addFlashAttribute(
                "successMessage",
                "Para confirmar sua conta, por favor verifique seu e-mail."
        );

        // Verifica se o redirecionamento foi para a página de login
        assertEquals("redirect:/auth/login", result);
    }
}
