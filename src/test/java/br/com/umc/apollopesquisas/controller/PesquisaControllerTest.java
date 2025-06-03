package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.Instituicao;
import br.com.umc.apollopesquisas.model.Pesquisa;
import br.com.umc.apollopesquisas.model.Usuario;
import br.com.umc.apollopesquisas.repository.UsuarioRepository;
import br.com.umc.apollopesquisas.service.InstituicaoService;
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
    private PesquisaService pesquisaService; // Serviço responsável pelas operações de pesquisa

    @Mock
    private UsuarioRepository usuarioRepository; // Repositório para buscar usuários

    @Mock
    private InstituicaoService instituicaoService; // Serviço para listar instituições (ADICIONADO)

    @Mock
    private Authentication authentication; // Simula autenticação do usuário

    @Mock
    private Model model; // Modelo para passagem de atributos para a view

    @InjectMocks
    private PesquisaController pesquisaController; // Controlador a ser testado

    private Pesquisa pesquisa; // Objeto de teste para pesquisa
    private Usuario usuario;   // Objeto de teste para usuário
    private Instituicao instituicao; //Objeto de teste para instituição

    // Inicializa mocks e objetos antes de cada teste
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

        instituicao = new Instituicao();
        instituicao.setInstituicaoId("123");
        instituicao.setNomeInstituicao("Instituição Teste");
        instituicao.setCnpj("123");

        // Mock para instituicaoService.listarTodas() para evitar NullPointerException
        when(instituicaoService.listarTodas()).thenReturn(Arrays.asList(instituicao));
    }

    // Testa a exibição do formulário para nova pesquisa
    @Test
    void novaPesquisaForm_Success() {
        String viewName = pesquisaController.novaPesquisaForm(model);

        assertEquals("form-pesquisa", viewName);
        verify(model).addAttribute(eq("pesquisa"), any(Pesquisa.class));
        verify(model).addAttribute(eq("instituicoes"), anyList());
    }

    // Testa criação de uma nova pesquisa com usuário válido
    @Test
    void criar_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.criar(any(Pesquisa.class))).thenReturn(pesquisa);

        String viewName = pesquisaController.criar(pesquisa, authentication);

        assertEquals("redirect:/pesquisas/minhas", viewName);
        verify(pesquisaService).criar(pesquisa);
    }

    // Testa criação de pesquisa com usuário não encontrado
    @Test
    void criar_UserNotFound() {
        when(authentication.getName()).thenReturn("naoexiste@teste.com");
        when(usuarioRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                pesquisaController.criar(pesquisa, authentication)
        );
    }

    // Testa listagem de todas as pesquisas
    @Test
    void listarTodas_Success() {
        List<Pesquisa> pesquisas = Arrays.asList(pesquisa);
        when(pesquisaService.listarTodas()).thenReturn(pesquisas);

        String viewName = pesquisaController.listarTodas(model);

        assertEquals("listar-pesquisas", viewName);
        verify(model).addAttribute("pesquisas", pesquisas);
    }

    // Testa exibição do formulário de edição para usuário admin
    @Test
    void editarPesquisaForm_AdminSuccess() {
        when(authentication.getName()).thenReturn("admin@teste.com");
        Usuario admin = new Usuario();
        admin.setUsuarioId("admin123");
        admin.setEmail("admin@teste.com");
        admin.setRole("ADMIN");
        when(usuarioRepository.findByEmail("admin@teste.com")).thenReturn(Optional.of(admin));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.editarPesquisaForm("123", model, authentication);

        assertEquals("editar-pesquisa", viewName);
        verify(model).addAttribute("pesquisa", pesquisa);
        verify(model).addAttribute(eq("instituicoes"), anyList());
    }

    // Testa exibição do formulário de edição para pesquisador comum
    @Test
    void editarPesquisaForm_PesquisadorSuccess() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.editarPesquisaForm("123", model, authentication);

        assertEquals("editar-pesquisa-pesquisador", viewName);
        verify(model).addAttribute("pesquisa", pesquisa);
        verify(model).addAttribute(eq("instituicoes"), anyList());
    }

    // Testa atualização de pesquisa por usuário válido
    @Test
    void atualizarPesquisa_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.atualizarPesquisa("123", pesquisa, authentication);

        assertEquals("redirect:/pesquisas/minhas", viewName);
        verify(pesquisaService).atualizar(eq("123"), any(Pesquisa.class));
    }

    // Testa exclusão de pesquisa por usuário válido
    @Test
    void deletar_Success() {
        when(authentication.getName()).thenReturn("teste@teste.com");
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuario));
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        String viewName = pesquisaController.deletar("123", authentication);

        assertEquals("redirect:/pesquisas/minhas", viewName);
        verify(pesquisaService).deletarPorId("123");
    }

    // Testa listagem das pesquisas do pesquisador autenticado
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

    // Testa listagem das pesquisas abertas via API
    @Test
    void listarAbertas_Success() {
        List<Pesquisa> pesquisas = Arrays.asList(pesquisa);
        when(pesquisaService.listarAbertas()).thenReturn(pesquisas);

        List<Pesquisa> result = pesquisaController.listarAbertas();

        assertEquals(pesquisas, result);
        verify(pesquisaService).listarAbertas();
    }

    // Testa busca de pesquisa por ID via API com sucesso
    @Test
    void buscarApiPorId_Success() {
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.of(pesquisa));

        ResponseEntity<Pesquisa> response = pesquisaController.buscarApiPorId("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pesquisa, response.getBody());
    }

    // Testa busca de pesquisa por ID via API quando não encontrada
    @Test
    void buscarApiPorId_NotFound() {
        when(pesquisaService.buscarPorId("123")).thenReturn(Optional.empty());

        ResponseEntity<Pesquisa> response = pesquisaController.buscarApiPorId("123");

        assertEquals(404, response.getStatusCodeValue());
    }

    // Testa exibição das pesquisas disponíveis para o pesquisador autenticado
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
