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

    private UsuarioController usuarioController;
    private VoluntarioRepository voluntarioRepository;
    private PesquisadorRepository pesquisadorRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setupMockUsuarios() {
        voluntarioRepository = Mockito.mock(VoluntarioRepository.class);
        pesquisadorRepository = Mockito.mock(PesquisadorRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        usuarioController = new UsuarioController();
        usuarioController.voluntarioRepository = voluntarioRepository;
        usuarioController.pesquisadorRepository = pesquisadorRepository;
        usuarioController.passwordEncoder = passwordEncoder;
    }

    @Test
    public void testCadastrarVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário Teste");
        voluntario.setSenha("123");

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        String result = usuarioController.cadastrarVoluntario(voluntario, redirectAttributes);

        verify(passwordEncoder, times(1)).encode("123");
        verify(voluntarioRepository, times(1)).save(any(Voluntario.class));
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Cadastro de voluntário realizado com sucesso!");

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    public void testCadastrarPesquisador_Success() {
        Pesquisador pesquisador = new Pesquisador();
        pesquisador.setNome("Pesquisador Teste");
        pesquisador.setSenha("123");

        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");

        String result = usuarioController.cadastrarPesquisador(pesquisador, redirectAttributes);

        verify(passwordEncoder, times(1)).encode("123");
        verify(pesquisadorRepository, times(1)).save(any(Pesquisador.class));
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Cadastro de pesquisador realizado com sucesso!");

        assertEquals("redirect:/auth/login", result);
    }

}