package br.com.umc.apollopesquisas.controller;

import br.com.umc.apollopesquisas.model.*;
import br.com.umc.apollopesquisas.repository.VoluntarioRepository;
import br.com.umc.apollopesquisas.service.FeedbackService;
import br.com.umc.apollopesquisas.service.ParticipacaoService;
import br.com.umc.apollopesquisas.service.PesquisaService;
import br.com.umc.apollopesquisas.service.VoluntarioService;
import br.com.umc.apollopesquisas.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VoluntarioControllerTest {

    @Mock
    private VoluntarioService voluntarioService;

    @Mock
    private PesquisaService pesquisaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ParticipacaoService participacaoService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private Model model;

    @Mock
    Principal principal;

    @Mock
    private VoluntarioRepository voluntarioRepository; // Mock do repositório de voluntários

    @InjectMocks
    private VoluntarioController voluntarioController; // Controlador a ser testado

    // Inicializa os mocks antes de cada teste
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Testa salvar voluntário com sucesso
    @Test
    void salveVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário Teste");
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);
        Voluntario result = voluntarioController.criar(voluntario);

        assertEquals("Voluntário Teste", result.getNome());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa criação de voluntário com sucesso, incluindo endereço
    @Test
    void criarVoluntario_Success() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Novo Voluntário");
        voluntario.setEndereco("Rua A, 123");

        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

        Voluntario result = voluntarioController.criar(voluntario);

        assertEquals("Novo Voluntário", result.getNome());
        assertEquals("Rua A, 123", result.getEndereco());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa criação de voluntário com nome nulo
    @Test
    void criarVoluntario_NullName() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome(null);

        when(voluntarioRepository.save(any(Voluntario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Voluntario result = voluntarioController.criar(voluntario);

        assertNull(result.getNome());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa falha ao salvar voluntário no repositório (exceção)
    @Test
    void criarVoluntario_RepositoryFailure() {
        Voluntario voluntario = new Voluntario();
        voluntario.setNome("Voluntário com Erro");

        when(voluntarioRepository.save(any(Voluntario.class))).thenThrow(new RuntimeException("Erro no banco"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> voluntarioController.criar(voluntario));

        assertEquals("Erro no banco", thrown.getMessage());
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa busca de voluntário por ID com sucesso
    @Test
    void findVoluntarioById_Sucess() {
        String id = "123";
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(id);
        voluntario.setNome("Voluntário Teste");
        when(voluntarioRepository.findById(id)).thenReturn(java.util.Optional.of(voluntario));

        ResponseEntity<Voluntario> response = voluntarioController.buscarPorId(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(voluntario, response.getBody());
        verify(voluntarioRepository).findById(id);
    }

    // Testa busca de voluntário por ID quando não encontrado
    @Test
    void buscarVoluntarioPorIdNaoEncontrado() {
        String id = "123";
        when(voluntarioRepository.findById(id)).thenReturn(java.util.Optional.empty());

        ResponseEntity<Voluntario> response = voluntarioController.buscarPorId(id);

        assertEquals(404, response.getStatusCodeValue());
        verify(voluntarioRepository).findById(id);
    }

    // Testa listar todos os voluntários com sucesso
    @Test
    void listarTodos_Success() {
        List<Voluntario> voluntarios = new ArrayList<>();
        Voluntario voluntario1 = new Voluntario();
        voluntario1.setUsuarioId("001");
        voluntario1.setNome("Voluntário 1");

        Voluntario voluntario2 = new Voluntario();
        voluntario2.setUsuarioId("002");
        voluntario2.setNome("Voluntário 2");

        voluntarios.add(voluntario1);
        voluntarios.add(voluntario2);

        when(voluntarioRepository.findAll()).thenReturn(voluntarios);

        List<Voluntario> result = voluntarioController.listarTodos();

        assertEquals(2, result.size());
        assertEquals("Voluntário 1", result.get(0).getNome());
        assertEquals("Voluntário 2", result.get(1).getNome());
        verify(voluntarioRepository).findAll();
    }

    // Testa listar todos os voluntários com lista vazia
    @Test
    void listarTodos_EmptyList() {
        when(voluntarioRepository.findAll()).thenReturn(new ArrayList<>());

        List<Voluntario> result = voluntarioController.listarTodos();

        assertTrue(result.isEmpty());
        verify(voluntarioRepository).findAll();
    }

    // Testa atualização de voluntário com sucesso
    @Test
    void atualizarVoluntario_Success() {
        String id = "123";
        Voluntario voluntario = new Voluntario();
        voluntario.setUsuarioId(id);
        voluntario.setNome("Teste Atualizado");
        when(voluntarioRepository.existsById(id)).thenReturn(true);
        when(voluntarioRepository.save(any(Voluntario.class))).thenReturn(voluntario);

        ResponseEntity<Voluntario> response = voluntarioController.atualizar(id, voluntario);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Teste Atualizado", response.getBody().getNome());
        verify(voluntarioRepository).existsById(id);
        verify(voluntarioRepository).save(voluntario);
    }

    // Testa atualização de voluntário quando não encontrado
    @Test
    void atualizarVoluntario_NotFound() {
        String id = "123";
        Voluntario voluntario = new Voluntario();
        when(voluntarioRepository.existsById(id)).thenReturn(false);

        ResponseEntity<Voluntario> response = voluntarioController.atualizar(id, voluntario);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(voluntarioRepository).existsById(id);
    }

    // Testa exclusão de voluntário com sucesso
    @Test
    void deletarVoluntario_Success() {
        String id = "123";
        when(voluntarioRepository.existsById(id)).thenReturn(true);

        ResponseEntity<String> response = voluntarioController.deletar(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Voluntário excluído com sucesso", response.getBody());
        verify(voluntarioRepository).existsById(id);
        verify(voluntarioRepository).deleteById(id);
    }

    // Testa exclusão de voluntário quando não encontrado
    @Test
    void deletarVoluntario_NotFound() {
        String id = "123";
        when(voluntarioRepository.existsById(id)).thenReturn(false);

        ResponseEntity<String> response = voluntarioController.deletar(id);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Voluntário não encontrado", response.getBody());
        verify(voluntarioRepository).existsById(id);
    }

    // Testa sucesso do método homeVoluntario
    @Test
    void homeVoluntario_Success() {
        String emailUsuario = "usuario@teste.com";
        String usuarioId = "111";
        Voluntario voluntario = new Voluntario();
        voluntario.setEmail(emailUsuario);

        when(principal.getName()).thenReturn(emailUsuario);
        when(usuarioService.findByEmail(emailUsuario)).thenReturn(java.util.Optional.of(new Usuario()));

        Participacao participacao = new Participacao();
        participacao.setPesquisaId("222");
        participacao.setParticipacaoId("333");
        when(participacaoService.listarParticipacoes(usuarioId)).thenReturn(List.of(participacao));

        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setPesquisaId("222");
        when(pesquisaService.buscarPesquisaPorId("222")).thenReturn(pesquisa);
        when(feedbackService.getFeedbackByParticipacaoId("333")).thenReturn(new Feedback());

        String response = voluntarioController.homeVoluntario(model, principal);

        assertEquals("home-voluntario", response);
        verify(model).addAttribute(eq("pesquisasComFeedback"), any());
    }

    // Testa falha ao não encontrar o usuário
    @Test
    void homeVoluntario_UserNotFound() {
        String emailUsuario = "invalido@teste.com";

        when(principal.getName()).thenReturn(emailUsuario);
        when(usuarioService.findByEmail(emailUsuario)).thenReturn(java.util.Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> voluntarioController.homeVoluntario(model, principal));

        assertEquals("Usuário não encontrado", thrown.getMessage());
    }

    // Testa ausência de participações no metodo homeVoluntario
    @Test
    void homeVoluntario_MissingParticipations() {
        String emailUsuario = "usuario@teste.com";
        String usuarioId = "111";

        when(principal.getName()).thenReturn(emailUsuario);
        when(usuarioService.findByEmail(emailUsuario)).thenReturn(Optional.of(new Usuario(usuarioId)));

        when(participacaoService.listarParticipacoes(usuarioId)).thenReturn(List.of());

        String response = voluntarioController.homeVoluntario(model, principal);

        assertEquals("home-voluntario", response);
        verify(model).addAttribute(eq("pesquisasComFeedback"), any());
    }
}
