package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisador;
import br.com.umc.apollopesquisas.model.Voluntario;
import br.com.umc.apollopesquisas.repository.PesquisadorRepository;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsuarioControllerTest {

    private UsuarioController usuarioController; // Controlador a ser testado
    private VoluntarioRepository voluntarioRepository; // Mock do repositório de voluntários
    private PesquisadorRepository pesquisadorRepository; // Mock do repositório de pesquisadores
    private PasswordEncoder passwordEncoder; // Mock do encoder de senha

   /* // Inicializa os mocks e injeta no controlador antes de cada teste
    @BeforeEach
    public void setupMockUsuarios() {
        voluntarioRepository = Mockito.mock(VoluntarioRepository.class);
        pesquisadorRepository = Mockito.mock(PesquisadorRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        usuarioController = new UsuarioController();
        usuarioController.voluntarioRepository = voluntarioRepository;
        usuarioController.pesquisadorRepository = pesquisadorRepository;
        usuarioController.passwordEncoder = passwordEncoder;
    }*/

    // Testa cadastro de voluntário com sucesso
    @Test
    public void testCadastrarVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário Teste");
        voluntario.setSenha("123");

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        // Simula o retorno do encoder de senha
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        /*// Executa o metodo de cadastro
        String result = usuarioController.cadastrarVoluntario(voluntario, redirectAttributes);

        // Verifica se o encoder foi chamado com a senha correta
        verify(passwordEncoder, times(1)).encode("123");
        // Verifica se o voluntário foi salvo no repositório
        verify(voluntarioRepository, times(1)).save(any(Voluntario.class));
        // Verifica se a mensagem de sucesso foi adicionada nos atributos de redirecionamento
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Cadastro de voluntário realizado com sucesso!");

        // Verifica se o redirecionamento foi para a página de login
        assertEquals("redirect:/auth/login", result);*/
    }

    // Testa cadastro de pesquisador com sucesso
    @Test
    public void testCadastrarPesquisador_Success() {
        Pesquisador pesquisador = new Pesquisador();
        pesquisador.setNome("Pesquisador Teste");
        pesquisador.setSenha("123");

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        // Simula o retorno do encoder de senha
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
/*
        // Executa o metodo de cadastro
        String result = usuarioController.cadastrarPesquisador(pesquisador, redirectAttributes);
*/
        // Verifica se o encoder foi chamado com a senha correta
        verify(passwordEncoder, times(1)).encode("123");
        // Verifica se o pesquisador foi salvo no repositório
        verify(pesquisadorRepository, times(1)).save(any(Pesquisador.class));
        // Verifica se a mensagem de sucesso foi adicionada nos atributos de redirecionamento
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Cadastro de pesquisador realizado com sucesso!");
/*
        // Verifica se o redirecionamento foi para a página de login
        assertEquals("redirect:/auth/login", result);

 */
    }

}
