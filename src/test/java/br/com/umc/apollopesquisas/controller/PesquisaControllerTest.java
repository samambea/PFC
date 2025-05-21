package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.service.PesquisaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PesquisaControllerTest {

    @Mock
    private PesquisaService pesquisaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private PesquisaController pesquisaController;

    private Pesquisa pesquisa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("123");
        pesquisa.setNomePesquisa("Pesquisa Teste");
        pesquisa.setUsuarioId("user123");

        usuario = new Usuario();
        usuario.setUsuarioId("user123");
        usuario.setEmail("teste@teste.com");
        usuario.setRole("PESQUISADOR");
    }

    @Test
    void novaPesquisaForm_Success() {
        String viewName = pesquisaController.novaPesquisaForm(model);
        
        assertEquals("form-pesquisa", viewName);
        verify(model).addAttribute(eq("pesquisa"), any(Pesquisa.class));
    }

    @Test
    void criar_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.criar(any(Pesquisa.class))).thenReturn(pesquisa);

        String viewName = pesquisaController.criar(pesquisa, authentication);

        assertEquals("redirect:/pesquisas/minhas", viewName);
        verify(pesquisaService).criar(pesquisa);
    }

    @Test
    void criar_UserNotFound() {
        when(authentication.getName()).thenReturn("naoexiste@teste.com");
        when(usuarioRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            pesquisaController.criar(pesquisa, authentication)
        );
    }

    @Test
    void listarTodas_Success() {
        List<Pesquisa> pesquisas = Arrays.asList(pesquisa);
        when(pesquisaService.listarTodas()).thenReturn(pesquisas);

        String viewName = pesquisaController.listarTodas(model);

        assertEquals("listar-pesquisas", viewName);
        verify(model).addAttribute("pesquisas", pesquisas);
    }

    @Test
    void editarPesquisaForm_AdminSuccess() {
        when(authentication.getName()).thenReturn("admin@teste.com");
        Usuario admin = new Usuario();
        admin.setRole("ADMIN");
        when(usuarioRepository.findByEmail("admin@teste.com")).thenReturn(Optional.of(admin));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.editarPesquisaForm("123", model, authentication);

        assertEquals("editar-pesquisa", viewName);
        verify(model).addAttribute("pesquisa", pesquisa);
    }

    @Test
    void editarPesquisaForm_PesquisadorSuccess() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.editarPesquisaForm("123", model, authentication);

        assertEquals("editar-pesquisa-pesquisador", viewName);
        verify(model).addAttribute("pesquisa", pesquisa);
    }

    @Test
    void atualizarPesquisa_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.atualizarPesquisa("123", pesquisa, authentication);

        assertEquals("redirect:/pesquisas/minhas", viewName);
        verify(pesquisaService).atualizar(eq("123"), any(Pesquisa.class));
    }

    @Test
    void deletar_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.deletar("123", authentication);

        assertEquals("redirect:/pesquisas/minhas", viewName);
        verify(pesquisaService).deletarPorId("123");
    }

    @Test
    void listarPesquisasDoPesquisador_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        List<Pesquisa> pesquisas = Arrays.asList(pesquisa);
        when(pesquisaService.listarPorUsuarioId("user123")).thenReturn(pesquisas);

        String viewName = pesquisaController.listarPesquisasDoPesquisador(model, authentication);

        assertEquals("minhas-pesquisas", viewName);
        verify(model).addAttribute("pesquisas", pesquisas);
    }

    @Test
    void listarAbertas_Success() {
        List<Pesquisa> pesquisas = Arrays.asList(pesquisa);
        when(pesquisaService.listarAbertas()).thenReturn(pesquisas);

        List<Pesquisa> result = pesquisaController.listarAbertas();

        assertEquals(pesquisas, result);
        verify(pesquisaService).listarAbertas();
    }

    @Test
    void buscarApiPorId_Success() {
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        ResponseEntity<Pesquisa> response = pesquisaController.buscarApiPorId("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pesquisa, response.getBody());
    }

    @Test
    void buscarApiPorId_NotFound() {
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.empty());

        ResponseEntity<Pesquisa> response = pesquisaController.buscarApiPorId("123");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void mostrarPesquisasDisponiveis_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        List<Pesquisa> pesquisas = Arrays.asList(pesquisa);
        when(pesquisaService.findPesquisasDisponiveis("user123")).thenReturn(pesquisas);

        String viewName = pesquisaController.mostrarPesquisasDisponiveis(model, authentication);

        assertEquals("pesquisas", viewName);
        verify(model).addAttribute("pesquisas", pesquisas);
    }
}